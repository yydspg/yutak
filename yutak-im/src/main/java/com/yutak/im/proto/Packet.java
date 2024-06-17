package com.yutak.im.proto;

import io.vertx.core.buffer.Buffer;

public abstract class Packet {
    public int noPersist;
    public int redDot;
    public int syncOnce;
    public int dup;
    public int hasServerVersion;
    public int frameType;
    public abstract int getFrameType();
    public abstract Buffer encode();
    public abstract Packet decode( Buffer buffer);
}
