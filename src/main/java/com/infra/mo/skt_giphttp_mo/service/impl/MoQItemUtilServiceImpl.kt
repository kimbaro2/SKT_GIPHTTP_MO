package com.infra.mo.skt_giphttp_mo.service.impl

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM
import com.infra.mo.skt_giphttp_mo.service.MoQItemUtilService
import org.springframework.stereotype.Service

/**
 * QITEM 유틸 서비스 구현.
 * MOThreadPool 공통 호출 함수(copyQItem, getNInforNo)를 서비스 내부에서 사용할 수 있도록 의존성만 주입.
 */
@Service
class MoQItemUtilServiceImpl : MoQItemUtilService {

    override fun copyQItem(src: QITEM): QITEM {
        src.write()
        val copy = QITEM()
        copy.write()
        val bytes = src.pointer.getByteArray(0, src.size())
        copy.pointer.write(0, bytes, 0, bytes.size)
        copy.read()
        return copy
    }

    override fun getNInforNo(qItem: QITEM): Int = qItem.usSource
}
