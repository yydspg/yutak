package com.yutak.im.store;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class Kit {
    public static int slotNum(String K) {
        CRC32 crc32 = new CRC32();
        crc32.update(K.getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % Config.slotNum);
    }
    public static byte[] buildChannelKey(String channelID,byte channelType) {
        return (Config.channelPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildUserTokenKey(String uid,int deviceFlag) {
        return (Config.userTokenPrefix+uid+"-"+deviceFlag).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildSubscribeKey(String channelID,byte channelType) {
        return (Config.subscribersPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildAllowListKey(String channelID,byte channelType) {
        return (Config.allowlistPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildDenyListKey(String channelID,byte channelType) {
        return (Config.denylistPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildConversationKey(String uid) {
        return (Config.conversationPrefix+uid).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] encode(JsonObject o) {
        return o.encode().getBytes(StandardCharsets.UTF_8);
    }
    public static JsonObject decodeObj(byte[] b) {
        return new JsonObject(new String(b, StandardCharsets.UTF_8));
    }
    public static byte[] encode(JsonArray o) {
        return o.encode().getBytes(StandardCharsets.UTF_8);
    }
    public static JsonArray decodeArray(byte[] b) {
        return new JsonArray(new String(b, StandardCharsets.UTF_8));
    }
    //Little end sorting
    // can not be used for String --> bytes
    public static int bytesToInt(byte[] bytes) {
        // must be bytes.length <= 4
        if (bytes == null || bytes.length > 4) {
            return -1;
        }
        int r = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            r = r | ((bytes[i] & 0xff) << (8 * i));
        }
        return r;
    }

    // little end sorting
    public static byte[] intToBytes(int v) {
        byte[] b = new byte[4];
        b[3] = (byte) ((v >> 24) & 0xFF);
        b[2] = (byte) ((v >> 16) & 0xFF);
        b[1] = (byte) ((v >> 8) & 0xFF);
        b[0] = (byte) (v & 0xFF);
        return b;
    }
    public static void main(String[] args) {
      int a = 1;
        byte[] bytes = intToBytes(a);
        int i = bytesToInt(bytes);
        System.out.println(i);
    }
}
