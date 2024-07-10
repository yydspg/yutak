package com.yutak.im.store;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Index {
    public ReentrantLock lock ;
    // means byte location
    public int position;
    // one data unit byte space
    public int entrySize;
    // basic message seq
    public int baseMessageSeq;
    // file max byte size
    public int maxBytes;
    // max entry num
    public int maxEntryNum;
    public int hotEntries; // hot log index
    // real file
    private long totalContentSize;
    private FileChannel fileChannel;
    // memory-mapped
    private MappedByteBuffer mappedByteBuffer;
    // this init is block method,please use carefully
    public Index(String path,int baseMessageSeq) {
        maxBytes = Config.indexMaxSize;
        entrySize = 8;
        maxEntryNum = maxBytes / entrySize;
        hotEntries = 8192 / entrySize;
        this.baseMessageSeq = baseMessageSeq;
        try {
            fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.CREATE, StandardOpenOption.READ,StandardOpenOption.WRITE);
            fileChannel.truncate(maxBytes);
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,fileChannel.size());
            resetPosition();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
    private Entry readEntryAtPosition(int pos) {
        byte[] d = readAtPosition(pos);
        if (d == null) {
            return null;
        }
        return decodeEntry(d);
    }
    private Entry decodeEntry(byte[] d){
        byte[] b1 = new byte[4];
        byte[] b2 = new byte[4];
        if (d == null) {
            return null;
        }
        for (int i = 0; i < d.length; i++) {
            if(i < 4) {
                b1[i] = d[i];
            }else {
                b2[i-4] = d[i];
            }
        }
        Entry e = new Entry();
        e.relativeOffset = Kit.bytesToInt(b1);
        e.position = Kit.bytesToInt(b2);
        return e;
    }
    private void resetPosition() {
        int pos = 0;
        while (pos < totalContentSize) {
            Entry e = readEntryAtPosition(pos);
            if (e != null && e.relativeOffset == 0) {
                break;
            }
            pos += entrySize;
        }
        position = pos;
    }
    private byte[] readAtPosition(int pos) {
        lock.lock();
        try {
            if (pos + 8 >= fileChannel.size()) {
                return null;
            }
            byte[] b = new byte[8];
            mappedByteBuffer.get(pos,b);
            return b;
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally {
            lock.unlock();
        }
        return null;
    }
    public static class Entry {
        public int relativeOffset;
        public int position;
        private byte[] encode() {
            byte[] bytes = new byte[8];
            byte[] b1 = Kit.intToBytes(relativeOffset);
            byte[] b2 = Kit.intToBytes(position);
            for (int i = 0; i < bytes.length; i++) {
                if(i <4) {
                    bytes[i] = b1[i];
                }else {
                    bytes[i] = b2[i-4];
                }
            }
            return bytes;
        }
    }
    public void force(){
        try {
            fileChannel.force(true);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public void close(){
        force();
        try {
            fileChannel.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        mappedByteBuffer.clear();
    }
    /**
     * 文件追加 entry
     * @param offset 偏移
     * @param pos 位置
     */
    public void append(int offset,int pos){
        lock.lock();
        try{
            if (position >=maxEntryNum) {
                log.warn("Index overflow");
            }
            Entry e = new Entry();
            e.relativeOffset = offset - baseMessageSeq;
            e.position = pos;
            mappedByteBuffer.put(position,e.encode());
            position += entrySize;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    public MessageSeqPosition lookup(int targetOffset) {
        lock.lock();
        MessageSeqPosition m = new MessageSeqPosition();
        try {
            long res = indexSlotRange(targetOffset);
            if (res == -1) {
                m.messageSeq = baseMessageSeq;
                m.position =0;
                return m;
            }
            return parseEntry(res);
        }finally {
            lock.unlock();
        }
    }
    public boolean isFull() {
        return position >= maxBytes;
    }

    public MessageSeqPosition lastPosition(){
        lock.lock();
        MessageSeqPosition m = new MessageSeqPosition();
        try {
            if (position == 0) {
                m.messageSeq = baseMessageSeq;
                m.position =0;
                return m;
            }
            return parseEntry(position/entrySize - 1);
        }finally {
            lock.unlock();
        }
    }
    /**
     *
     * @param mid 索引
     * @return message 位置
     */
    public MessageSeqPosition parseEntry(long mid) {
        long pos = mid * entrySize;
        MessageSeqPosition m = new MessageSeqPosition();
        try {
            if (pos + entrySize >mappedByteBuffer.capacity()) {
                m.messageSeq = baseMessageSeq;
                return m;
            }
            byte[] b = new byte[8];
            mappedByteBuffer.get(position,b);
            Entry e = decodeEntry(b);
            m.messageSeq = baseMessageSeq + e.relativeOffset;
            m.position = e.position;
            return m;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    private long indexSlotRange(int target) {
        long num = position / entrySize;
        if (num == 0) {
            return -1;
        }
        /*


    entries-1：首先，entries变量表示索引中条目的总数量。减去1是因为索引通常是0到entries-1，所以entries-1给出了最后一个条目的索引位置。

    idx.warmEntries：这是一个预定义的变量，可能表示被标记为“热”的条目数量。这个值可能是在设计或配置阶段设定的，用于确定多少个最近的条目应被视为“热”。

    entries-1-idx.warmEntries：这部分计算了“热条目”区域的起始位置。从最后一个条目的索引位置entries-1开始，减去idx.warmEntries的数量，就得到了“热条目”区域的开始索引。

    math.Max(0, ...)：这里使用math.Max函数是为了防止entries-1-idx.warmEntries的结果变为负数。如果idx.warmEntries的值大于entries，这会导致结果为负数，而索引位置不能是负数。因此，math.Max(0, ...)确保了firstHotEntry的值至少为0。

    float64(...)和int64(...)转换：在进行数学运算时，使用float64类型可以避免整数运算的溢出问题。计算完成后，将结果转换回int64类型，因为索引位置通常需要是整数。

         */
        long firstHostEntry = Math.max(0,num - 1 - hotEntries);
        long start = parseEntry(firstHostEntry).messageSeq;
        if ( start < target) {
            return binarySearch(start,num -1,target);
        }else {
            return binarySearch(0,start,target);
        }
    }
    private long binarySearch(long  start, long end,int target) {
        long s = start;
        long e = end;
        while (s <= e) {
            long mid = (s + (e - 1) / 2);
            int seq = parseEntry(mid).messageSeq;
            if (seq == target) {
                return mid;
            }else if (seq > target) {
                e = mid - 1;
            }else {
                s = mid + 1;
            }
        }
        if (s == end - 1) {
            return end -1;
        }
        return -1;

    }
    public static class Edge {
        public long s;
        public long e;
    }
    public static class MessageSeqPosition {
        public int messageSeq;
        public long position;
    }
    public static void main(String[] args) {
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            Entry entry = new Entry();
            entry.relativeOffset = r.nextInt() ;
            entry.position = r.nextInt();
            if (entry.position < 0) {
                entry.position = -entry.position;
            }
            if (entry.relativeOffset < 0) {
                entry.relativeOffset = -entry.relativeOffset;
            }
            System.out.println("offset:" + entry.relativeOffset);
            System.out.println("position:" + entry.position);
            System.out.println("current size:"+entry.encode().length);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(r.nextInt());
        }
    }

}
