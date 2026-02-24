package com.infra.mo.skt_giphttp_mo.dto.jna

interface LibC {
    companion object {
        const val RTLD_LAZY: Int = 0x00001
        const val RTLD_NOW: Int = 0x00002
        const val RTLD_GLOBAL: Int = 0x00100
        const val RTLD_LOCAL = 0x00000
    }
}

object SmsDef {
    const val OCS_SMSC = 900;
    const val SKT_TP = 11;
    const val KTF_TP = 16;
    const val LGT_TP = 19;

    const val MESSAGE_MO = 1
    const val MESSAGE_MT = 2
    const val MESSAGE_TR = 3
    const val SPLITED_MO = 4    /* HSMSS */

    const val TABLE_LOGTRACE_READ_INTERVAL = 60

    const val TABLE_READ_INTERVAL = 300

    const val TABLE_READCONFIG_INTERVAL = 300

    const val TABLE_READ_INTERVAL_300 = 300     /* 2013.05.13 - 140bytes & UCS2 */

    const val TABLE_READ_INTERVAL_60 = 60     /* 통합 PKG, 1분주기 */

    const val VSMSS_TYPE = 'V'

    const val HSMSS_TYPE = 'H'

    const val LOG_TYPE_REQ = 0

    const val LOG_TYPE_RESULT = 1

    const val MAX_LOG_ENTRY = 10000

    const val USEC_MSEC = 1000        /* OS Upgrade : 2017.2Q : ADD */

    const val CDMA_ROAMING = 36

    const val PORTED_CDMA_ROAMING = 37

    const val GSM_WCDMA_ROAMING = 40

    const val PORTED_GSM_WCDMA_ROAMING = 41

    const val NOTI_PLUS_NORMAL_MO = 20

    const val NOTI_PLUS_PORTED_MO = 21

    const val NOTI_PLUS_PORTED_MT = 22 /* HSMSS */

    const val NORMAL_MO = 1

    const val PORTED_MO = 57

    const val CALLFWD_BLOCK_NOTI_CID = "3333333311"    /* dbpark 2010.06.30 착신전환 차단 안내 CID */

    const val CALLFWD_BLOCK_NOTI_CALLBACK = "114"

    const val NORMAL_TR = 4

    const val PORTED_TR = 56

    const val FORWARD_MO = 48

    const val FORWARD_MT = 49

    const val FORWARD_TR = 50

    const val FORWARD_PORTED_MT = 52

    const val PORTED_FORWARD_MT = 52 /* HSMSS */

    const val DOUBLE_FORWARD_TR = 53

    const val FORWARD_CDMA_ROAMING_MO = 38

    const val FORWARD_GSM_ROAMING_MO = 42

    const val NUMBER_PLUS_MO = 64

    const val NUMBER_PLUS_TR = 65

    const val NUMBER_PLUS_CDMA_ROAMING = 69

    const val NUMBER_PLUS_GSM_ROAMING = 70

    const val NUMBER_PLUS_CDMA_ROAMING_MO =
        69                                          /* NUMBER_PLUS : Added by KimJongha 2009.02.25 */

    const val NUMBER_PLUS_GSM_ROAMING_MO =
        70                                          /* NUMBER_PLUS : Added by KimJongha 2009.02.25 */

    const val BIZ_NUMBER_MO = 71

    const val BIZ_NUMBER_CDMA_ROAMING_MO = 72

    const val BIZ_NUMBER_GSM_ROAMING_MO = 73

    const val BIZ_NUMBER_TR = 80

    const val BIZ_NUMBER_MT = 81

    const val BIZ_NUMBER_PORTED_MT = 82

    const val KTF_2G_PORTED_MO = 6

    const val KTF_2G_PORTED_CDMA_ROAMING_MO = 16

    const val KTF_2G_PORTED_GSM_ROAMING_MO = 18

    const val KTF_2G_NOTI_PLUS_PORTED_MO = 23

    const val KTF_3G_PORTED_MO = 10

    const val KTF_3G_PORTED_CDMA_ROAMING_MO = 12

    const val KTF_3G_PORTED_GSM_ROAMING_MO = 13

    const val KTF_3G_NOTI_PLUS_PORTED_MO = 25

    const val LGT_2G_PORTED_MO = 9

    const val LGT_2G_PORTED_CDMA_ROAMING_MO = 17

    const val LGT_2G_PORTED_GSM_ROAMING_MO = 19

    const val LGT_2G_NOTI_PLUS_PORTED_MO = 24

    const val LGT_3G_PORTED_MO = 11

    const val LGT_3G_PORTED_CDMA_ROAMING_MO = 14

    const val LGT_3G_PORTED_GSM_ROAMING_MO = 15

    const val LGT_3G_NOTI_PLUS_PORTED_MO = 26

    const val NOTI_NORMAL_MO =
        90                                          /* NUMBER_PLUS : Modified by KimJongha 2009.03.04 기존 : 70*/

    const val NOTI_PORTED_MO =
        91                                          /* 2009 2Q : Modified by KimJongha 2009.04.29 기존 : 71*/

    const val KTF_2G_NOTI_PORTED_MO =
        92                                          /* 2009 2Q : Modified by KimJongha 2009.04.29 기존 : 82*/

    const val KTF_3G_NOTI_PORTED_MO =
        93                                          /* 2009 2Q : Modified by KimJongha 2009.04.29 기존 : 83*/

    const val LGT_2G_NOTI_PORTED_MO =
        94                                          /* 2009 2Q : Modified by KimJongha 2009.04.29 기존 : 84*/

    const val LGT_3G_NOTI_PORTED_MO =
        95                                          /* 2009 2Q : Modified by KimJongha 2009.04.29 기존 : 85*/

    const val NOTI_NUMBER = "7777777777"

    const val NOTI_ING_NUMBER = "7777777778"

    const val NOTI_PLUS_NUMBER = "3333399999"

    const val NOTI_PLUS_ING_NUMBER = "3333399990"

    const val MGR = 0    /* 운용자 처리부 (resource 관리용)	*/

    const val SDM = 1    /* 운용자 처리부 (화면 관리용)		*/

    const val SIM = 2    /* 운용자 처리부 (가입자 정보관리)	*/

    const val CTM = 3    /* 운용자 처리부 (구성관리용)		*/

    const val DBM = 4    /* DB 처리부				*/

    const val IDBM = 5    /* IonD DB 처리부 */

    const val IOPM = 6    /* IonD menu&information 운용자 처리부  */

    const val SCLMB = 10    /* 스케쥴러 미납처리부 			*/

    const val SCLMR = 11    /* 스케쥴러 공지사항처리부 		*/

    const val SCLMP = 12    /* 스케쥴러 개인생활정보 처리부 	*/

    const val SCLML = 13    /* 스케쥴러 생활정보 처리부 		*/

    const val CISIF = 20    /* CIS I/F parent			*/

