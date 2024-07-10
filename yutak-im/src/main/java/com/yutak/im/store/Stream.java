package com.yutak.im.store;

import com.yutak.im.proto.CS;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class Stream {
    public String topicDir;
    public String streamNo;
    public FileChannel streamFile;
    public FileChannel streamMetaFile;
    public int maxStreamSeq;
    private ReentrantLock streamLock;
    private ReentrantLock streamMetaLock;
    public boolean maxStreamSeqLoaded;
    public Stream(String streamNo,String topicDir) {
        streamLock = new ReentrantLock();
        streamMetaLock = new ReentrantLock();
        this.streamNo = streamNo;
        this.topicDir = topicDir;
        maxStreamSeqLoaded = false;
    }
    private int readMaxStreamSeq() {
        long startOffset = 0;
        int maxStreamSeq = 0;
        buildStreamFile();
        try {
            if (streamFile.size() == 0) {
                return 0;
            }
            long fileSize = streamFile.size();
            while(startOffset < fileSize) {

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    private Model.StreamMeta readMeta() {
        streamMetaLock.lock();
        try {
            buildStreamMetaFile();
            streamMetaFile.position(0);
            ByteBuffer a = ByteBuffer.allocate(200);
            streamMetaFile.read(a);
            byte[] b1 = a.array();
            int t = 0;
            while (b1[t] != 0) {
                t++;
            }
            byte[] b2 = new byte[t];
            for (int i1 = 0; i1 < b2.length; i1++) {
                b2[i1] = b1[i1];
            }
            String s = new String(b2, StandardCharsets.UTF_8);
            JsonObject e = new JsonObject(s);
            return e.mapTo(Model.StreamMeta.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            streamMetaLock.unlock();
        }
        return null;
    }
    // this method only use once
    private void saveMeta(Model.StreamMeta data) {
        streamMetaLock.lock();
        try{
            buildStreamMetaFile();
            streamMetaFile.position(0);
            ByteBuffer a = ByteBuffer.allocate(200);
            a.put(data.encode());
            // TODO  :  memory recycle
            streamMetaFile.write(a);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            streamMetaLock.unlock();
        }
    }
    private void streamEnd() {
        Model.StreamMeta meta = readMeta();
        if (meta != null) {
            meta.streamFlag = CS.Stream.end;
        }
        saveMeta(meta);
    }
    private void buildStreamMetaFile()  {
        if (streamMetaFile == null) {
            Path p = Paths.get(streamMetaPath());
            try {
                streamMetaFile = FileChannel.open(p,StandardOpenOption.CREATE, StandardOpenOption.WRITE,StandardOpenOption.READ);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
    private void buildStreamFile(){
        if(streamFile == null){
            Path p = Paths.get(streamPath());
            try {
                streamFile = FileChannel.open(p,StandardOpenOption.CREATE, StandardOpenOption.WRITE,StandardOpenOption.READ);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
    private String streamPath(){
        return topicDir+"/"+streamNo+Config.streamSuffix;
    }
    private String streamMetaPath() {
        return topicDir +"/"+streamNo+ Config.streamMetaSuffix;
    }

    public static void main(String[] args) throws IOException {
        String test ="/home/paul/pro/yutak/testFile";
        int i = 0;
        Random r = new Random();
        long l = System.currentTimeMillis();
        while (i < 2000) {
            i++;
            Model.StreamMeta s = new Model.StreamMeta();
            s.streamNo = Kit.generateRandomString(40);
            s.channelID = Kit.generateRandomString(40);
            s.channelType = 90;
            s.messageID = r.nextInt();
            s.streamFlag = 0;
            s.messageSeq = r.nextInt();

            byte[] bytes = Json.encode(s).getBytes();
            FileChannel file = FileChannel.open(Paths.get(test), StandardOpenOption.CREATE, StandardOpenOption.WRITE,StandardOpenOption.READ);

            file.force(true);
            ByteBuffer a = ByteBuffer.allocate(200);
            try {
                file.read(a);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] b1 = a.array();
            int t = 0;
            while (b1[t] != 0) {
                t++;
            }
            byte[] b2 = new byte[t];
            for (int i1 = 0; i1 < b2.length; i1++) {
                b2[i1] = b1[i1];
            }

            String s1 = new String(b2, StandardCharsets.UTF_8);
            JsonObject e = new JsonObject(s1);
            Model.StreamMeta streamMeta = e.mapTo(Model.StreamMeta.class);
            Kit.debug(s,streamMeta);
        }
    }
}
