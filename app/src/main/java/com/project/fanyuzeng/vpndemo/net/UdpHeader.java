package com.project.fanyuzeng.vpndemo.net;

import com.project.fanyuzeng.vpndemo.utils.UnsignedConvertUtil;

import java.nio.ByteBuffer;

/**
 * 对 UDP 头部的封装
 * <p>
 * <a href="https://zhangbinalan.gitbooks.io/protocol/content/udptou_bu.html">UDP头部结构</a>
 *
 * @author：ZengFanyu .
 * @date：2017/11/25
 */
public class UdpHeader {
    /**
     * UDP数据报头部标准长度
     */
    public static final int UDP_HEADER_SISE = 8;

    /**
     * 源端口 字段 , 16bit
     */
    private int mSourcePort;
    /**
     * 目的端口 字段, 16bit
     */
    private int mDestinationPort;
    /**
     * UDP长度字段,包含头部 16bit
     */
    private int mLength;
    /**
     * 校验和 字段,16bit
     */
    private int mCheckSum;

    /**
     * 从缓冲区中按位读取字节数据,填充到 UDP 各个字段
     *
     * @param byteBuffer
     */
    public UdpHeader(ByteBuffer byteBuffer) {
        mSourcePort = UnsignedConvertUtil.getUnsignedShort(byteBuffer.getShort());
        mDestinationPort = UnsignedConvertUtil.getUnsignedShort(byteBuffer.getShort());
        mLength = UnsignedConvertUtil.getUnsignedShort(byteBuffer.getShort());
        mCheckSum = UnsignedConvertUtil.getUnsignedShort(byteBuffer.getShort());
    }

    /**
     * 用UDP各个字段值, ByteBuffer
     *
     * @param buffer
     */
    public void constructUdpBuffer(ByteBuffer buffer) {
        buffer.putShort((short) mSourcePort);
        buffer.putShort((short) mDestinationPort);
        buffer.putShort((short) mLength);
        buffer.putShort((short) mCheckSum);
    }

    @Override
    public String toString() {
        return "UDP header info{" + "SourcePort=" + mSourcePort +
                ",DestinationPort=" + mDestinationPort +
                ",Length" + mLength +
                ",Check sum" + mCheckSum +
                "}";
    }
}