    const val CISIFC = 21    /* CIS I/F command 처리부		*/

    const val CISIFR = 22    /* CIS I/F result 처리부		*/

    const val CISIFF = 23    /* CIS I/F Billdata 처리부 */

    const val BILLDATA = 23      /* CIS I/F Billdata 처리부 */

    const val NOTSENDBILL = 24  /* CIS I/F NotifyPay 처리부 */

    const val NOTDELIVERED = 25  /* CIS I/F NotifyPay 처리부 */

    const val GUCS = 30    /* Bank UserChange Send 처리부          */

    const val SMSIFT = 100    /* SMS I/F 처리부			*/

    const val SMSIFR = 200    /* SMS I/F 처리부			*/

    const val CBCIF = 300    /* CBC I/F처리부			*/

    const val CSIF = 400    /* 고객센터 처리부			*/

    const val IVRIF = 500    /* IVR I/F 처리부			*/

    const val VMSIF = 600    /* VMS I/F 처리부			*/

    const val IPIF = 700    /* 생활정보 I/F 처리부			*/

    const val ARSIF = 800    /* 정보샘 처리부  */

    const val IPDBIF = 900    /* 생활정보 DB I/F 처리부          */

    const val SMSMOT = 1000    /* IonD SMS I/F Transmitter 처리부 	*/

    const val SMSMOR = 1100    /* IonD SMS I/F Receiver 처리부 	*/

    const val IAM = 1200    /* IonD Agent Module			*/

    const val RIAM = 1250    /* Response IonD Agent Module 		*/

    const val IPAM = 1300    /* IP Agent Module			*/

    const val MIPAM = 1400    /* Mobile Origination IP Agent Module	*/

    const val IPSTAT = 1500    /* IP Agent 통계 처리부                 */

    const val BDSIF = 1600    /* Phone Mail과금 처리부 */

    const val BDSIF_PRI = 1601    /* Phone Mail과금 처리부 - Primary*/

    const val BDSIF_SEC = 1602    /* Phone Mail과금 처리부 - Secondary*/

    const val BDSIF_CONV = 1603    /* Phone Mail과금 처리부 - ConvForm*/

    const val NPDB = 2000    /* NPDB */

    const val OK = 1

    const val SUCCESS = 1

    const val ERROR = -1

    const val SM_SEND_MSG = 0    /* 메시지 전송에 관련된 패킷 	      */

    const val SM_MSG_SEND_RESULT = 1    /* 메시지 전송후 결과저장,   	      */

    const val SM_PRIVATE_INFO = 2    /* 개인정보 저장, DB table write      */

    const val SM_LIVING_INFO = 3    /* 생활정보 저장, DB table write      */

    const val SM_LIVING_RESULT = 4    /* 생활정보 전송후 결과저장, DB write */

    const val SM_ERROR = 5    /* 운용자 모듈 (SDM)에 에러전송 UDP   */

    const val SM_LINE_STATUS = 6    /* I/F 접속부에서 라인검사결과 저장   */

    const val SM_READ_RESULT = 7    /* VMS, IVR에서 메시지 전송 결과 조회 */

    const val SM_QUERY_SUB = 8    /* VMS, IVR에서 가입자 조회, DB read  */

    const val SM_UPDATE_SUB = 9    /* 외부모듈에서 가입자 변경, DB write */

    const val SM_BILL_DATA_FILE = 10    /* 정기원의 미납화일, FTP read	      */

    const val SM_BILL_DATE_UPDATE = 11    /* 정기원 미납데이타 DB저장, DB write */

    const val SM_BILL_DATA_READ = 12    /* CS에서 미납데이타 읽음, DB read    */

    const val SM_SEND_BILL_UPDATE = 13    /* CS에서 미납데이타 변경후 전송요구  */

    const val SM_SEND_IOND = 20    /* IOND관련 SMS 메시지전송 */

    const val SM_TELE_SEND = 21    /* Telemetry관련 SMS 메시지전송 */

    const val SM_TELE_RESULT = 22    /* Telemetry관련 SMS 메시지결과 */

    const val SM_MO_BEEP = 23    /* MO로 요구하는 무선호출 정보 */

    const val SM_IP_REQUEST = 24    /* Agent -> IPAM 으로 정보 요구 */

    const val SM_IP_RESULT = 25    /* IPAM -> Agent로 정보 결과 */

    const val SM_SIMPLE_MSG = 26    /* IP -> IPAM으로 단순 정보 전송 */

    const val SM_SIMPLE_RESULT = 27    /* IPAM -> IP로 단순 정보 전송 결과 */

    const val SM_LINE_CHECK = 28    /* Line Check 전송 */

    const val SM_LINE_CHECK_RESULT = 29    /* Line Check Result 전송 */

    const val SM_FLOW_CNTL = 30      /* Flow Control 요구 */

    const val SM_FLOW_STRT = 31      /* 초기 전송 요구 */

    const val CALL_TYPE_MO = 1

    const val CALL_TYPE_MT = 2

    const val CALL_TYPE_WEBTOPHONE = 3

    const val MSG_DELEVER_OK = 2
    const val SEND_OK = 2  // VBILL_MO용
    const val SEND_FAIL = -1  // VBILL_MO용
    const val NOTI_TIMEOUT = 3  // VBILL_MO용 (분 단위)

    const val IOND_START = 65527    /* 단말기->SMS Server:단말기 최초 접속 */

    const val IOND_MENU = 65528    /* 단말기->SMS Server:단말기 메뉴 선택 데이타 */

    const val IOND_PROMPT = 65529    /* 단말기->SMS Server:단말기 prompt 입력 */

    const val IOND_MSG_MENU = 65530    /* 단말기->SMS Server:에러, 메뉴에 대한 선택 */

    const val IOND_CALLDATA = 65531    /* 단말기->Server:call back형 자료 '계속'요구 */

    const val IOND_IPDATA = 65532    /* 단말기->Server:데이터형 자료 '계속' 요구 */

    const val IOND_END = 65533    /* SMS Server->단말기:에러메시지 & 종료요구 */

    const val IOND_TELE = 65534    /* 단말기->SMS Server:Telemetry Data 전송 */

    const val FILE_ERROR = 1    /* 화일 open, read, write error 	      */

    const val ASYN_ERROR = 2    /* async device open, read, write error	      */

    const val SOCK_ERROR = 3    /* UNIX socket(TCP,UDP) open,read,write error */

    const val DB_ERROR = 4    /* DB open, read, write error		      */

    const val PROTOCOL_ERROR = 5    /* 통신 프로토콜 error			      */

    const val SEMA_ERROR = 6    /* semaphore, sharedMem init, read,write error*/

    const val MODEM_ERROR = 7    /* 모뎀포트 block 또는 이상상태		      */

    const val NETW_ERROR = 8    /* TCP/IP network socket open, read write,    */

    const val FORK_ERROR = 9    /* process fork error 			      */

