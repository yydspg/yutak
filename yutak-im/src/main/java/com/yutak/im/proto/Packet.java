package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public abstract class Packet {
    public boolean noPersist;
    public boolean redDot;
    public boolean syncOnce;
    public boolean dup;
    public boolean hasServerVersion;
    public int frameSize;
    public CS.FrameType frameType;
    public abstract byte getFrameType();
    public abstract Buffer encode(byte version);
    public abstract Packet decode(byte version, Buffer buffer);
}
