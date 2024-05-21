package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public abstract class Packet {
    public boolean noPersist;
    public boolean redDot;
    public boolean syncOnce;
    public boolean dup;
    public boolean hasServerVersion;
    public int frameSize;
    public int frameType;
    public abstract int getFrameType();
    public abstract Buffer encode();
    public abstract Packet decode( Buffer buffer);
}
