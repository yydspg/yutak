package com.yutak.im.domain;

public class Conversation {
        public String UID               ; // User UID (user who belongs to the most recent session)
        public String ChannelID         ; // Conversation channel
        public byte ChannelType         ;
        public int UnreadCount          ;    // Number of unread messages
        public long Timestamp           ;  // Last session timestamp (10 digits)
        public int LastMsgSeq           ; // Sequence number of the last message
        public String LastClientMsgNo   ; // Last message client number
        public long LastMsgID           ;  // Last message ID
        public long Version             ;  // Data version
}
