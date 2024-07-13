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
    public LRU<String,YutakStream> streamLRU;
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
        streamLRU = new LRU<>();
        try{
            Path p = Paths.get(topicDir);
            Files.createDirectories(p);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    public int getLastMessageSeq(){
        return lastMsgSeq.get();
    }
    private Segment getSegment(int baseMessageSeq) {
        String key = getSegmentCacheKey(baseMessageSeq);
        Segment s = segmentLRU.get(key);
        if(s != null) {
            return s;
        }
        segmentsLock.lock();
        try{
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
    public Message readMessageAt(int messageSeq) {
        int baseMessageSeq = getSlotNum(messageSeq);
        Segment segment = getSegment(baseMessageSeq);
        return segment.readAt(messageSeq);
    }
    public void saveStreamMeta(Model.StreamMeta meta) {
        getStream(meta.streamNo).saveMeta(meta);
    }
    public List<Message> readLastMessages(int limit) {
        return null;
    }
    public List<Message> readLastMessagesWithEnd(int endMessageSeq,int limit) {
        return null;
    }
    public Model.StreamMeta readStreamMeta(String streamNo) {
        return getStream(streamNo).readMeta();
    }
    public List<Model.StreamItem> getStreamItems(String streamNo) {
        return getStream(streamNo).readItems();
    }
    public void streamEnd(String streamNo) {
        getStream(streamNo).end();
        streamLRU.remove(streamNo);
    }
    public int appendStreamItem(String streamNo, Model.StreamItem item) {
        YutakStream s = getStream(streamNo);
        return s.appendItem(item);
    }
    private Segment resetActiveSegment(int baseMessageSeq) {
        Segment s = getSegment(baseMessageSeq);
        lastMsgSeq.set(s.lastMessageSeq.get());
        return s;
    }
    private void roll(Message m){
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
    // TODO  :  here ,this method need to be use once
    public void initSegments() {
        List<Integer> s = getAllSegmentsBaseMessageSeq();
        Collections.sort(s);
        segments = s;
        lastBaseMessageSeq = s.get(s.size()-1);
    }
    private YutakStream getStream(String streamNo){
        YutakStream s;
        s= streamLRU.get(streamNo);
        if(s==null) {
            s = new YutakStream(streamNo,topicDir);
            streamLRU.put(streamNo,s);
        }
        return s;
    }
    // return base message seq, according to input message seq
    private int getSlotNum(int msgSeq){
        for (int i = 0; i < segments.size()-1; i++) {
            if (segments.get(i)>= msgSeq &&  msgSeq < segments.get(i+1)){
                return segments.get(i);
            }
        }
        // default is the max message seq
        return segments.get(segments.size()-1);
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
