package com.zz.fangdao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.telephony.SmsManager;

public class SmsSender {
	//fix030s005n ��ʱ�޴�
	//fix030s***n ��ʱ���޴�
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

  /* �ɽ��ܵĵ绰��ʽ��:

   * ^//(? : ����ʹ�� "(" ��Ϊ��ͷ

   * (//d{3}): ��������������

   * //)? : ����ʹ��")"����

   * [- ]? : ��������ʽ�����ʹ�þ�ѡ���Ե� "-".

   * (//d{4}) : �ٽ�������������

   * [- ]? : ����ʹ�þ�ѡ���Ե� "-" ����.

   * (//d{4})$: ���ĸ����ֽ���.

   * ���ԱȽ��������ָ�ʽ:

   * (123)456-78900, 123-4560-7890, 12345678900, (123)-4560-7890  

  */

  String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
  String expression2 = "^\\(?(\\d{2})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
 

  CharSequence inputStr = phoneNumber;

  /*����Pattern*/

  Pattern pattern = Pattern.compile(expression);

  /*��Pattern �Բ�������Matcher��Regular expression*/

  Matcher matcher = pattern.matcher(inputStr);

  /*����Pattern2*/

  Pattern pattern2 =Pattern.compile(expression2);

  /*��Pattern2 �Բ�������Matcher2��Regular expression*/

  Matcher matcher2= pattern2.matcher(inputStr);

  if(matcher.matches()||matcher2.matches())

  {

    isValid = true;

  }

  return isValid; 

}
}
