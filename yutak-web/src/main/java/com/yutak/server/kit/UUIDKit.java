package com.yutak.server.kit;

import java.util.UUID;

public class UUIDKit {
    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