    const val X25_ERROR = 10    /* X.25관련 error			      */

    const val SIGNAL_ERROR = 11    /* signal error 		 	      */

    const val SELECT_ERROR = 12    /* select  error	   		      */

    const val CONFIG_ERROR = 13    /* Config Value Read/Write error	      */

    const val ETC_ERROR = 14    /* 위에서 지정되지 않은 error		      */

    const val Q_EMPTY_ERR = -1

    const val Q_FULL_ERR = -1

    const val ENT_CRI_SEC_ERR = -11

    const val EX_CRI_SEC_ERR = -12

    const val MAX_STATID = 100

    const val SMSIF_IDX = 0

    const val CISIF_IDX = 1

    const val ARSIF_IDX = 2

    const val IVRIF_IDX = 4

    const val VMSIF_IDX = 5

    const val IPIF_IDX = 6

    const val IPDBIF_IDX = 8

    const val SMSMO_IDX = 9

    const val IPAM_IDX = 12

    const val BDSIF_IDX = 15

    const val SHM_MSG = 0x1876.toChar()

    const val SHM_INFO = 0x1877.toChar()

    const val SHM_STATUS = 0x1875.toChar()

    const val SHM_IOND = 0x1880.toChar()

    const val SEM_MSG = 0x186A.toChar()

    const val SEM_INFO = 0x186B.toChar()

    const val SEM_IOND = 0x1890.toChar()

    const val SCLMP_TYPE_DEFAULT = 0

    const val SCLMP_TYPE_PAY_NOTI = 1    /* 자동이체 가입자 대상 요금 안내 */

    const val SCLMP_TYPE_GREETING = 2    /* 신규 등록 가입자 인사말  */

    const val SCLMP_TYPE_SVC_RESTART = 3    /* 정지해제 안내문 */

    const val PCS_TRACE_DELIVER_IN = 0

    const val PCS_TRACE_DELIVER_OUT = 1

    const val PCS_TRACE_PDELIVER_IN = 2

    const val PCS_TRACE_PDELIVER_OUT = 3

    const val H2H_TRACE_DELIVER_IN = 0

    const val H2H_TRACE_DELIVER_OUT = 1

    const val H2H_TRACE_PDELIVER_IN = 2

    const val H2H_TRACE_PDELIVER_OUT = 3

    const val GL_TRACE_DELIVER_IN = 0

    const val GL_TRACE_DELIVER_OUT = 1

    const val GL_TRACE_PDELIVER_IN = 2

    const val GL_TRACE_PDELIVER_OUT = 3

    const val GS_TRACE_DELIVER_IN = 4

    const val GS_TRACE_DELIVER_OUT = 5

    const val GS_TRACE_PDELIVER_IN = 6

    const val GS_TRACE_PDELIVER_OUT = 7

    const val GW_TRACE_DELIVER_IN = 8

    const val GW_TRACE_DELIVER_OUT = 9

    const val TRACE_DELIVER_ACK_IN = 100

    const val TRACE_DELIVER_ACK_OUT = 101

    const val TRACE_PDELIVER_ACK_IN = 102

    const val TRACE_PDELIVER_ACK_OUT = 103

    const val TRACE_REPORT_IN = 104

    const val TRACE_REPORT_OUT = 105

    const val QITEM_TRACE_DELIVER_IN = 0

    const val QITEM_TRACE_DELIVER_OUT = 1

    const val MRM_TRACE_DELIVER_IN = 0

    const val MRM_TRACE_DELIVER_OUT = 1

    const val SMPP_TRACE_DELIVER_IN = 0

    const val SMPP_TRACE_DELIVER_OUT = 1

    const val MAX_CALL_NUMBER = 99999999  /* dbpark 2012.11.15 */

    const val MIN_CALL_NUMBER = 1000000        /* 2013.03.18 - request by lake */

    const val DPRINTF_LOG_PERIOD_MINUTELY = 0x01.toChar()

    const val DPRINTF_LOG_PERIOD_TEN_MINUTELY = 0x10.toChar()

    const val DPRINTF_LOG_PERIOD_HOURLY = 0x11.toChar()

    const val DPRINTF_LOG_PERIOD_DAILY = 0x12.toChar()    /* modified by jws 12 -> 0x12 */

    const val DPRINTF_LOG_PERIOD_WEEKLY = 0x13.toChar()

    const val STATPRINTF_PERIOD_TEN_MINUTE = 0x10.toChar()

    const val STATPRINTF_PERIOD_MINUTE = 0x11.toChar()        /* 2016.10.13 - CB 통계 추가 생성 */

    const val STATPRINTF_PERIOD_5MINUTE = 0x05.toChar()

    const val DEFINE_GIPVERID_462 = 462

    const val DEFINE_GIPVERID_470 = 470

    const val DEFINE_GIPVERID_500 = 500

    const val DEFINE_GIPVERID_510 = 510

    const val DEFINE_GIPVERID_4 = 4

    const val DEFINE_GIPVERID_5 = 5

    const val DEFINE_HTTPVERID_5 = 5


    const val MAX_SHORT_MSG_LEN = 180 /* VSMSS */

    const val MAX_SHORT_MSG_LEN_MT = 140 /* VSMSS */
    const val MAX_SHORT_MSG_LEN_CP949 = 80 /* CP949 한글 인코딩용 */


    const val MAX_CALLBACK_LEN = 20

    const val MAX_GLOBAL_SHORT_MSG_LEN = 140

    const val MAX_GSMS_SHORT_MSG_LEN = 140

    const val GIP_MO_DATALENGTH_510 = 224

    const val GIP_MO_DATALENGTH_500 = 209

    const val GIP_MO_DATALENGTH_470 = 171

    const val GIP_MO_DATALENGTH_462 = 156

    const val QITEM_SIZE_CID = 16

    const val QITEM_SIZE_MINNO = 12

    const val QITEM_SIZE_MSGID = 9

    const val MAX_LOCATION = 10

    const val MAX_ROAMING = 8

    const val LEN_TRACE_ID = 20

    const val OSFI4_LEN = 4

    const val OSFI_LEN = 8

    const val OSFI_SIZE = OSFI_LEN + 1

    const val SMS_OSFI_LEN = 5

    const val SMS_OSFI_SIZE = SMS_OSFI_LEN + 1

    const val MVNO_INFO_LEN = 25

    const val MVNO_INFO_SIZE = MVNO_INFO_LEN + 1

    const val FULL_RN_LEN = 25                    /* 2012.10.31 - FULL RN */

    const val FULL_RN_SIZE = FULL_RN_LEN + 1        /* 2012.10.31 - FULL RN */

    const val RCS_REFERENCE_SIZE = 15         /* 2012.11.22 dbpark */

    const val ORIGCID_SIZE = 10    /* 2022.05 KISA 식별코드 */

    const val RELAYCID_SIZE = 5    /* 2022.05 KISA 식별코드 */

