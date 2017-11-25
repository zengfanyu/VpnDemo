package com.project.fanyuzeng.vpndemo;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: fanyuzeng on 2017/11/23 11:31
 * @desc:
 */
public class Utils {
    private static final int BUFFER_ALLOCATE_CAPACITY = 10240;
    private static ConcurrentLinkedQueue<ByteBuffer> sBufferQueue = new ConcurrentLinkedQueue<>();

    /**
     * 关闭可关闭的资源
     * @param somethingCanClosed
     */
    public static void closeResource(Closeable... somethingCanClosed) {
        for (Closeable closeable : somethingCanClosed) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 此处获取的缓冲区是直接建立在物理内存上的,所以要注意回收
     *
     * @return
     */
    public static ByteBuffer getByteBuffer() {
        ByteBuffer buffer = sBufferQueue.poll();
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(BUFFER_ALLOCATE_CAPACITY);
        }
        return buffer;
    }

    /**
     * 释放 Bytebuffer 的容量,存入队列中缓存
     *
     * @param buffer
     */
    public static void releaseCapacity(ByteBuffer buffer) {
        buffer.clear();
        sBufferQueue.offer(buffer);
    }

    /**
     * 请求ByteBuffer队列中缓存的ByteBuffer
     */
    public static void clear() {
        sBufferQueue.clear();
    }

}
