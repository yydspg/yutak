package com.yutak.im.domain;

public class Stream {
    public static class Meta {
        public String  streamNo ;
        public long  messageID  ;
        public String  channelID;
        public byte  channelType;
        public int  messageSeq  ;
        public byte  streamFlag ;

    }
    public static class Item {
        public String ClientMsgNo ;
        public int StreamSeq   ;
        public byte[] Blob        ;
    }
}
