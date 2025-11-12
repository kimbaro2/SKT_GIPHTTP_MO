package com.infra.mo.skt_giphttp_mo.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES256 암호화 예제 클래스
 * 키 생성부터 암호화, 복호화까지의 전체 과정을 보여줍니다.
 */
public class EncryptionExample {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static void main(String[] args) {
        /*
        * {
  "status": 200,
  "resultCode": "0",
  "msgId": "1FB03ADC1",
  "serverTime": "2025-08-25T17:28:17.0929133",
  "access": {
    "aes_key_base64": "kH9s5aq3BKEIR195FlOPwDzNgjKVxvU17+dxl6+03GQ=",
    "iv_base64": "M/e18C89u/qnupbeYcZVwg=="
  }
}
        *
        * */


        try {
            System.out.println("🔐 AES256 암호화 예제 시작");
            System.out.println("=".repeat(50));

            // 1. 키 생성
            String srcCID = "1577000001";
            String ipAddr = "127.0.0.1";


            System.out.println("11⃣ 키 생성");
//            String ivBase64 = AesKeyFileGenerator.generateRandomIvToBase64();
//            String aesKeyBase64 = AesKeyFileGenerator.generateRandomKeyToBase64(srcCID, ipAddr);
            String ivBase64 = "M/e18C89u/qnupbeYcZVwg=="; // 예제용 고정 IV
            String aesKeyBase64 = "kH9s5aq3BKEIR195FlOPwDzNgjKVxvU17+dxl6+03GQ="; // 예제용 고정 키


            System.out.println("   IV (Base64): " + ivBase64);
            System.out.println("   AES Key (Base64): " + aesKeyBase64);
            System.out.println();

            // 2. 암호화할 데이터
            String destCID = "010";
            String destCallNo = "41901234";
            String callback = "01000000001";
            String msg = "hKOx7KO87ZWt7IqkVEVTVO2VnO2VnTIyMzQ=";

            System.out.println("2️2 암호화할 원본 데이터");
            String plainText = String.format("destCID:%s|destCallNo:%s|callback:%s|msg:%s", destCID, destCallNo, callback, msg);
            System.out.println(plainText);

            // 3. 암호화
            System.out.println("3️3 AES256 암호화 수행");
            String destCID_Enc = encryptWithKey(destCID, aesKeyBase64, ivBase64);
            String destCallNo_Enc = encryptWithKey(destCallNo, aesKeyBase64, ivBase64);
            String callback_Enc = encryptWithKey(callback, aesKeyBase64, ivBase64);
            String msg_Enc = encryptWithKey(msg, aesKeyBase64, ivBase64);

            String encrypted = String.format("\ndestCID:[%s]\ndestCallNo:[%s]\ncallback:[%s]\nmsg:[%s]",
                    destCID_Enc, destCallNo_Enc, callback_Enc, msg_Enc);
            System.out.println("   암호화 결과: " + encrypted);

            // 4. 복호화
            System.out.println("4️4 AES256 복호화 수행");
            String destCID_Dec = decryptWithKey(destCID_Enc, aesKeyBase64, ivBase64);
            String destCallNo_Dec = decryptWithKey(destCallNo_Enc, aesKeyBase64, ivBase64);
            String callback_Dec = decryptWithKey(callback_Enc, aesKeyBase64, ivBase64);
            String msg_Dec = decryptWithKey(msg_Enc, aesKeyBase64, ivBase64);
            String decrypted = String.format("destCID:%s|destCallNo:%s|callback:%s|msg:%s",
                    destCID_Dec, destCallNo_Dec, callback_Dec, msg_Dec);
            System.out.println("   복호화 결과: " + decrypted);

            // 5. 검증
            System.out.println("5️5 검증");
            boolean isMatchDestCID = destCID.equals(destCID_Dec);
            boolean isMatchDestCallNo = destCallNo.equals(destCallNo_Dec);
            boolean isMatchCallback = callback.equals(callback_Dec);
            boolean isMatchMsg = msg.equals(msg_Dec);

            System.out.println("   destCID 일치: " + (isMatchDestCID ? "✅" : "❌"));
            System.out.println("   destCallNo 일치: " + (isMatchDestCallNo ? "✅" : "❌"));
            System.out.println("   callback 일치: " + (isMatchCallback ? "✅" : "❌"));
            System.out.println("   msg 일치: " + (isMatchMsg ? "✅" : "❌"));
            System.out.println();

            // 6. 다양한 데이터 타입 테스트
            System.out.println("6️⃣ 다양한 데이터 타입 테스트");
            testVariousDataTypes(aesKeyBase64, ivBase64);

        } catch (Exception e) {
            System.err.println("❌ 암호화 예제 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * AES256 암호화 메서드
     */
    public static String encryptWithKey(String plainText, String keyBase64, String ivBase64) throws Exception {
        // Base64 디코딩
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);

        // 키와 IV 설정
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // 암호화 수행
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES256 복호화 메서드
     */
    public static String decryptWithKey(String encryptedBase64, String keyBase64, String ivBase64) throws Exception {
        // Base64 디코딩
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        // 키와 IV 설정
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // 복호화 수행
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 다양한 데이터 타입에 대한 암호화 테스트
     */
    private static void testVariousDataTypes(String keyBase64, String ivBase64) throws Exception {
        String[] testData = {
                "숫자만: 1234567890",
                "영문만: Hello World",
                "한글만: 안녕하세요",
                "특수문자: !@#$%^&*()",
                "혼합: Hello 안녕 123 !@#",
                "긴문자열: " + "A".repeat(100),
                "빈문자열: ",
                "한글+숫자: 가나다라마바사 1234567890"
        };

        for (int i = 0; i < testData.length; i++) {
            String original = testData[i];
            String encrypted = encryptWithKey(original, keyBase64, ivBase64);
            String decrypted = decryptWithKey(encrypted, keyBase64, ivBase64);

            boolean success = original.equals(decrypted);
            System.out.println("   테스트 " + (i + 1) + ": " + (success ? "✅" : "❌"));
            System.out.println("      원본: " + original);
            System.out.println("      암호화: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
            System.out.println("      복호화: " + decrypted);
            System.out.println();
        }
    }
}
