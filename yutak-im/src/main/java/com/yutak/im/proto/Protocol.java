package com.yutak.im.proto;

import com.yutak.im.kit.BufferKit;
import io.vertx.core.buffer.Buffer;

public class Protocol {

    public static Buffer encode(Packet packet) {
        return packet.encode();
    }
    public static Packet decode(Buffer buffer) {
        Packet packet = BufferKit.getPacket(buffer);
        return packet.decode(buffer);
    }
}
