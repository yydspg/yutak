package com.yutak.im.domain;

public class Conversation {
        public String UID               ; // User UID (user who belongs to the most recent session)
        public String channelID         ; // Conversation channel
        public int channelType         ;
        public int unreadCount          ;    // Number of unread messages
        public long timestamp           ;  // Last session timestamp (10 digits)
        public int lastMsgSeq           ; // Sequence number of the last message
        public String lastClientMsgNo   ; // Last message client number
        public long lastMsgID           ;  // Last message ID
        public long version             ;  // Data version
}