    const val LINK_VERSION_SMPP = 200        /* 2011.12.07 - OSFI8 */

    const val LINK_VERSION_SMPP_OSFI8_MVNO = 201        /* 2011.12.07 - OSFI8, MVNO */

    const val LINK_VERSION_SMPP_FULL_RN = 203        /* 2012.10.31 - FULL RN */

    const val LINK_VERSION_SMPP_DCS_UCS2 = 204        /* 2013.04.08 - 140bytes & UCS2 */

    const val LINK_VERSION_SMPP_AUTH_FLAG = 205        /* 2014.07.08 - AUTH_FLAG */

    const val LINK_VERSION_SMPP_STAT_FLAG = 206        /* 2015.06.22 - SMSC STAT ADD */

    const val LINK_VERSION_SMPP_ROAMING_IND = 207        /* 2016.09.08 - Roaming Ind PKG */

    const val LINK_VERSION_MRM = 400        /* 2012.01.06 - OSFI8 */

    const val LINK_VERSION_MRM_OSFI8_MVNO = 401        /* 2012.01.06 - OSFI8, MVNO */

    const val LINK_VERSION_MRM_LGRN = 401        /* 2011.12.06 - OSFI8, MVNO */

    const val GIP_BODY_LENGTH_OLD = 116

    const val GIP_BODY_LENGTH = 156

    const val GIP_BODY_LENGTH_470 = 171        /* 2022.07 KISA 식별코드 156 + szOrgCID[10] + szRelayCID[5] */

    const val GIP_BODY_LENGTH_500 = 209        /* 2013.04.08 - 140bytes & UCS2 */

    const val GIP_BODY_LENGTH_510 = 224        /* 2022.07 KISA 식별코드 209 + szOrgCID[10] + szRelayCID[5] */

    const val NPDB_RN_SKT_2G = 1190

    const val NPDB_RN_SKT_3G = 1020

    const val NPDB_RN_KCT_3G = 1021

    const val NPDB_RN_KTF_2G = 1690

    const val NPDB_RN_KTF_3G = 1029

    const val NPDB_RN_LGT_2G = 1990

    const val NPDB_RN_LGT_3G = 1022

    const val TELECOM_CODE_010 = 10

    const val TELECOM_CODE_SKT = 11

    const val TELECOM_CODE_KTF = 16

    const val TELECOM_CODE_LGT = 19

    const val NP_PREFIX_SKT = 11        /* Added by Kimjongha 2007.10.16 : for Stat의 NP Prefix */

    const val NP_PREFIX_KTF = 16

    const val NP_PREFIX_KTF_018 = 18

    const val NP_PREFIX_LGT = 19

    const val RM_FLAG_BLOCK_CONNECT = 1

    const val RM_FLAG_PRIORITY_FLAG = 2

    const val RM_FLAG_BKSMSC_SEND = 3

    const val M2M_RSP_MO = 1

    const val M2M_RSP_MT = 2

    const val M2M_RSP_OTA_TR = 3

    const val MAX_SMS_QNO = 24

    const val Q_PRI_NORMAL = 0

    const val Q_PRI_URGENT = 1

    const val MAX_PREFIX2SMSC_ENTRY = 10000        /* Prefix 통합 : modified 2017.03.29 : 5000 -> 10000 */

    const val PREFIXFILE_NAME = "/bin/config/PREFIX.cnf"

    const val SMGSPREFIXFILE_NAME = "/bin/config/SMGSPREFIX.cnf"

    const val MAX_QINFO_ENTRY = 5000

    const val QINFOFILE_NAME = "/bin/config/QINFOR.cnf"

    const val MAX_EXEC_NAME_LEN = 100

    const val MAX_SMSIF_CHILD = 1000

    const val SMPPCFGFILE_NAME = "/bin/config/SMPPCFG.cnf"

    // #define SMS_SHM_MODE    0666
    const val SMS_SHM_MODE = 438  // 10진수

    const val SMS_FTOK_ID = 'A'

    const val SMSQ_SHM_PATH = "/APP/sms/bin/ipc/SmsQShm"

    const val SMSQ_SEM_PATH = "/APP/sms/bin/ipc/SmsQSem"

    const val INIT_QINFO_Q_ATTACH_FAILED = -1

    const val INIT_QINFO_READ_PREFIX_FAILED = -2

    const val INIT_QINFO_READ_QINFO_FAILED = -3

    const val INIT_QINFO_READ_GIENQ_FAILED = -4

    const val INIT_QINFO_READ_STATROUTE_FAILED = -5

    const val SMS_Q_LOCK_SUCCESS = 1

    const val SMS_Q_LOCK_FAIL_TRY_AGAIN = 0

    const val SMS_Q_LOCK_FAIL_INVALID_SEMID = -2

    const val SMS_Q_LOCK_FAIL_ACCESS_FAIL = -3

    const val SMS_Q_LOCK_FAIL_UNKNOWN = -4

    const val SMS_Q_UNLOCK_SUCCESS = 2

    const val SMS_Q_UNLOCK_FAIL_TRY_AGAIN = 0

    const val SMS_Q_UNLOCK_FAIL_INVALID_SEMID = -12

    const val SMS_Q_UNLOCK_FAIL_ACCESS_FAIL = -13

    const val SMS_Q_UNLOCK_FAIL_UNKNOWN = -14

    const val Q_INSERT_FAIL_INVALID_DEST_SMSC = -21

    const val Q_INSERT_FAIL_INVALID_QNO = -22


    const val Q_INSERT_FAIL_Q_FULL_TOFILE =
        6                                       /* SAVEQ : Added by KimJongha 2008.08.01 */

    const val Q_INSERT_FAIL_Q_FULL = 5

    const val Q_INSERT_SUCCESS = 3

    const val AGING_QUEUE_NOT_EXIST = -24

    const val Q_DELETE_SUCCESS = 4

    const val Q_DELETE_FAIL_Q_EMPTY = 6

    const val QTYPE_VMS = 0

    const val QTYPE_INFORM = 1

    const val QTYPE_BILL = 3

    const val QTYPE_PRI = 4

    const val QTYPE_GENERAL = 5

    const val QTYPE_AGING = 6

    const val QTYPE_IOND = 2

    const val QTYPE_IMO = 7

    const val QTYPE_IRES = 8

    const val QTYPE_IMOPLUS = 9

    const val QTYPE_CIMO = 10

    const val MSG_CODE_SM_REQ = 11

    const val MSG_CODE_SM_RES = 12

    const val MSG_CODE_SM_REP = 13

    const val MSG_CODE_SM_RSP = 14

    const val MSG_CODE_SUBS_REQ = 10

    const val MSG_CODE_SUBS_RES = 16

    const val MSG_CODE_ROUTE_REQ = 17

    const val MSG_CODE_ROUTE_RES = 18

    const val MSG_CODE_SM_MO = 19

