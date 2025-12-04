package com.infra.mo.skt_giphttp_mo.dto.smsController;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@NoArgsConstructor
@JsonDeserialize(using = Rsv4ProtocolItem.Rsv4ProtocolItemDeserializer.class)
public class Rsv4ProtocolItem {
    // JSON에서 int 값을 받아서 char로 변환
    // Jackson이 int를 직접 char로 deserialize할 수 없으므로 Integer로 받고 변환
    private Integer value;
    
    // JSON 직렬화 시 char로 변환
    @JsonValue
    public char getData() {
        if (value == null) {
            return 0;
        }
        // int 값을 char로 안전하게 변환
        return (char) (value & 0xFFFF);
    }
    
    // 기본 생성자와 함께 사용할 수 있도록 Integer를 받는 생성자 추가
    public Rsv4ProtocolItem(Integer value) {
        this.value = value;
    }
    
    // 기존 코드 호환성을 위한 setter
    public void setData(char data) {
        this.value = (int) data;
    }
    
    /**
     * JSON 역직렬화를 위한 커스텀 Deserializer
     * 배열 요소로 사용될 때도 작동하도록 구현
     */
    public static class Rsv4ProtocolItemDeserializer extends JsonDeserializer<Rsv4ProtocolItem> {
        @Override
        public Rsv4ProtocolItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // JSON에서 int 값을 읽어서 Rsv4ProtocolItem 생성
            // null이거나 숫자가 아닌 경우 0으로 처리
            if (p.getCurrentToken().isNumeric()) {
                int intValue = p.getIntValue();
                return new Rsv4ProtocolItem(intValue);
            } else {
                // null이거나 숫자가 아닌 경우 0으로 처리
                return new Rsv4ProtocolItem(0);
            }
        }
    }
}
