package com.infra.mo.skt_giphttp_mo.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class CommonUtil {

	public static String replaceRcsBody (String rcsBody) {
		String replace_rcsBody = rcsBody.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>").replaceAll("\\n", "<br>").replaceAll("\\/", "/").replaceAll("\\t", "    "); 
		return replace_rcsBody;
	}
	
	
	public static String escapeRcsBody (String rcsBody) {
		String replace_rcsBody = rcsBody.replace("\\\"(", "(").replace("\\\")", ")").replace("\\\"\\u", "\\u").replace("\\\"", "\"");
		return replace_rcsBody;
	}
	
	//BC 카드 데이터가 이스케이프 처리 되어 들어옴
	public static String bcEscapeRcsBody (String rcsBody) {
		String replace_rcsBody = rcsBody.replace("\\\"(", "(").replace("\\\")", ")").replace("\\\"", "").replace("\\\"", "").replace("\\\\", "").replace("\\\"", "\"");
		return replace_rcsBody;
	}
	
	public static String getPrintStackTrace(Exception e) {

		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		return errors.toString();
	}
	
	public static String getNowDttm() {
    	Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(cal.getTime());
    }
	
	/**
	 * 빈 문자열인지 체크한다.
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().isEmpty()) return true;
		return false;
	}
	
	/**
	 * 문자열에서 숫자만 반환
	 * 
	 * @param str
	 * @return
	 */
	public static String getOnlyNum(String str) {
		if (str == null) return "";
		return str.replaceAll("[^0-9]", "");		// 숫자이외의 값을 삭제
	}
	
	/**
	 * 문자 수신이 가능한 폰번호 인지 체크
	 * @param phone
	 * @return
	 */
	public static boolean isValidRecvPhone(String phone) {
    	if (isEmpty(phone) == true) return false;
    	
    	String regEx = "^(01[016789]{1}|050[0-9]{0,2})-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$";		// "^(01[016789]{1}|050)[0-9]{3,4}[0-9]{4}$";
    	return Pattern.matches(regEx, phone.trim());
    }
	
	/**
	 * 문자 발송이 가능한 폰번호 인지 체크
	 * @param phone
	 * @return
	 */
	public static boolean isValidCallback(String phone) {
		if (isEmpty(phone) == true) return false;
    	
		phone = phone.trim();
    	if (Pattern.matches("^01([01256789])-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^0(2|31|32|33|41|42|43|44|51|52|53|54|55|61|62|63|64)-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^0(30|50|60|70|80)-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^13(30|31|32|33|35|36|37|38|50|55|57|65|66|69|72|77|79|82|85|88|90|97|98|99)$", phone)) return true;
    	if (Pattern.matches("^1[568][0-9]{2}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^1[568][0-9]{2}$", phone)) return true;
    	if (Pattern.matches("^1(00|01|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|82|88)$", phone)) return true;
    	
		/*if (Pattern.matches("^01([01256789])[0-9]{7,8}$", phone)) return true;
		if (Pattern.matches("^0(2|31|32|33|41|42|43|44|51|52|53|54|55|61|62|63|64)[0-9]{7,8}$", phone)) return true;
		if (Pattern.matches("^0(30|50|60|70|80)[0-9]{7,9}$", phone)) return true;
		if (Pattern.matches("^13(30|31|32|33|35|36|37|38|50|55|57|65|66|69|72|77|79|82|85|88|90|97|98|99)$", phone)) return true;
		if (Pattern.matches("^1[568][0-9]{6,6}$", phone)) return true;
		if (Pattern.matches("^1(00|01|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|82|88)$", phone)) return true;*/
    	return false;
    }
	
	// #1544, *1544 허용
	public static boolean isValidCallbackSharp(String phone) {
		if (isEmpty(phone) == true) return false;
    	
		phone = phone.trim();
    	if (Pattern.matches("^01([01256789])-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^0(2|31|32|33|41|42|43|44|51|52|53|54|55|61|62|63|64)-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^0(30|50|60|70|80)-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^13(30|31|32|33|35|36|37|38|50|55|57|65|66|69|72|77|79|82|85|88|90|97|98|99)$", phone)) return true;
    	if (Pattern.matches("^1[568][0-9]{2}-{0,1}[0-9]{4}$", phone)) return true;
    	if (Pattern.matches("^[*#]{0,1}1[568][0-9]{2}$", phone)) return true;
    	if (Pattern.matches("^1(00|01|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|82|88)$", phone)) return true;
    	
		/*if (Pattern.matches("^01([01256789])[0-9]{7,8}$", phone)) return true;
		if (Pattern.matches("^0(2|31|32|33|41|42|43|44|51|52|53|54|55|61|62|63|64)[0-9]{7,8}$", phone)) return true;
		if (Pattern.matches("^0(30|50|60|70|80)[0-9]{7,9}$", phone)) return true;
		if (Pattern.matches("^13(30|31|32|33|35|36|37|38|50|55|57|65|66|69|72|77|79|82|85|88|90|97|98|99)$", phone)) return true;
		if (Pattern.matches("^1[568][0-9]{6,6}$", phone)) return true;
		if (Pattern.matches("^1(00|01|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|82|88)$", phone)) return true;*/
    	return false;
    }
	
	/**
	 * 광고수신 거부용 번호인지 체크 (080)
	 * @param phone
	 * @return
	 */
	public static boolean isRejectTel(String phone) {
		if (isEmpty(phone) == true) return false;
    	return Pattern.matches("^080-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone.trim());
	}
	
	/**
	 * 국제전화 번호 형식이 맞는지 체크 TODO : 보완 필요
	 * @param phone
	 * @return
	 */
	public static boolean isValidGlobalPhone(String phone) {
		if (isEmpty(phone) == true) return false;
		if (isValidCallbackSharp(phone) == true) return true;
    	return Pattern.matches("^\\+[0-9]{1,5}{1}-{0,1}[0-9]{2,3}-{0,1}[0-9]{3,4}-{0,1}[0-9]{4}$", phone.trim());
	}
	
	/**
	 * 광고 문자로 시작하는지 체크
	 * 
	 * @param text
	 * @return 
	 */
	public static boolean isAdvertising(String text) {
		if (isEmpty(text) == true) return false;
		if (text.startsWith("광고") == true) return true;
		if (text.startsWith("(광고)") == true) return true;
		if (text.startsWith("[광고]") == true) return true;
		//if (Pattern.matches("^(광고|\\(광고\\)|\\[광고\\]).*", text)) return true;
		return false;
	}
	
	/**
	 * 첫줄에 광고관련 문자가 들어가는지 체크 (앞 10글자만 체크한다)
	 * 
	 * @param rcsBody
	 * @return
	 */
	public static boolean isAdvertisingSpam(String text) {
		if (isEmpty(text) == true) return false;
		int length = text.length();
		if (length > 16) { length = 10; } // 시작 16 글자만 체크한다.
		String startText = text.substring(0, length);
		
		return Pattern.matches("^.*광[^가-힣]*고.*$", startText);
	}
	
	/**
	 * URL 형식을 체크한다.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidUrl(String str) {
		String regexURL = "^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$";
		if (CommonUtil.isEmpty(str) == true) return false;
		//UrlValidator urlValidator = new UrlValidator();
		//return urlValidator.isValid(str);
		return Pattern.matches(regexURL, str.trim());
	}

	/**
	 * 날짜 형식을 체크한다
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isValidDate(String date) {
		if (CommonUtil.isEmpty(date) == true) return false;
		return (Pattern.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2,3}Z$", date.trim())) ? true : false;
		// 2020-08-01T23:59:00.000Z
		
		/*try {
			SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			dateFormat.parse(date);
		    return true;
		} catch (ParseException e) {
			return false;
		}*/
	}

	/**
	 * 위도/경도 형식을 체크한다.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidLocation(String str) {
		if (Pattern.matches("^[0-9]{1,3}(\\.[0-9]{1,7}){0,1}$", str)) return true;
		return false;
	}

	public static String dateFormatChange(String date) {
		   if (date == null) return null;
	       SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+09");
	       SimpleDateFormat new_format = new SimpleDateFormat("yyyyMMddHHmmss");
	       
	       String new_date = "";
	       
	       try {
	    	   
	           Date original_date = original_format.parse(date);
	           new_date = new_format.format(original_date);
	           
	       } catch (ParseException e) {
	           e.printStackTrace();
	       }
	       return new_date;
		}
	
	public static String getObjectString(Object obj) {
		if (obj == null) return null;
		return obj.toString();
	}
	
	public static String getObjectStringNull(Object obj) {
		if (obj == null) return "";
		return obj.toString();
	}
	
	public static int getObjectInteger(Object obj) {
		if (obj == null || obj.toString().isEmpty()) return 0;
		return Integer.valueOf(obj.toString());
	}
	
	public static byte[] getObejctByte(Object obj) {
		if (obj == null) return "".getBytes(StandardCharsets.UTF_8);
		
		return obj.toString().getBytes(StandardCharsets.UTF_8);
	}
	
	public static boolean isValidExp(String exp) {
		if (isEmpty(exp) == true) return false;
		String str = "|jpg|jpeg|png|bmp|gif|";
		return (str.indexOf(exp+"|") >= 1) ? true : false;
	}
	
	public static byte[] make4Byte(byte[] szStr) {
		if (szStr.length % 4 == 0) {
			return szStr;
		}
		
		int size = (szStr.length / 4 + 1) * 4;
		byte[] ret = new byte[size];  
		Arrays.fill(ret, (byte) 0x00);
		System.arraycopy(szStr, 0, ret, 0, szStr.length);
		return ret;
	}
	
	public static BufferedReader getReader(String[] cmd) throws Exception {
	    BufferedReader bufferedreader = null;
	    try {
	      Process proc = Runtime.getRuntime().exec(cmd);
	      InputStream inputstream = proc.getInputStream();
	      InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
	      bufferedreader = new BufferedReader(inputstreamreader);
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 
		return bufferedreader;
	  }	
}