    const val QTYPE_SM_REQ = MSG_CODE_SM_REQ

    const val QTYPE_SM_REP = MSG_CODE_SM_REP

    const val QTYPE_SM_REP_AGING = 19

    const val QTYPE_SM_BILL = 20

    const val MSG_CODE_DN_REQ = 21

    const val MSG_CODE_DN_RES = 22

    const val MSG_CODE_DN_REP = 23

    const val MSG_CODE_DN_RSP = 24

    const val QTYPE_DN_REQ = MSG_CODE_DN_REQ

    const val QTYPE_DN_REP = MSG_CODE_DN_REP

    const val QTYPE_DN_BILL = 30

    const val MSG_CODE_CYBER_REQ = 41

    const val MSG_CODE_CYBER_RES = 42

    const val QTYPE_CYBER_REQ = MSG_CODE_CYBER_REQ

    const val QTYPE_SM_MO: Int = 31

    val CYBER_REQ: Int
        get() = 32// 32비트에서는 CYBER_REQ 정의 안 됨

    const val SUB_QTYPE_CANCEL = 4

    const val SUB_QTYPE_VMS_NOTI = 5

    const val SUB_QTYPE_VMS_NOTI_STORE = 6

    const val SUB_QTYPE_VMS_NOTI_STORE_LEFT = 7

    const val SUB_QTYPE_FAX_NOTI_ARRIVAL = 8

    const val SUB_QTYPE_FAX_NOTI_STORE = 9

    const val SUB_QTYPE_FAX_NOTI_STORE_LEFT = 10

    const val SUB_QTYPE_DIGITAL_PAGING = 11

    const val SUB_QTYPE_ANNI = 12

    const val SUB_QTYPE_ALARM = 13

    const val SUB_QTYPE_SCHEDULE = 14

    const val SUB_QTYPE_CONGLATULATION = 15

    const val SUB_QTYPE_VMS_CONFIRM = 16

    const val SUB_QTYPE_RSV_CALL_FAIL = 17

    const val SUB_QTYPE_INFORM_SIMPLE = 20

    const val SUB_QTYPE_INFORM_MULTICAST = 21

    const val SUB_QTYPE_SCLMB_NOTI_SUSPEND = 30

    const val SUB_QTYPE_SCLMB_NOTI_UNCHARGED = 31

    const val SUB_QTYPE_SCLMP_PAY_NOTI = 41

    const val SUB_QTYPE_SCLMP_GREETING = 42

    const val SUB_QTYPE_SCLMP_SVC_RESTART = 43

    const val SUB_QTYPE_SCLMP_GENERAL = 44

    const val SUB_QTYPE_SCLMR_NOTI = 45

    const val SUB_QTYPE_IPDBIF_INFMSG = 46

    const val SUB_QTYPE_GLOABAL_NILMSG = 47

    const val SUB_QTYPE_SIMPLE_MT = 10

    const val SUB_QTYPE_PORTED_OUT_MT = 13

//const val  SUB_QTYPE_PORTED_OUT_MT= 48

    const val SUB_QTYPE_PORTED_OUT_MO = 49

    const val SUB_QTYPE_GSMS_MT = 50  /*  shchoi  2004-06-17 4:08오후 */

    const val SUB_QTYPE_GSMS_PORTEDOUT_MT = 51  /*  Patch Code : H2101  HJC 2004-08-20   */

    const val SUB_QTYPE_PORTED_NOTI_MT = 60  /* jcbyun 20040830 : H2100 */

    const val SUB_QTYPE_FWD_MT = 61

    const val SUB_QTYPE_PORTED_OUT_FWD_MT = 62

    const val SUB_QTYPE_RCS_TR = 71     /* 2012.11.22 dbpark*/

    const val SUB_QTYPE_IOND_ISTOP = 65525

    const val SUB_QTYPE_IOND_ILONG = 65526

    const val SUB_QTYPE_IOND_START = 65527

    const val SUB_QTYPE_IOND_MENU = 65528

    const val SUB_QTYPE_IOND_PROMPT = 65529

    const val SUB_QTYPE_IOND_MSG_MENU = 65530

    const val SUB_QTYPE_IOND_CALLDATA = 65531

    const val SUB_QTYPE_IOND_IPDATA = 65532

    const val SUB_QTYPE_IOND_END = 65533

    const val SUB_QTYPE_IOND_TELE = 65534

    const val SUB_QTYPE_IOND_BELL = 64000

    const val SUB_QTYPE_CBC_REQ = 65500

    const val SUB_QTYPE_CBC_CAN = 65501

    const val SUB_QTYPE_CBC_ACT = 65502

    const val SUB_QTYPE_CBC_INA = 65503

    const val SUB_QTYPE_CLONG = 63240     /* DB에는 COLNG COLORLONG */

    const val SUB_QTYPE_LONG = 63230

    const val SUB_QTYPE_CCSMS = 61825

    const val SM_REQ_CONNECT = 1

    const val SM_REQ_SEND = 2

    const val SM_REQ_LINK = 3

    const val SM_REQ_REPLACE = 4

    const val SM_REQ_CANCEL = 5

    const val SM_REQ_QUERY = 6

    const val SM_REQ_GET = 7

    const val SM_REQ_MSGID = 8

    const val SM_REQ_TRANS_RESULT = 9

    const val SM_REQ_SIMPLE = 10

    const val SM_REQ_MO = 31


    const val SM_REQ_MIN = 11

    const val SM_REQ_CLIENTID = 12

    const val SM_REQ_PORTED = 13

    const val SUB_QTYPE_REQ_BELL = 64000

    const val SUB_QTYPE_REQ_LOCAT = 65300

    const val SUB_QTYPE_CYBER_WEB_DN = 65504

    const val SUB_QTYPE_WAP_MAIL = 65490

    const val SUB_QTYPE_WAP_PUSH = 65491

    const val SUB_QTYPE_REQ_CONNECT = SM_REQ_CONNECT

    const val SUB_QTYPE_REQ_SEND = SM_REQ_SEND

    const val SUB_QTYPE_REQ_LINK = SM_REQ_LINK

    const val SUB_QTYPE_REQ_REPLACE = SM_REQ_REPLACE

    const val SUB_QTYPE_REQ_CANCEL = SM_REQ_CANCEL

    const val SUB_QTYPE_REQ_QUERY = SM_REQ_QUERY

    const val SUB_QTYPE_REQ_GET = SM_REQ_GET

    const val SUB_QTYPE_REQ_MSGID = SM_REQ_MSGID

    const val SUB_QTYPE_REQ_TRANS_RESULT = SM_REQ_TRANS_RESULT

    const val SUB_QTYPE_REQ_SIMPLE = SM_REQ_SIMPLE

    const val DN_SUBMIT = 3

    const val DN_DELIVER = 4

    const val DN_REPLACE = 5

    const val DN_CANCEL = 6

    const val DN_QUERY = 7

