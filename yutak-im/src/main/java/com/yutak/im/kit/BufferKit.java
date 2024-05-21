package com.yutak.im.kit;

import com.yutak.im.proto.CS;
import com.yutak.im.proto.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


public class BufferKit {
    private static final Logger log = LoggerFactory.getLogger(BufferKit.class);

    // p -> packet ,f -> fixHeader
    public static Packet decodeFixHeader(Packet p,byte f) {
        p.frameType = f & 0xf0;
        p.dup = (f & 0x08 ) > 0;
        p.syncOnce = (f & 0x04) > 0;
        p.redDot = (f & 0x02) > 0;
        p.noPersist = (f & 0x01) > 0;

        if(p.frameType == CS.FrameType.CONNACK) {
            p.hasServerVersion = (f & 0x01) > 0;
        }
        return p;
    }
    public static byte encodeFixHeader(Packet p) {
        int f = 0;
        if(p.frameType == CS.FrameType.PING || p.frameType == CS.FrameType.PONG) return (byte) (f | p.frameType);
        f |= p.frameType;
        if(p.dup) f |= 0x08;
        if(p.syncOnce) f |= 0x04;
        if(p.redDot) f |= 0x02;
        if(p.noPersist) f |= 0x01;
        if(p.hasServerVersion) f |= 0x01;
        return (byte) f;
    }
    public static void debug(Packet p) {
        try {
            Class<? extends Packet> aClass = p.getClass();
            Field[] fields = aClass.getFields();
            log.info("遍历属性");
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName();
                Object o = f.get(p);
                log.info("{} = {}", name, o);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static void debug(Packet p,Packet p1) {
        try {
            Class<? extends Packet> aClass = p.getClass();
            Field[] fields = aClass.getFields();
            Class<? extends Packet> aClass1 = p1.getClass();
            Field[] fields1 = aClass1.getFields();
            for (Field f : fields) {
                f.setAccessible(true);
                for (Field t : fields1) {
                    t.setAccessible(true);
                    if (t.getName().equals(f.getName()) || t.get(p1) == f.get(p)) {
                        log.info(t.getName()+ " test success !");
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
