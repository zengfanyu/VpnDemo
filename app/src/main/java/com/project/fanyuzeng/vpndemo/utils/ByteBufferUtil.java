package com.project.fanyuzeng.vpndemo.utils;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 创建直接内存的工具类
 * <p>
 * 不使用JVM堆栈而是通过操作系统来创建内存块用作缓冲区，它与当前操作系统能够更好的耦合，
 * 因此能进一步提高I/O操作速度。但是分配直接缓冲区的系统开销很大，因此只有在缓冲区较大并长期存在，
 * 或者需要经常重用时，才使用这种缓冲区
 *
 * @author：ZengFanyu .
 * @date：2017/11/25
 */
public class ByteBufferUtil {
    private static final int BUFFER_ALLOCATE_CAPACITY = 10240;
    /**
     * 线程安全的队列,用于缓存已经从操作系统中申请的内存
     */
    private static ConcurrentLinkedQueue<ByteBuffer> sBufferQueue = new ConcurrentLinkedQueue<>();


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
     * 释放 ByteBuffer 的容量,存入队列中缓存
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
