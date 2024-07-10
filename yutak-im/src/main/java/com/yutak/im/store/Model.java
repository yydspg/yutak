package com.yutak.im.store;

import io.vertx.core.json.Json;

import java.nio.charset.StandardCharsets;

public class Model {
    public static class userInfo {
        public String token;
        public byte device_level;
    }
    public static class StreamMeta {
        public String streamNo;
        public long messageID;
        public String channelID;
        public int channelType;
        public int messageSeq;
        public int streamFlag; // related to CS.stream
        public byte[]  encode() {
            return Json.encode(this).getBytes(StandardCharsets.UTF_8);
        }
    }
    public static class StreamItem {
        public String clientMsgNo;
        public int streamSeq;
        public byte[] blob;
    }
}
