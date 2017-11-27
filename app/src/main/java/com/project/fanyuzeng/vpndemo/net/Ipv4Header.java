package com.project.fanyuzeng.vpndemo.net;

import com.project.fanyuzeng.vpndemo.utils.UnsignedConvertUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * 对IP数据报的封装
 * <p>
 * <a href="http://mars.netanya.ac.il/~unesco/cdrom/booklet/HTML/NETWORKING/IMAGES/ipheader.gif">IP数据报的详细结构</a>
 * <p>
 * <a href="https://ccie.lol/knowledge-base/ipv4-and-ipv6-packet-header/">IP数据报头部结构,超级详细的介绍</a>
 *
 * @author：ZengFanyu .
 * @date：2017/11/25
 */
public class Ipv4Header {
    /**
     * IP数据报的头部标准长度
     */
    public static final int IPV4_HEADER_SISE = 20;
    /**
     * 协议字段位 6 代表 TCP 协议
     */
    public static final int TCP_PROTOCOL_NUM = 6;
    /**
     * 协议字段位 17 代表 UDP 协议
     */
    public static final int UDP_PROTOCOL_NUM = 17;
    /**
     * -1 值自定义,此处只关注 UDP 和 TCP 两种传输层协议
     */
    public static final int OTHER_PROTOCOL_NUM = -1;
    /**
     * 版本 字段，4bit
     */
    private byte mVersion;
    /**
     * IP Header Length 首部长度 字段，4bit,长度单位为 4 字节
     */
    private byte mIHL;
    /**
     * mIHL<<2  也就是乘 4 之后,IP数据报头部长度值 最小为 20 字节
     */
    private int mHeaderLength;
    /**
     * 服务类型 字段，8bit
     */
    private short mTypeOfService;
    /**
     * 总长度 字段, 16bit ,单位为 1 字节,包括头部和数据, 所以IP 数据报最大长度为 65535 字节
     * IP数据报的有效荷载=IP数据报总长度-IP数据报头部长度 (mTotalLength-mHeaderLength)
     */
    private int mTotalLength;

    /**
     * 标识 字段 16 bit ,标志 字段 3 bit,片位移 13bit,这三个字段联合起来用于分段重组操作
     */
    private int mIdentificationAndFlagAndFragmentOffset;
    /**
     * Time to live ,生存时间, 8bit ,建议的缺省值为64,IP包每经过一个路由器,mTTL-- ,
     * 为 0 时则丢弃,防止路由回路导致的IP数据报无限转发的情况
     */
    private short mTTL;
    /**
     * 协议号 字段, 8bit,用于指出此数据报携带何种协议,以便目的主机的IP层将数据报进行合理的交付.
     * 6 TCP；17 UDP
     */
    private short mProtocolNum;
    /**
     * 协议号对应的协议种类
     */
    private TransportProtocol mProtocol;
    /**
     * 首部校验和字段,只检查 IP 数据报的首部,不检查数据部 16bit
     * <p>
     * 因为每个路由器要改变TTL的值，所以路由器会为每个通过的数据包重新计算这个值
     * <p>
     * <a href="https://ccie.lol/knowledge-base/rfc-1141-en-incremental-updating-of-the-internet-checksum/">校验算法</a>
     */
    private int mHeaderCheckSum;
    /**
     * 源地址 字段,32bit
     */
    private InetAddress mSourceAddress;
    /**
     * 目的地址 字段,32bit
     */
    private InetAddress mDestinationAddress;

    /**
     * 从缓冲区中按位读取到字节数据,分别填充至 IP 数据报各字段
     *
     * @param buffer
     */
    public Ipv4Header(ByteBuffer buffer) {
        byte versionAndIHL = buffer.get();
        mVersion = (byte) (versionAndIHL >> 4);

        mIHL = (byte) (versionAndIHL & 0x0F);

        mHeaderLength = mIHL << 2;

        mTypeOfService = UnsignedConvertUtil.getUnsignedByte(buffer.get());
        mTotalLength = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());

        //int 没有 unsinged 和 singed 之分
        mIdentificationAndFlagAndFragmentOffset = buffer.getInt();

        mTTL = UnsignedConvertUtil.getUnsignedByte(buffer.get());

        mProtocolNum = UnsignedConvertUtil.getUnsignedByte(buffer.get());
        mProtocol = TransportProtocol.getProtocolNameByProtocolNum(mProtocolNum);

        mHeaderCheckSum = UnsignedConvertUtil.getUnsignedShort(buffer.getShort());


