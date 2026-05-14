package com.palmer.billingstatementgenerator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Manages a per-user OS-level file lock to prevent multiple instances of the
 * application from running simultaneously. The lock file is created at
 * {@code ~/.wfh-billing/.lock} and is automatically released on JVM exit.
 */
public final class AppLock {
    private static final Logger log = LoggerFactory.getLogger(AppLock.class);
    private static final String LOCK_PATH = System.getProperty("user.home") + "/.wfh-billing/.lock";

    private static FileChannel channel;
    private static FileLock lock;

    private AppLock() {
    }

    /**
     * Attempts to acquire the application lock.
     *
     * @return {@code true} if the lock was acquired, {@code false} if another instance is running
     */
    public static boolean acquire() {
        try {
            new File(LOCK_PATH).getParentFile().mkdirs();
            channel = new RandomAccessFile(LOCK_PATH, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                return false;
            }
            Runtime.getRuntime().addShutdownHook(new Thread(AppLock::release));
            return true;
        } catch (IOException e) {
            log.warn("Could not acquire application lock", e);
            return false;
        }
    }

    private static void release() {
        try {
            if (lock != null) lock.release();
            if (channel != null) channel.close();
        } catch (IOException e) {
            log.warn("Failed to release application lock", e);
        }
    }
}