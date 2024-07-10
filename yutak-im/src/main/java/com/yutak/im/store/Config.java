package com.yutak.im.store;

import com.yutak.im.YutakIMApplication;
import io.vertx.core.file.FileSystem;

import java.nio.charset.StandardCharsets;

public class Config {
    public static String logDir ;
    public static int slotNum;
    public static String dataDir;
    public static boolean firstStart;
    public static String rootBucketPrefix;
    public static String messageOfUserCursorPrefix;
    public static String userTokenPrefix;
    public static String channelPrefix;
    public static String subscribersPrefix;
    public static String denylistPrefix;
    public static String allowlistPrefix;
    public static String notifyQueuePrefix;
    public static String userSeqPrefix;
    public static String nodeInFlightDataPrefix;
    public static String conversationPrefix;
    public static byte[] systemUIDsKey;
    public static byte[] ipBlacklistKey;
    public static boolean isFirstStart;
    public static int messageSeqSize;
    public static int messageDataLenSize;
    public static int indexMaxSize;
    public static FileSystem fileSystem;
    public static String streamSuffix;
    public static String streamMetaSuffix;
    public static String indexSuffix;
    public static String segmentSuffix;
    public static long segmentMaxSize;
    // must remember use this constructor
    static {
        slotNum = 2;
        dataDir = "./data";
        logDir = "./log";
        firstStart = false;
        messageOfUserCursorPrefix = "messageOfUserCursor";
        userTokenPrefix = "userToken";
        channelPrefix = "channel";
        subscribersPrefix = "subscribers";
        denylistPrefix = "denylist";
        allowlistPrefix = "allowlist";
        notifyQueuePrefix = "notifyQueue";
        userSeqPrefix = "userSeq";
        nodeInFlightDataPrefix = "nodeInFlightData";
        conversationPrefix = "conversation:";
        systemUIDsKey = "systemUIDs".getBytes(StandardCharsets.UTF_8);
        ipBlacklistKey = "ipBlacklist".getBytes(StandardCharsets.UTF_8);
        isFirstStart = false;
        messageSeqSize = 8;
        messageDataLenSize = 4;
        indexMaxSize = 2 *1024 *1024; // index file max size 2m
        fileSystem = YutakIMApplication.vertx.fileSystem();
        streamSuffix = ".stream";
        streamMetaSuffix = ".streamMeta";
        indexSuffix = ".index";
        segmentSuffix = ".segment";
        segmentMaxSize = 4 * 1024 * 1024;
    }

}