    const val DN_SUB_ACK = 8

    const val DN_USER_ACK = 9

    const val DN_MSGID = 10

    const val DN_REPLACE_RES = 11

    const val DN_CANCEL_RES = 12

    const val DN_QUERY_RES = 13

    const val SUB_QTYPE_DN_SUBMIT = DN_SUBMIT

    const val SUB_QTYPE_DN_DELIVER = DN_DELIVER

    const val SUB_QTYPE_DN_REPLACE = DN_REPLACE

    const val SUB_QTYPE_DN_CANCEL = DN_CANCEL

    const val SUB_QTYPE_DN_QUERY = DN_QUERY

    const val SUB_QTYPE_DN_SUB_ACK = DN_SUB_ACK

    const val SUB_QTYPE_DN_USER_ACK = DN_USER_ACK

    const val SUB_QTYPE_DN_MSGID = DN_MSGID

    const val SUB_QTYPE_DN_REPLACE_RES = DN_REPLACE_RES

    const val SUB_QTYPE_DN_CANCEL_RES = DN_CANCEL_RES

    const val SUB_QTYPE_DN_QUERY_RES = DN_QUERY_RES

    const val NPDB_QUERY = 20

    const val SM_STATE_PORTED_OUT_HSMSS = 8

    const val SM_STATE_NPDB_ERR = 9

    const val SM_STATE_FORWARD_HSMSS = 10

//const val  SM_STATE_NCHANGE=			13

    const val SM_STATE_FWD_CNT_OVER =
        14                        /* FWD_CNT Code 수정 : Modified by KimJongha 2008.08.07 */

    const val SM_STATE_SPAM_BLOCK = 16                        /* 2013.05.13 - 140bytes & UCS2 */

    const val SM_STATE_FWD_DETECT_CID = 0                       /* 2013.10.14 - 2013_2Q_PN/CR */

    const val SM_STATE_MRMSPAM = 1                        /* 2015.07.27 - added by jws cause code */
    const val SM_STATE_DELIVERED = 2                      /* 전송 완료 (SEND_OK와 동일) */

    const val ERRORID_CP_INSERTQ_FAIL = 24

    const val ST_Q_FULL_SMSMANAGER = -228

    const val ST_Q_INSERT_FAIL_SMSMANAGER = -301

    const val ST_GIPALL_SMSMGR_OK = 72

    const val ERRORID_CP_MO_SUCCESS = 15
    const val ERRORID_CENTER_MT_SUCCESS = 32
    const val ST_SMSMOT_SOCK_SEND_FAIL = -341

    const val ERRORID_MO_SUCCESS = 35
    const val ERRORID_CENTER_MTQ_FULL = 40
    const val ERRORID_CENTER_MTQ_FAIL = 41

    // MO-TR 관련 통계/에러 코드 (C inc/StatDef.h, inc/TraceDef.h 기준)
    const val ERRORID_CP_MO_TR_FAIL = 28            /* MO-TR 과금/처리 실패 */
    const val ERRORID_CP_MO_TR_SUCCESS = 27         /* MO-TR 과금 성공 */

    // C 코드 기준: Center TR 관련 ErrorId (C 코드에서 사용하는 값)
    const val ERRORID_CENTER_TR_SUCCESS = 2         /* C 코드: msgStatus==2일 때 사용 */
    const val ERRORID_CENTER_TR_EXPIRED = 3         /* C 코드: msgStatus==3일 때 사용 */
    const val ERRORID_CENTER_TR_PORTOUT = 7         /* C 코드: msgStatus==7,8,9,10일 때 사용 */
    const val ST_GIPEVENT_MOTR_OK = 66              /* 성공(MO TR) */
    const val ST_Q_INSERT_FAIL_VBILLMO = -316       /* 큐 입력 오류(VBILLMO) */


    const val GIPEVENT_BLOCK_NOTI_CID = "3333333310"
    const val GIPEVENT_BLOCK_NOTI_CALLBACK = "114"

    const val ST_GIPEVENT_INSQ_POLL = 87

    const val ST_GIP_MO_LIMIT = -155

    const val ST_Q_INSERT_FAIL_BLOCKNOTI = -248

    const val ST_GIPEVENT_INSQ_BLOCKNOTI = 88

    const val MODULEID_GIPEVENT_C = 31
    const val MODULEID_VBILLMO = 39  // VBILL MO 모듈 ID
    const val MODULEID_SMSMOR = 13  // SMSMOR 모듈 ID
    const val SERVICEID_GIPEVENT = 45
    const val SERVICEID_GIPM = 41  // GIPM 서비스 ID
    const val SERVICEID_NOTISENDING = 61
    const val SERVICEID_NOTISENT = 62
    const val ERRORID_CP_MO_FAIL = 16
    const val ERRORID_CENTER_MO_SUCCESS = 35  // Center MO 성공
    const val ST_Q_INSERT_FAIL_POLL = -299
    const val MODULEID_NOTISEND = 34

    const val MODULEID_GIPALL_C = 27

    const val SERVICEID_GIPALL = 43

    const val ERRORID_CP_INVALID_SUBSCRIBER = 23

    const val ST_GIP_INVALID_SMIN_SMSMANAGER = -153

    const val ERRORID_CP_TR_TOTAL = 17
    const val ERRORID_CP_TR_SUCCESS = 18
    const val ERRORID_CP_TR_FAIL = 19
    const val ST_GIP_SOCK_SEND_FAIL = -160
    const val ST_GIPALL_MTTR_SEND_OK = 81            /*전송(MTTR)*//*MsgStatus*/

    // ProcessSMRes 관련 상수
    // C 오리지널 기준(inc/TraceDef.h):
    // - ST_GIPEVENT_MO_OK = 56 (성공(MO))
    // - ST_GIPEVENT_MORS_OK = 61 (성공(MO RES))
    // - ST_GIPEVENT_MTTR_OK = 71 (성공(MT TR))
    // - ST_GIPEVENT_MOACK_BILL_OK = 80 (과금성공(MOACK))
    // - ST_DB_NO_DATA_GIPMOCALLINFO = -125
    // - ST_GIP_MORS_FAIL = -154
    // - ST_GIP_INVALID_CID = -144
    const val ST_GIPEVENT_MO_OK = 56                 /* 성공(MO) */
    const val ST_GIPEVENT_MT_OK = 46                 /* 성공(MT) */
    const val ST_GIPEVENT_MORS_OK = 61               /* 성공 (MO RES) */
    const val ST_GIP_MORS_FAIL = -154                /* MO RES 실패 */
    const val ST_GIPEVENT_MTTR_OK = 71               /* 성공 (MT TR) */
    const val ST_DB_NO_DATA_GIPMOCALLINFO = -125     /* DB NO DATA(GIPMOCALLINFO) */
    const val ST_GIPEVENT_MOACK_BILL_OK = 80         /* 과금성공(MOACK) */
    const val ST_GIP_INVALID_CID = -144              /* Invalid CID */
    const val ST_NOTISEND_OK = 182                   /* 성공 */

