package com.yutak.im.store;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.zip.CRC32;

public class Kit {
    public static int slotNum(String K) {
        CRC32 crc32 = new CRC32();
        crc32.update(K.getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % Config.slotNum);
    }
    public static byte[] buildChannelKey(String channelID,int channelType) {
        return (Config.channelPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildUserTokenKey(String uid,int deviceFlag) {
        return (Config.userTokenPrefix+uid+"-"+deviceFlag).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildSubscribeKey(String channelID,int channelType) {
        return (Config.subscribersPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildAllowListKey(String channelID,int channelType) {
        return (Config.allowlistPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildDenyListKey(String channelID,int channelType) {
        return (Config.denylistPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] buildConversationKey(String uid) {
        return (Config.conversationPrefix+uid).getBytes(StandardCharsets.UTF_8);
    }
    public static String buildTopicKey(String channelID,int channelType) {
        return channelID+"-"+channelType;
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
    public static void debug(Object p, Object p1) {
        try {
            Class aClass = p.getClass();
            Field[] fields = aClass.getDeclaredFields();
//            Field[] fields = aClass.getFields();
            Class aClass1 = p1.getClass();
            Field[] fields1 = aClass1.getFields();
            for (Field f : fields) {
                f.setAccessible(true);
                for (Field t : fields1) {
                    t.setAccessible(true);
                    if (t.getName().equals(f.getName()) || t.get(p1) == f.get(p)) {
                        System.out.println("test success");
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String generateRandomString(int length) {

        SecureRandom random = new SecureRandom();

        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(length);


        for (int i = 0; i < length; i++) {

            int index = random.nextInt(allowedChars.length());

            char randomChar = allowedChars.charAt(index);

            sb.append(randomChar);

        }


        return sb.toString();

    }
}
