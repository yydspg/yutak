package com.yutak.im.store;

public class Config {
    public static String logDir ;
    public static int slotNum;
    public static String dataDir;
    public static Config config ;
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
    public static String systemUIDsKey;
    public static String ipBlacklistKey;
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
        systemUIDsKey = "systemUIDs";
        ipBlacklistKey = "ipBlacklist";
    }

}
