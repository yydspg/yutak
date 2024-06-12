package com.yutak.im.domain;

import java.util.List;

public abstract class Channel {
   public abstract boolean baned();
   public abstract List<String> getSubscribedUsers();
}
