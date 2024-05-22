package com.yutak.im.domain;

public class Stream {
    public static class Meta {
        public String  StreamNo ;
        public long  MessageID  ;
        public String  ChannelID;
        public byte  ChannelType;
        public int  MessageSeq  ;
        public byte  StreamFlag ;

    }
    public static class Item {
        public String ClientMsgNo ;
        public int StreamSeq   ;
        public byte[] Blob        ;
    }
}
