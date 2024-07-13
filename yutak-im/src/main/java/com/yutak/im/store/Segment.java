package com.yutak.im.store;

import com.yutak.im.domain.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Segment {
    public int baseMessageSeq;
    public String segmentDir;
    public int position;
    public AtomicBoolean isSanityCheck;
    public AtomicInteger lastMessageSeq;
    public long fileSize;
    public long indexIntervalBytes;
    public long bytesSinceLastIndexEntry;
    private Topic topic;
    public Index index;
    private ReentrantLock lock;
    private FileChannel segmentFile;
    private ByteBuffer buffer;
    public Segment(Topic t,int baseMessageSeq) {
        segmentDir = t.topicDir +"/logs";
        this.baseMessageSeq = baseMessageSeq;
        indexIntervalBytes = 4* 1024;
        topic = t;
        // block method
        index = new Index(indexPath(),baseMessageSeq);
        lock = new ReentrantLock();
        buffer = ByteBuffer.allocate(4);
        try{
            Path p = Paths.get(segmentDir);
            Files.createDirectories(p);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public int appendMessages(List<Message> messages) {
        if (messages == null ||messages.isEmpty()) {
            return 0;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        messages.forEach(m -> {
            byte[] e = m.encode();
            byte[] length = Kit.intToBytes(e.length);
            try {
                stream.write(length);
                stream.write(e);
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        });
        byte[] data = stream.toByteArray();
        int dataSize = append(data);
        // file size not enough
        if(bytesSinceLastIndexEntry > indexIntervalBytes || dataSize > indexIntervalBytes) {
            index.append(messages.get(0).recvPacket.messageSeq, position - dataSize);
        }
        bytesSinceLastIndexEntry += dataSize;
        try {
            stream.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return 0;
    }
    public Message readAt(int messageSeq) {
        lock.lock();
        try{
            Index.MessageSeqPosition pos = index.lookup(messageSeq);

        }finally {
            lock.unlock();
        }
        return null;
    }
    private long readTargetPosition(long startPos,int targetMessageSeq) {
        if (startPos > fileSize) {
            log.error("over size");
            return 0;
        }
        return 0;
    }
    public void force(){
        try {
            segmentFile.force(true);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private int append(byte[] data) {
        lock.lock();
        try{
            ByteBuffer buffer = ByteBuffer.wrap(data);
            segmentFile.write(buffer);
            position += data.length;
            fileSize += data.length;
            return data.length;
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return -1;
    }
    public void init() {
        if (isSanityCheck.get()) {
            return;
        }
        try{
            if (segmentFile == null) {
                segmentFile = FileChannel.open(Paths.get(segmentPath()), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            }
            fileSize = segmentFile.size();
            long lastMsgStartPosition = sanityCheck(fileSize);
            if (lastMsgStartPosition == 0) {
                if (position > 0) {
                    //only one message
                    lastMessageSeq.set(baseMessageSeq+1);
                }else{
                    // no message
                    lastMessageSeq.set(baseMessageSeq);
                }
            }else {
                int msgSeq = getMsgSeq(lastMsgStartPosition);
                if (msgSeq != -1) {lastMessageSeq.set(msgSeq);}
            }
            isSanityCheck.set(true);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public void close(){
        force();
        try {
            segmentFile.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        index.close();
    }
    private int getMsgSeq(long offset){
        try {
            segmentFile.position(offset+4);
            buffer.clear();
            segmentFile.read(buffer);
            byte[] a = buffer.array();
            return Kit.bytesToInt(a);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return -1;
    }
    /**
     *
     * @param segmentFileSize file size
     * @return last message start position
     */
    private long sanityCheck(long segmentFileSize) {
        Index.MessageSeqPosition offsetPosition = index.lastPosition();
        if (offsetPosition.position <= 0 && offsetPosition.messageSeq <= 0)
            return 0;
        long startCheckPosition = offsetPosition.position;
        long lastMsgLen = 0;
        while(startCheckPosition < segmentFileSize) {
            long l = nextMsgLen(startCheckPosition);
            if (l == 4){
                // end file
                break;
            }
            if (l == -1) {
                // error
                break;
            }
            lastMsgLen = l;
            startCheckPosition += l;
        }
        position = (int) startCheckPosition;
        return startCheckPosition - lastMsgLen;
    }

    private long nextMsgLen(long offset) {
        try {
            segmentFile.position(offset);
            buffer.clear();
            segmentFile.read(buffer);
            byte[] a = buffer.array();
            int len = Kit.bytesToInt(a);
            return  4 + len;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return -1;
    }
    private String segmentPath() {
        return segmentDir+"/"+baseMessageSeq+Config.segmentSuffix;
    }
    private String indexPath() {
        return segmentDir +"/"+ baseMessageSeq+Config.indexSuffix;
    }
}
