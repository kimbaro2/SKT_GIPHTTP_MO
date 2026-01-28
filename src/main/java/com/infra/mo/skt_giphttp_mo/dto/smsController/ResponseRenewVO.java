package com.infra.mo.skt_giphttp_mo.dto.smsController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infra.mo.skt_giphttp_mo.utils.TimeZoneConstants;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseRenewVO {
    private static final String SEOUL_TIMEZONE = "Asia/Seoul";
    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of(SEOUL_TIMEZONE);

    @NotNull
    public Integer status;  /* HTTP status */

    @NotNull
    public String msgId;  /* 위트콤 관리 번호 */

    @NotNull
    public LocalDateTime serverTime;  /* 서버 처리 시각 */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Error error;  /* 에러 오브젝트 */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ResponseTR responseTR;  /* TR 결과 데이터 (null이 아닐 때만 포함) */

    // ✅ Error 내부 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        private Integer msgStatus;         /* 에러 코드 */
        private String type;         /* 에러 타입 */
        private String description;  /* 에러 원인 설명 */
    }

    // ✅ 간단 응답 생성 (builder context safe)
    public ResponseRenewVO toSimpleResponseVO(HttpStatus httpStatusCode) {
        return ResponseRenewVO.builder()
                .status(httpStatusCode.value())
                .msgId(this.msgId)
                .serverTime(LocalDateTime.now(TimeZoneConstants.SEOUL_ZONE_ID))
                .build();
    }

    // ✅ 정적 팩토리 메서드 (성공 응답용)
    public static ResponseRenewVO success(HttpStatus status, String msgId) {
        return ResponseRenewVO.builder()
                .status(status.value())
                .msgId(msgId)
                .serverTime(LocalDateTime.now(TimeZoneConstants.SEOUL_ZONE_ID))
                .responseTR(new ResponseTR(5, 0, new ResponseTR.DataBody()))
                .build();
    }

    // ✅ 정적 팩토리 메서드 (에러 응답용)
    public static ResponseRenewVO error(HttpStatus status, Integer msgStatus, String type, String description) {
        return ResponseRenewVO.builder()
                .status(status.value())
                .serverTime(LocalDateTime.now(TimeZoneConstants.SEOUL_ZONE_ID))
                .error(new Error(msgStatus, type, description))
                .build();
    }

    // 프로덕션 환경에서는 디버그용 main 메서드 제거
    // 개발/테스트 환경에서만 필요시 주석 해제하여 사용
    /*
    public static void main(String[] args) {
        printJson(ResponseRenewVO.success(HttpStatus.OK, "1FAFED6B2"));
        printJson(ResponseRenewVO.error(HttpStatus.BAD_REQUEST, 0, "UNKNOWN_TYPE", "알 수 없는 상태"));
    }

    private static void printJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            log.debug("ResponseRenewVO JSON: {}", mapper.writeValueAsString(obj));
        } catch (Exception e) {
            log.error("JSON 변환 실패: {}", e.getMessage(), e);
        }
    }
    */
}
