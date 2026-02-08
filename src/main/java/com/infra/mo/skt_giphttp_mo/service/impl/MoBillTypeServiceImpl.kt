package com.infra.mo.skt_giphttp_mo.service.impl

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpMoAccessEntity
import com.infra.mo.skt_giphttp_mo.service.MoBillTypeService
import com.infra.mo.skt_giphttp_mo.utils.BillTypeValidator
import org.springframework.stereotype.Service

/**
 * BILLTYPE / MOTRBILL 체크 로직 서비스 구현.
 * BillTypeValidator 및 entity 필드를 사용해 각 서비스에서 공통으로 사용할 수 있도록 제공.
 */
@Service
class MoBillTypeServiceImpl : MoBillTypeService {

    override fun getBillTypeChar(entity: GipHttpMoAccessEntity?, defaultChar: Char): Char =
        BillTypeValidator.toChar(entity?.billType, defaultChar)

    override fun validateAndNormalize(billType: String?, defaultValue: String): String =
        BillTypeValidator.validateAndNormalize(billType, defaultValue)

    override fun isFreeBill(gBillType: Char): Boolean = gBillType == '1'

    override fun isGiveBill(gBillType: Char): Boolean = gBillType == '4'

    override fun isMoTrBillEnabled(entity: GipHttpMoAccessEntity?): Boolean =
        entity?.moTrBill != null && entity.moTrBill == 1

    override fun isMoTrBillEnabled(moTrBill: Int?): Boolean = moTrBill != null && moTrBill == 1
}
