package com.project.fanyuzeng.vpndemo.net;

import com.project.fanyuzeng.vpndemo.utils.UnsignedConvertUtil;

import java.nio.ByteBuffer;

/**
 * 对TCP报文段的封装
 *
 * @author：ZengFanyu .
 * @date：2017/11/25
 * @see <a href="https://zhangbinalan.gitbooks.io/protocol/content/tcpbao_wen_ge_shi.html">TCP报文段字段含义以及大小</a>
 */

public class TcpHeader {
    /**
     * TCP报文段的头部标准长度
     */
    public static final int TCP_HEADER_SISE = 20;
    /**
     * 源端口号 字段,16bit
     */
    private int mSourcePort;
    /**
     * 目的端口号字段,16bit
     */
    private int mDestinationPort;
    /**
     * 数据序号字段,32bit
     */
    private long mSeqNum;
    /**
     * 确认序号字段, 32bit
     */
    private long mAckNum;
    /**
     * 偏移字段 4 bit,和保留字段6bit, 其中偏移字段的单位是 4 byte
     */
    private byte mDataOffsetAndReserved;
    /**
     * 根据偏移字段计算出的头部长度
     */
    private int mHeaderLength;
    /**
     * 标记位字段,6个,1 个 1 bit
     */
    private byte mFlags;
    /**
     * 窗口 字段,16bit
     */
    private int mWindows;
    /**
     * 校验和字段 16bit
     */
    private int mCheckSum;
    /**
     * 紧急指针字段,16bit
     */
    private int mUrgentPointer;
    /**
     * 可选数据字段和填充数据字段
     */
    private byte[] mOptionDataAndFillData;

    /**
     * 从缓冲区中按位读取到字节数据,分别填充至 TCP 报文段的各字段
     *
     * @param buffer
     */
    public TcpHeader(ByteBuffer buffer) {
        mSourcePort = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());
        mDestinationPort = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());
        mSeqNum = UnsignedConvertUtil.getUnsignedInt(buffer.getInt());
        mAckNum = UnsignedConvertUtil.getUnsignedInt(buffer.getInt());
        mDataOffsetAndReserved = buffer.get();
        //偏移字段4bit,保留字段6bit,先 与 0xf0 得到数据偏移字段,又因为偏移字段的单位是4byte 所以<<2
        // TODO: 2017/11/27 check mHeaderLength is valid ??
        mHeaderLength = (mDataOffsetAndReserved & 0xf0) << 2;
        //此处前2bit是保留位字段的最后2位,不属于标记位的内容,取后6位标记位时,按位与
        mFlags = buffer.get();
        mWindows = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());
        mCheckSum = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());
        mUrgentPointer = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());

        int optionLength = mHeaderLength - TCP_HEADER_SISE;

        if (optionLength > 0) {
            mOptionDataAndFillData = new byte[optionLength];
            buffer.get(mOptionDataAndFillData, 0, optionLength);
        }

    }

    public void constructTcpHeader(ByteBuffer buffer) {
        buffer.put((byte) mSourcePort);
        buffer.put((byte) mDestinationPort);
        buffer.putInt((int) mSeqNum);
        buffer.putInt((int) mAckNum);
        buffer.put(mDataOffsetAndReserved);
        buffer.put(mFlags);
        buffer.putShort((short) mWindows);
        buffer.putShort((short) mCheckSum);
        buffer.putShort((short) mUrgentPointer);

        //选项字段和填充字段值在建立连接的请求中发送

    }

    public boolean isUAG() {
        return (mFlags & 0x20) == 0x20;
    }

    public boolean isACK() {
        return (mFlags & 0x10) == 0x10;
    }

    public boolean isPSH() {
        return (mFlags & 0x08) == 0x08;
    }

    public boolean isRST() {
        return (mFlags & 0x04) == 0x04;
    }

    public boolean isSYN() {
        return (mFlags & 0x02) == 0x02;
    }

    public boolean isFIN() {
        return (mFlags & 0x01) == 0x01;
    }

    @Override
    public String toString() {
        return "TCP header info{" + "SourcePort=" + mSourcePort +
                ",DestinationPort=" + mDestinationPort +
                ",SequenceNum=" + mSeqNum +
                ",AcknowledgementNum=" + mAckNum +
                ",DataOffsetAndReserved=" + mDataOffsetAndReserved +
                ",HeaderLength=" + mHeaderLength +
                ",Windows=" + mWindows +
                ",CheckSum=" + mCheckSum +
                ",UrgentPointer=" + mUrgentPointer +
                ",Flags=" +
                "[UAG=" + isUAG() +
                " || ACK=" + isACK() +
                " || PSh=" + isPSH() +
                " || RST=" + isRST() +
                " || SYN=" + isSYN() +
                " || FIN=" + isFIN() + "]" +
                "}";
    }

    public int getSourcePort() {
        return mSourcePort;
    }

    public int getDestinationPort() {
        return mDestinationPort;
    }

    public long getSeqNum() {
        return mSeqNum;
    }

    public long getAckNum() {
        return mAckNum;
    }

    public byte getDataOffsetAndReserved() {
        return mDataOffsetAndReserved;
    }

    public int getHeaderLength() {
        return mHeaderLength;
    }

    public byte getFlags() {
        return mFlags;
    }

    public int getWindows() {
        return mWindows;
    }

    public int getCheckSum() {
        return mCheckSum;
    }

    public int getUrgentPointer() {
        return mUrgentPointer;
    }

    public byte[] getOptionDataAndFillData() {
        return mOptionDataAndFillData;
    }

    public void setSourcePort(int sourcePort) {
        mSourcePort = sourcePort;
    }

    public void setDestinationPort(int destinationPort) {
        mDestinationPort = destinationPort;
    }

    public void setSeqNum(long seqNum) {
        mSeqNum = seqNum;
    }

    public void setAckNum(long ackNum) {
        mAckNum = ackNum;
    }

    public void setDataOffsetAndReserved(byte dataOffsetAndReserved) {
        mDataOffsetAndReserved = dataOffsetAndReserved;
    }

    public void setHeaderLength(int headerLength) {
        mHeaderLength = headerLength;
    }

    public void setFlags(byte flags) {
        mFlags = flags;
    }

    public void setWindows(int windows) {
        mWindows = windows;
    }

    public void setCheckSum(int checkSum) {
        mCheckSum = checkSum;
    }

    public void setUrgentPointer(int urgentPointer) {
        mUrgentPointer = urgentPointer;
    }

    public void setOptionDataAndFillData(byte[] optionDataAndFillData) {
        mOptionDataAndFillData = optionDataAndFillData;
    }
}