    const val SM_STATE_EXPIRED = 3                        /* validity period expired */

    const val SM_STATE_DELETED = 4                        /* message has been deleted */

    const val SM_STATE_UNDELIVERABLE = 5                       /* 2013.04.08 - 140bytes & UCS2 */

    const val SM_STATE_ACCEPTED = 6                        /* message is in accepted state */

    const val SM_STATE_PORTED_OUT = 7                        /* 2012.09.14 */

    const val SM_STATE_PORTEDOUT = 7

    const val SM_STATE_PORTEDOUT_KTF = 8

    const val SM_STATE_PORTEDOUT_LGT = 9

    const val SM_STATE_PORTEDOUT_SKT = 10

    const val SM_STATE_FORWARD = 12

    const val SM_STATE_NCHANGE = 13                        /* 2015.06.15 added by jws: NCHANGE */

    const val SM_STATE_FWDFAIL = 14                      /* 2013.04.08 - 140bytes & UCS2 */

    const val SM_STATE_SPAMERR = 16                      /* 2013.04.08 - 140bytes & UCS2 */

    const val SM_STATE_USERDEL = 17                      /* 2015.06.15 - added by jws cause code */

    const val SM_STATE_NPREFIX = 19                      /* 2015.06.15 - added by jws cause code */

    const val SM_STATE_ADMCANC = 20                      /* 2015.06.15 - added by jws cause code */

    const val MAX_GIENQ_ENTRY = 10000

    const val GIENQ_ENTRY_FILE_NAME = "/bin/config/GIEnq.cnf"

    const val MAX_STATROUTE_ENTRY = 100

    const val STATROUTE_ENTRY_FILE_NAME = "/bin/config/StatRoute.cnf"

    const val MAX_CID2VIRTUALMIN_ENTRY = 100

    const val MAX_TCPCYBERACCESSLIST_ENTRY = 1000

    const val TCPCYBERACCESSFILE_NAME = "/bin/config/GITcpCyberAccessList.cnf"

    const val MAX_GIADDR_ENTRY = 100

    const val DATA_TYPE_TEXT_ENG = 0

    const val DATA_TYPE_TEXT_KOR = 1

    const val DATA_TYPE_TEXT = 2

    const val DATA_TYPE_BINARY = 3    /* Don't Care */

    const val TERM_TYPE_ENG = '0'

    const val TERM_TYPE_KOR = '1'

    const val DCS_TYPE_GSM7_00 = 0x00.toChar()

    const val DCS_TYPE_GSM7 = 0x02.toChar()        /* 0x00 */

    const val DCS_TYPE_ASCII7 = 0x01.toChar()

    const val DCS_TYPE_8BIT = 0x04.toChar()

    const val DCS_TYPE_UCS2 = 0x08.toChar()

    const val DCS_TYPE_KSC5601 = 0x0E.toByte()

    const val DCS_TYPE_BINARY = 0xF6.toChar()    /* M2M RSP */

    const val DCS_TYPE_UNKNOWN = 0xFF.toChar()

    const val DCS_TYPE_DEC_GSM7 = 2        /* DB */

    const val DCS_TYPE_DEC_ASCII7 = 1

    const val DCS_TYPE_DEC_8BIT = 4

    const val DCS_TYPE_DEC_UCS2 = 8

    const val DCS_TYPE_DEC_KSC5601 = 14

    const val DCS_TYPE_DEC_BINARY = 246

    const val DCS_TYPE_DEC_UNKNOWN = 255

    const val GI_RES_NO_ERR = 0

    const val GI_RES_UNKNOWN_PREFIX = 5

    const val GI_RES_UNKNOWN_MRM = 6


    const val GI_RES_SUBS_INVALID = 10

    const val GI_RES_SUBS_SUSPENDED = 11

    const val GI_RES_SUBS_INVALID_TERM = 12

    const val GI_RES_SUBS_INVALID_TERM_KOR = 13

    const val GI_RES_SUBS_INVALID_TERM_ENG = 14

    const val GI_RES_SUBS_INVALID_CALLNO = 15

    const val GI_RES_FORMAT_INVALID = 20

    const val GI_RES_FORMAT_INVALID_CID = 21

    const val GI_RES_FORMAT_INVALID_MSG_CODE = 22

    const val GI_RES_FORMAT_INVALID_MSG_SUBCODE = 23

    const val GI_RES_FORMAT_INVALID_MSG_SEQNO = 24

    const val GI_RES_FORMAT_INVALID_DATA_TYPE = 25

    const val GI_RES_OPMASK_DENY_FLAG = 40

    const val GI_RES_OPMASK_DENY_VLDPRD = 41

    const val GI_RES_OPMASK_DENY_PRIOROTY = 42

    const val GI_RES_OPMASK_DENY_REPFLG = 43

    const val GI_RES_OPMASK_DENY_RGTDLV = 44

    const val GI_RES_OPMASK_DENY_OPERATION = 50

    const val GI_RES_OPMASK_DENY_CONNECT = 51

    const val GI_RES_OPMASK_DENY_SEND = 52

    const val GI_RES_OPMASK_DENY_LINK = 53

    const val GI_RES_OPMASK_DENY_REPLACE = 54

    const val GI_RES_OPMASK_DENY_CANCEL = 55

    const val GI_RES_OPMASK_DENY_QUERY = 56

    const val GI_RES_OPMASK_DENY_GET = 57

    const val GI_RES_FC_NAK = 60

    const val GI_RES_Q_INSERT_FAILED = 61

    const val GI_RES_INVALID_VERID = 62

    const val GI_RES_SPAM = 63        /* 2013.04.08 - 140bytes & UCS2 */

    const val GI_RES_UNKNOWN_PRIORITY = 68

    const val PAGE_ROW_DATALIST = 5

    const val MAX_LOG_LEVEL = 3

    const val LOG_CRITICAL = 0

    const val LOG_ERROR = 0

    const val LOG_WARNING = 0

    const val LOG_REPAIR = 0

    const val LOG_NORMAL = 1

    const val LOG_DEBUG = 2

    const val LOG_SYS = 2

    const val LOG_CONFIG = 2

    const val LOG_MSG_DEBUG = 3

    const val LOG_TCP_DATA = 3

    const val DEF_VAL_NOT_W_ZONE = '0'

    const val DEF_VAL_W_ZONE = '1'

    const val CB_PARAM_HEAD =
        0x07.toChar()                                    /*CB 구분자변경 : Added by KimJongha 2008.03.05*/

    const val CB_PARAM_TAIL = 0x07.toChar()

    const val CB_PARAM_URL =
        0x07.toChar()                                    /*CB 구분자변경 : Added by KimJongha 2008.03.05*/

