package com.yutak.im.store;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Segment {
    public int baseMessageSeq;
    public String segmentDir;
    public int position;
    public AtomicBoolean isSanityCheck;
    public AtomicInteger lastMessageSeq;
    public long fileSize;
    public long indexIntervalBytes;
    public long bytesSinceLastIndexEntry;
}
