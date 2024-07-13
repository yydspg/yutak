package com.yutak.im.store;

import io.netty.buffer.ByteBufOutputStream;
import io.vertx.core.json.Json;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    // TODO  :  here has bug
    @Slf4j
    public static class StreamItem {
        public int size;
        public String clientMsgNo;
        public int streamSeq;
        public byte[] blob;
        // data type :
        /*
            4 bytes : total length
            4 bytes : stream seq
            4 bytes : client msg length
             ... bytes : client msg
             4 bytes : blob length
             ... bytes : blob
         */
        public byte[] encode() {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            byte[] str = clientMsgNo.getBytes(StandardCharsets.UTF_8);
            // 1. total data length
            // 2. clientMsgNo length
            // 3. blob length
            b.write(4+4+4+str.length+blob.length);
            b.write(streamSeq);
            b.write(str.length);
            try {
                b.write(str);
                b.write(blob.length);
                b.write(blob);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return b.toByteArray();
        }
        // input data
        /*
           total data
         */
        public StreamItem decode(byte[] data) {
            int t = 0;
            byte[] size = new byte[4];
            for (int i = t; i < 4; i++) {
                size[i] = data[i];
            }
            this.size = Kit.bytesToInt(size);
            t += 4;
            byte[] seq = new byte[4];
            for (int i = 0; i < 4; i++) {
                seq[i] = data[i+t];
            }
            this.streamSeq = Kit.bytesToInt(seq);
            t += 4;
            byte[] str = new byte[4];
            for (int i = 0; i < 4; i++) {
                str[i] = data[i+t];
            }
            int strLen = Kit.bytesToInt(str);
            t += 4;
            byte[] msgNo = new byte[strLen];
            for (int i = 0; i < strLen; i++) {
                msgNo[i] = data[i+t];
            }
            clientMsgNo = new String(msgNo, StandardCharsets.UTF_8);
            t += strLen;
            byte[] len = new byte[4];
            for (int i = 0; i < 4; i++) {
                len[i] = data[i+t];
            }
            int blobLenLen = Kit.bytesToInt(len);
            t += 4;
            blob = new byte[blobLenLen];
            for (int i = 0; i < blobLenLen; i++) {
                blob[i] = data[i+t];
            }
            return this;
        }
    }
}
