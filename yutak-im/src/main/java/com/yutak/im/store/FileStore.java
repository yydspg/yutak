package com.yutak.im.store;

import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileStore {

    public RocksDB db;
    public final ThreadPoolExecutor executor; //executor pool
    public final ConcurrentHashMap<Integer,ColumnFamilyDescriptor> slotDescriptos;
    public final ConcurrentHashMap<Integer,ColumnFamilyHandle> slotHandles;
    private final Logger log = LoggerFactory.getLogger(FileStore.class);
    private final WriteOptions wOps;
    private final ReadOptions rOps;
    public FileStore() {
        executor = new ThreadPoolExecutor(10, 30, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        slotHandles = new ConcurrentHashMap<>();
        slotDescriptos = new ConcurrentHashMap<>();
        wOps = new WriteOptions();
        rOps = new ReadOptions();
    }
    private void init() {
        Options options = null;
        try {
            options = new Options();
            options.setDbLogDir(Config.logDir);
            options.setCreateIfMissing(true);
            db = RocksDB.open(options,Config.dataDir);
            RocksDB.loadLibrary();
            // create handler and descriptor in memory
            for (int i = 0; i < Config.slotNum; i++) {
                String s = Integer.toHexString(i);
                ColumnFamilyDescriptor c = new ColumnFamilyDescriptor(s.getBytes(StandardCharsets.US_ASCII));
                ColumnFamilyHandle handle = db.createColumnFamily(c);
                if(handle != null) {
                    // single thread put into map
                    slotDescriptos.put(i, c);
                    slotHandles.put(i, handle);
                }
            }
            db.close();
            db = RocksDB.open(Config.dataDir,slotDescriptos.values().stream().toList(),slotHandles.values().stream().toList());
            //
            log.info("Start file store successfully");
        } catch (Exception e) {
            log.error("File store start error,cause:{}",e.getMessage());
            if(e instanceof RocksDBException) {
                options.close();
                if(db != null) db.close();
                slotDescriptos.clear();
                slotHandles.clear();
            }
        }
    }
    public void destroy() {
        executor.shutdown();
        slotDescriptos.clear();
        slotHandles.forEach((k,v)->{
            v.close();
        });
        slotDescriptos.clear();
    }
    private ColumnFamilyHandle getHandle(int slot) {
        return slotHandles.get(slot);
    }
    public CompletableFuture<ChannelInfo> getChannel(String channelID,byte channelType) {
        return CompletableFuture.supplyAsync(()->{
            byte[] V = getV(BuildKit.sortNum(channelID), BuildKit.buildChannelKey(channelID, channelType));
            if(V == null) {
                return null;
            }
            ChannelInfo c = new ChannelInfo();
            c.channelId = channelID;
            c.channelType = channelType;
            c.ban = V[0];
            c.large = V[1];
            c.disband = V[2];
            return c;
        },executor);
    }
    private void putKV(int slot,byte[] K,byte[] V) {
        try {
            db.put(slotHandles.get(slot),wOps,K,V);
        } catch (RocksDBException e) {
            log.error("putK error,cause:{}",e.getMessage());
        }
    }
    private byte[] getV(int slot,byte[] K) {
        byte[] V = null;
        try {
            V = db.get(slotHandles.get(slot),rOps,K);
        }catch (RocksDBException e) {
            log.error("getK error,cause:{}",e.getMessage());
        }
        return V;
    }
    public static void main(final String[] args) throws RocksDBException {
//        if (args.length < 1) {
//            System.out.println(
//                    "usage: RocksDBColumnFamilySample db_path");
//            System.exit(-1);
//        }
        System.out.println("start rocksDB test");
        final String db_path = "./data";

        System.out.println("RocksDBColumnFamilySample");
        try(final Options options = new Options().setCreateIfMissing(true);
            final RocksDB db = RocksDB.open(options, db_path)) {

            assert(db != null);

            // create column family
            try(final ColumnFamilyHandle columnFamilyHandle = db.createColumnFamily(
                    new ColumnFamilyDescriptor("new_cf".getBytes(),
                            new ColumnFamilyOptions()))) {
                assert (columnFamilyHandle != null);
            }
        }
        ColumnFamilyDescriptor columnFamilyDescriptor = new ColumnFamilyDescriptor("1".getBytes(StandardCharsets.US_ASCII));
        ColumnFamilyOptions cl = new ColumnFamilyOptions();
        // open DB with two column families
        final List<ColumnFamilyDescriptor> columnFamilyDescriptors =
                new ArrayList<>();
        // have to open default column family
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
        // open the new one, too
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
                "new_cf".getBytes(), new ColumnFamilyOptions()));
        final List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
        try(final DBOptions options = new DBOptions();
            final RocksDB db = RocksDB.open(options, db_path,
                    columnFamilyDescriptors, columnFamilyHandles)) {
            assert(db != null);

            try {
                // put and get from non-default column family
                db.put(
                        columnFamilyHandles.get(1), new WriteOptions(), "key".getBytes(), "value".getBytes());

                // atomic write
                try (final WriteBatch wb = new WriteBatch()) {
                    wb.put(columnFamilyHandles.get(0), "key2".getBytes(),
                            "value2".getBytes());
                    wb.put(columnFamilyHandles.get(1), "key3".getBytes(),
                            "value3".getBytes());
                    wb.delete(columnFamilyHandles.get(1), "key".getBytes());
                    db.write(new WriteOptions(), wb);
                }

                // drop column family
                db.dropColumnFamily(columnFamilyHandles.get(1));
            } finally {
                for (final ColumnFamilyHandle handle : columnFamilyHandles) {
                    handle.close();
                }
            }
        }
    }
}