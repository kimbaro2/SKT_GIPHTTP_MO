package com.infra.mo.skt_giphttp_mo.dto.jna

data class GIMSGHDR(
    val uMsgVerId: Int,

    val szSrcCId: ByteArray,   // QITEM_SIZE_CID 크기
    val uSrcCallNo: Int,
    val uSrcAddrRsv: Int,

    val szDestCId: ByteArray,  // QITEM_SIZE_CID 크기
    val uDestCallNo: Int,
    val uDestAddrRsv: Int,

    val usMsgCode: Short,
    val usMsgSubCode: Short,
    val usMsgCodeRsv: ShortArray, // 크기 2

    val uDataLen: Int,
    val uMsgSerialNo: Int,
    val ucTermType: Byte,
    // val ucDataType: Byte,   // 주석 처리된 필드
    val ucDataEncoding: Byte,
    val ucRsv: ByteArray,       // 크기 2

    val nRsv4Protocol: IntArray, // 크기 12

    val ucData: ByteArray        // 크기 4
)