        try {
            byte[] sourceAddress = new byte[4];
            buffer.get(sourceAddress, 0, 4);
            mSourceAddress = InetAddress.getByAddress(sourceAddress);

            byte[] destinationAddress = new byte[4];
            buffer.get(destinationAddress, 0, 4);
            mDestinationAddress = InetAddress.getByAddress(destinationAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用TCP各个字段值,构造 ByteBuffer
     *
     * @param buffer 填充了IP数据报头部各字段信息的 Buffer
     */
    public void consructIPv4Buffer(ByteBuffer buffer) {
        buffer.put((byte) (mVersion << 4 | mIHL));
        buffer.put((byte) mTypeOfService);
        buffer.putShort((short) mTotalLength);
        buffer.putInt(mIdentificationAndFlagAndFragmentOffset);
        buffer.put((byte) mTTL);
        buffer.put((byte) mProtocol.getProtocolNumber());
        buffer.putShort((short) mHeaderCheckSum);
        buffer.put(mSourceAddress.getAddress());
        buffer.put(mDestinationAddress.getAddress());
    }


    @Override
    public String toString() {
        return "IPv4 header info{" + "Version=" + mVersion +
                ",IHL=" + mIHL +
                ",Type of service=" + mTypeOfService +
                ",Total length=" + mTotalLength +
                ",Identification and flag and fragment offset=" + mIdentificationAndFlagAndFragmentOffset +
                ",TTL=" + mTTL +
                ",Protocol number=" + mProtocolNum +
                ",Header check sun=" + mHeaderCheckSum +
                ",Source address" + mSourceAddress.getHostAddress() +
                ",Destination address=" + mDestinationAddress.getHostAddress() +
                "}";
    }

    /**
     * 封装 protocol 字段对应的传输协议
     */
    private enum TransportProtocol {

        /**
         * 表示传输控制协议
         */
        TCP(TCP_PROTOCOL_NUM),
        /**
         * 表示用户数据报协议
         */
        UDP(UDP_PROTOCOL_NUM),

        /**
         * IP头部的protocol字段
         */
        OTHERPROTOCOL(OTHER_PROTOCOL_NUM),;

        private int protocolNumber;

        private static TransportProtocol getProtocolNameByProtocolNum(int protocolNumber) {
            TransportProtocol mProtocolName;
            if (protocolNumber == TCP_PROTOCOL_NUM) {
                mProtocolName = TCP;
            } else if (protocolNumber == UDP_PROTOCOL_NUM) {
                mProtocolName = UDP;
            } else {
                mProtocolName = OTHERPROTOCOL;
            }
            return mProtocolName;
        }

        TransportProtocol(int protocolNumber) {
            this.protocolNumber = protocolNumber;
        }

        public int getProtocolNumber() {
            return protocolNumber;
        }
    }

    public byte getVersion() {
        return mVersion;
    }

    public byte getIHL() {
        return mIHL;
    }

    public int getHeaderLength() {
        return mHeaderLength;
    }

    public short getTypeOfService() {
        return mTypeOfService;
    }

    public int getTotalLength() {
        return mTotalLength;
    }

    public int getIdentificationAndFlagAndFragmentOffset() {
        return mIdentificationAndFlagAndFragmentOffset;
    }

    public short getTTL() {
        return mTTL;
    }

    public short getProtocolNum() {
        return mProtocolNum;
    }

    public TransportProtocol getProtocol() {
        return mProtocol;
    }

    public int getHeaderCheckSum() {
        return mHeaderCheckSum;
    }

    public InetAddress getSourceAddress() {
        return mSourceAddress;
    }

    public InetAddress getDestinationAddress() {
        return mDestinationAddress;
    }

    public void setVersion(byte version) {
        mVersion = version;
    }

    public void setIHL(byte IHL) {
        mIHL = IHL;
    }

    public void setHeaderLength(int headerLength) {
        mHeaderLength = headerLength;
    }

    public void setTypeOfService(short typeOfService) {
        mTypeOfService = typeOfService;
    }

    public void setTotalLength(int totalLength) {
        mTotalLength = totalLength;
    }

    public void setIdentificationAndFlagAndFragmentOffset(int identificationAndFlagAndFragmentOffset) {
        mIdentificationAndFlagAndFragmentOffset = identificationAndFlagAndFragmentOffset;
    }

    public void setTTL(short TTL) {
        mTTL = TTL;
    }

    public void setProtocolNum(short protocolNum) {
        mProtocolNum = protocolNum;
    }

    public void setProtocol(TransportProtocol protocol) {
        mProtocol = protocol;
    }

    public void setHeaderCheckSum(int headerCheckSum) {
        mHeaderCheckSum = headerCheckSum;
    }

    public void setSourceAddress(InetAddress sourceAddress) {
        mSourceAddress = sourceAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress) {
        mDestinationAddress = destinationAddress;
    }
}
