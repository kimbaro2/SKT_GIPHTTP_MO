package com.infra.mo.skt_giphttp_mo.dto.jna

data class SMReqTransResDTO(
    var ucMsgStatus: Byte,
    var ucGSMErrCode: Byte,
    var ucMsgId: ByteArray // 크기 = QITEM_SIZE_MSGID
)