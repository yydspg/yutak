package com.yutak.im.store;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Index {
    public ReentrantLock lock ;
    public long position;
    public int entrySize;
    public int baseMessageSeq;
    public long maxBytes;
    public long maxEntryNum;
    public long hotEntries; // 热点日志条
    private RandomAccessFile file;
    public long totalContentSize;
    public FileChannel fileChannel;
    public Index(String path,int baseMessageSeq) {
        maxBytes = Config.indexMaxSize;
        entrySize = 8;
        maxEntryNum = maxBytes / entrySize;
        hotEntries = 8192 / entrySize;
        this.baseMessageSeq = baseMessageSeq;
        try {
            file = new RandomAccessFile(path,"rw");
            long maxSize = entrySize * (maxEntryNum /entrySize);
            fileChannel = file.getChannel();
            fileChannel.truncate(maxSize);
            resetPosition();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
    private Entry readEntryAtPosition(long pos) {
        byte[] d = readAtPosition(pos);
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
        long pos = 0;
        while (pos < totalContentSize) {
            Entry e = readEntryAtPosition(pos);
            if (e != null && e.relativeOffset == 0) {
                break;
            }
            pos += entrySize;
        }
        position = pos;
    }
    private byte[] readAtPosition(long pos) {
        lock.lock();
        try {
            if (pos + 8 >= file.length()) {
                return null;
            }
            byte[] bytes = new byte[8];
            file.read(bytes, (int) pos,8);
            return bytes;
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
    public static void main(String[] args) {
        Random r = new Random();
        Index sadf = new Index("sadf", 1);
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
    private Entry decodeEntry(byte[] data) {
        return null;
    }
}
