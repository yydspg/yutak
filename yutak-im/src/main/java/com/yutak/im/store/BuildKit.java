package com.yutak.im.store;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class BuildKit {
    public static int sortNum(String channelID) {
        CRC32 crc32 = new CRC32();
        crc32.update(channelID.getBytes(StandardCharsets.US_ASCII));
        return (int) (crc32.getValue() % Config.slotNum);
    }
    public static byte[] buildChannelKey(String channelID,byte channelType) {
        return (Config.channelPrefix+channelID+"-"+channelType).getBytes(StandardCharsets.US_ASCII);
    }
    public static void main(String[] args) {
        int  i =1;
        while (i < 10) {
            System.out.println(sortNum("fezt"));
            i++;
        }
    }
}