    const val TELESVCID_GLOBAL_MMS =
        61867                                   /*GLOBAL_MMS : Added by KimJongha 2008.03.27*/

    const val TELESVCID_GWPUSH = 61859

    const val TELESVCID_SMMS = 61865

    const val TELESVCID_MOZEN = 65535

    const val TELESVCID_MMCS = 62001        /* 2023.03 Message Multi Channel Service (OASIS) */

    const val TELESVCID_MMCS1 = 62002        /* 2023.03 Message Multi Channel Service (OTP인증) */

    const val TELESVCID_MMCS2 = 62003        /* 2023.03 Message Multi Channel Service (등기문자) */

    const val TELE_SVC_ID_WLONG = 61842

    const val TELE_SVC_ID_GLONG = 62867           /* 2016.02.24 - PCS v1.84 GLONG */

    const val SEG_SIZE = 6

    const val TID_SIZE = 6

    const val MSG_PCS_BIND = 0

    const val MSG_PCS_BIND_ACK = 1

    const val MSG_PCS_DELIVER = 2

    const val MSG_PCS_DELIVER_ACK = 3

    const val MSG_PCS_REPORT = 4

    const val MSG_PCS_REPORT_ACK = 5

    const val MSG_PCS_PDELIVER = 6

    const val MSG_PCS_PDELIVER_ACK = 7

    const val TELEID_ENTRY_FILE_NAME = "/bin/config/TELEID.cnf"

    const val MAX_ACCESS_LIST_ENTRY = 200

    const val MSGENCODING_ENTRY_FILE_NAME = "/bin/config/MSGENCODING.cnf"

    const val MAX_DUP_MSG_CNT =
        100                                    /*CHECK_DUP_MSG : Added by KimJongha 2008.04.07*/

    const val DUP_MSG_EMPTY = 0                                    /*CHECK_DUP_MSG : Added by KimJongha 2008.04.07*/

    const val MAX_VAILD_PERIOD =
        86400                                    /* Define : Added by KimJongha 2008.09.29 */

    const val TYPE_SMPP_SIMPLE = 0
    const val TYPE_SMPP_CLIENTID = 1
    const val RGTDLV_FLAG_0 = 0
    const val RGTDLV_FLAG_2 = 2

    const val CFG_OFF = 0

    const val CFG_ON = 1

    const val STR_SMPP3A_ON_FLAG = "SMPP3A_ON_FLAG"    // shchoi 2010-05-10 오후 6:05:37

    const val STR_SMPP3A_ON_CBCHK_FLAG = "STR_CBCHK_FLAG"    /* dbpark 2010.10.27 */

    const val STR_MT_PARSE_CALLBACK = "MT_PARSING_CALLBACK"    /* dbpark 2011.07.14 */

    const val STR_MO_PARSE_CALLBACK = "MO_PARSING_CALLBACK"    /* dbpark 2011.07.14 */

    const val EWOULDBLOCK_SLEEP = "EWOULDBLOCK_SLEEP"

    const val EWOULDBLOCK_TRYCNT = "EWOULDBLOCK_TRYCNT"

    const val APPLY_NEW_SMSMANAGER = "APPLY_NEW_SMSMANAGER"

    const val FSMSC_MAX_USAGE = "FSMSC_MAX_USAGE"

    const val KISA_VLDCHK_ONOFF = "KISA_VLDCHK_ONOFF"

    const val CALLHISTORY = "CALLHISTORY"

    const val OCS_QUEUE = "OCS_QUEUE"

    const val AISURVEY_RELAY_QUEUE = "AISURVEY_RELAY_QUEUE"

    const val CALLFWD_BLK_NOTI = "CALLFWD_BLK_NOTI"

    const val AUTOCOMMIT_FLAG =
        0                                              /* 0 : Auto Commit OFF,   1 : Auto Commit On */

    const val COMMON_SMS = 0         /* 2012.11.22 dbpark */

    const val SMS_TO_RCS = 1

    const val RCS_TO_SMS = 2

    const val RCS_RESULT_SENT = 6

    const val RCS_RESULT_INCALIDDST = 7

    const val RCS_RESULT_POWEROFF = 8

    const val RCS_RESULT_HIDDEN = 9

    const val RCS_RESULT_TERMFULL = 10

    const val RCS_RESULT_ETC = 11

    const val RCS_RESULT_PORTED_OUT = 13

    const val RCS_RESULT_FWD = 14

    const val MAX_RSP_CID_ENTRY = 100

    const val MAX_DCS_7BIT_ENTRY = 100

    const val MAX_TRANSFORM_LIST = 10000

    const val CALLFW_ADDMSG_LEN = 8

    const val MAX_AUTH_CALLBACK_LIST = 100

    const val MAX_AUTH_CALLBACK_LEN = 21

    const val RUNNING_THREAD = 1

    const val STOP_THREAD = 0

    const val HTTP_Q_INSERT_FAIL_Q_FULL = 8500005
    const val HTTP_REQ_FAIL_UNKNOWN_OBJ = 8500010
    const val HTTP_REQ_FAIL_NULL_OBJ = 8500011
    const val HTTP_REQ_FAIL_UNKNOWN_MSG_TYPE = 8500008
    const val HTTP_REQ_FAIL_UNSURPPORT_MSG_TYPE = 8500009

    const val ALTI_NODATA = 0
    const val ALTI_SUCCESS = 1
    const val ALTI_FAIL = -1

    const val ERRORID_CP_MT_LIMIT = 1
    const val ERRORID_CP_MO_LIMIT = 2
    const val ERRORID_CP_GIVEBILL_LIMIT = 3

    /* CFG_ETC Table Select Key-word */
    const val KEY_NS_START_TM = "NS_START_TM"
    const val KEY_NS_END_TM = "NS_END_TM"
    const val KEY_NS_ONOFF_FLAG = "V_SPAM_ONOFF_FLAG"
    const val KEY_NS_INTERVAL_TM = "NS_INTERVAL_TM"

    /*BILLTYPE*/
    const val BILLTYPE_NONE = 0
    const val BILLTYPE_NOT = 1
    const val BILLTYPE_SRC = 2
    const val BILLTYPE_DESC = 3
    const val BILLTYPE_GIVE = 4
    const val BILLTYPE_CNT = 5

    const val AI_SURVEY_NUMBER = "05016";
    const val AI_SURVEY_NUMBER_LENGTH = 5;

    const val PTOPFILE_NAME = "/bin/config/PtoPList.cnf";

    const val IF_NULL = -1

    const val TID_SAVE = 0
    const val TID_NO_SAVE = 1

    /** TRACE 파일만 생성 LT_BOTH 외 나머지 구간은 LT_TRACE*/
    const val LT_TRACE = 0

    /** 통계 파일과 TRACE 모두 생성  --> 엔드포인트 시점에 저장, 요청 건당 1번, /home4/sms/StatLog/VSTAT 에서 확인*/
    const val LT_BOTH = 1

}