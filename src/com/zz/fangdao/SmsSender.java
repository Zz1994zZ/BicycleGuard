package com.zz.fangdao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.telephony.SmsManager;

public class SmsSender {
	//fix030s005n 限时限次
	//fix030s***n 限时不限次
	static String a[]={"fix030s005n123456","fix030s***n123456","move123456 0200","nomove123456","arm123456","disarm123456"};
	// String sms_content="";
	//String phone_number="";
public static void send(String phone_number,int type){
	String sms_content=a[type];
	SmsManager smsManager = SmsManager.getDefault();
    if(sms_content.length() > 70) {
        List<String> contents = smsManager.divideMessage(sms_content);
        for(String sms : contents) {
            smsManager.sendTextMessage(phone_number, null, sms, null, null);
        }
    } else {
     smsManager.sendTextMessage(phone_number, null, sms_content, null, null);
    }
	
}
public static void send(String phone_number,String sms_content){
	SmsManager smsManager = SmsManager.getDefault();
    if(sms_content.length() > 70) {
        List<String> contents = smsManager.divideMessage(sms_content);
        for(String sms : contents) {
            smsManager.sendTextMessage(phone_number, null, sms, null, null);
        }
    } else {
     smsManager.sendTextMessage(phone_number, null, sms_content, null, null);
    }
	
}
public static boolean isPhoneNumberValid(String phoneNumber)

{

  boolean isValid = false;

  /* 可接受的电话格式有:

   * ^//(? : 可以使用 "(" 作为开头

   * (//d{3}): 紧接着三个数字

   * //)? : 可以使用")"接续

   * [- ]? : 在上述格式后可以使用具选择性的 "-".

   * (//d{4}) : 再紧接着三个数字

   * [- ]? : 可以使用具选择性的 "-" 接续.

   * (//d{4})$: 以四个数字结束.

   * 可以比较下列数字格式:

   * (123)456-78900, 123-4560-7890, 12345678900, (123)-4560-7890  

  */

  String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
  String expression2 = "^\\(?(\\d{2})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
 

  CharSequence inputStr = phoneNumber;

  /*创建Pattern*/

  Pattern pattern = Pattern.compile(expression);

  /*将Pattern 以参数传入Matcher作Regular expression*/

  Matcher matcher = pattern.matcher(inputStr);

  /*创建Pattern2*/

  Pattern pattern2 =Pattern.compile(expression2);

  /*将Pattern2 以参数传入Matcher2作Regular expression*/

  Matcher matcher2= pattern2.matcher(inputStr);

  if(matcher.matches()||matcher2.matches())

  {

    isValid = true;

  }

  return isValid; 

}
}
