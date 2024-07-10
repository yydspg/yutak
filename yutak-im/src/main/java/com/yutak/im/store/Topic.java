package com.yutak.im.store;

import com.yutak.im.core.LRU;
import com.yutak.im.domain.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Slf4j
public class Topic {
    public String name;
    public int slot;
    public List<Integer> segments;
    public int lastBaseMessageSeq;
    public String topicDir;
    public ReentrantLock lock ;
    public LRU<String, Segment> segmentLRU;
    private AtomicInteger lastMsgSeq;
    private ReentrantLock segmentsLock;
    private ReentrantLock appendLock;
    public Topic(String name,int slot) {
        this.name = name;
        this.slot = slot;
        lock = new ReentrantLock();
        topicDir = topicPath(slot,name);
        segmentLRU = new LRU<>();
        segmentsLock = new ReentrantLock();
        appendLock = new ReentrantLock();
        lastMsgSeq = new AtomicInteger(0);
        try{
            Path p = Paths.get(topicDir);
            Files.createDirectories(p);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private Segment getSegment(int baseMessageSeq) {
        segmentsLock.lock();
        try{
            String key = getSegmentCacheKey(baseMessageSeq);
            Segment s = segmentLRU.get(key);
            if(s != null) {
                return s;
            }
            s = new Segment(this, baseMessageSeq);
            s.init();
            segmentLRU.put(key, s);
            return s;
        }finally {
            segmentsLock.unlock();
        }
    }
    public List<Integer> appendMessages(List<Message> messages) {
        appendLock.lock();
        try{
            Segment lastSegment = getActiveSegment();
            int preLastMsgSeq = lastSegment.lastMessageSeq.get();
            ArrayList<Integer> seqs = new ArrayList<>();
            messages.forEach(f->{
                int i = nextMsgSeq();
                f.recvPacket.messageSeq = i;
                seqs.add(i);
            });
            Message lastMsg = messages.get(messages.size() - 1);
            if (preLastMsgSeq >= lastMsg.recvPacket.messageSeq) {
                log.warn("message exists:{}",lastMsg.recvPacket.messageSeq);
                return null;
            }
            lastMsgSeq.set(lastMsg.recvPacket.messageSeq);
            // build a new segment
            if (lastSegment.index.isFull() || lastSegment.position > Config.segmentMaxSize) {
                roll(lastMsg);
                lastSegment = getActiveSegment();
            }
            // append message to last segment
            lastSegment.appendMessages(messages);
            return seqs;
        }finally {
            appendLock.unlock();
        }
    }
    private Segment resetActiveSegment(int baseMessageSeq) {
        Segment s = getSegment(baseMessageSeq);
        lastMsgSeq.set(s.lastMessageSeq.get());
        return s;
    }
    public void roll(Message m){
        Segment lastSegment = getActiveSegment();
        if (lastSegment!= null) {
            segmentLRU.remove(getSegmentCacheKey(m.recvPacket.messageSeq));
        }
        lastBaseMessageSeq = m.recvPacket.messageSeq;
        segments.add(m.recvPacket.messageSeq);
        resetActiveSegment(lastBaseMessageSeq);
    }
    private Segment getActiveSegment(){
        return getSegment(segments.get(segments.size()-1));
    }
    private int nextMsgSeq(){
        return lastMsgSeq.incrementAndGet();
    }
    private String getSegmentCacheKey(int baseMessageSeq) {
        return name + "-" + baseMessageSeq;
    }
    private String topicPath(int slot,String name){
        return Config.dataDir + slot+"topics/"+name;
    }
    public void initSegments() {
        List<Integer> s = getAllSegmentsBaseMessageSeq();
        Collections.sort(s);
        segments = s;
        lastBaseMessageSeq = s.get(s.size()-1);

    }
    private List<Integer> getAllSegmentsBaseMessageSeq() {
        String logDir = topicDir + "/logs";
        ArrayList<Integer> res = new ArrayList<>();
        try (Stream<Path> files = Files.list(Paths.get(logDir));) {
            files.forEach(file -> {
                if (file.getFileName().endsWith(Config.segmentSuffix)) {
                    res.add(Integer.parseInt(file.getFileName().toString()));
                }
            });
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }
}
