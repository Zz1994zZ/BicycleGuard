package com.zz.fangdao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

class SmsObserver extends ContentObserver {  
	Context context;
	Handler handler;
	double[] m=new double[2];
	LBSgetter lsb=new LBSgetter(){

		@Override
		public void doGetData(double d, double e) {
			// TODO Auto-generated method stub
			m[0]=d;
			m[1]=e;
		        Bundle data=new Bundle();
		        data.putDoubleArray("data",m );
		        Message msg=new Message();
		        msg.setData(data);
		        handler.sendMessage(msg);
		}
		
	};
    public SmsObserver(Context context, Handler handler) {  	        
        super(handler);
        this.context=context;
        this.handler=handler;
    }  

    @Override  
    public void onChange(boolean selfChange) {  
        //每当有新短信到来时，使用我们获取短消息的方法  
        getSmsFromPhone(); 
        //smsHandler.obtainMessage(MSG_OUTBOXCONTENT, sb.toString()).sendToTarget();          
    }  
    private Uri SMS_INBOX = Uri.parse("content://sms/");  
    public void getSmsFromPhone() {  
        ContentResolver cr = context.getContentResolver();  
        String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type  
       // String where = //" address = '1066321332' AND date >  "  +
        String where=     "date >  "+  (System.currentTimeMillis() - 10 * 60 * 1000);  
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");  //10分钟以内
        if (null == cur)  
            return;  
        String number;
        String name;
        String body="a a a ";
        if (cur.moveToNext()) {  
//             number = cur.getString(cur.getColumnIndex("address"));//手机号  
//             name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表  
             body = cur.getString(cur.getColumnIndex("body"));    
        }  
        System.out.println(body);
    
        if(getJWD(body)){
            Bundle data=new Bundle();
            data.putDoubleArray("data",m );
            Message msg=new Message();
            msg.setData(data);
            handler.sendMessage(msg);
        }
        cur.close();
    }
	public boolean   getJWD(String data){//返回是否立即得到坐标 错误坐标也是false
		data="加个前缀放置意外"+data;
		String []a2=data.split("Lac:");
		if(a2.length==2)
		{
			String[] b2=a2[1].split("\nT:");
			String[] b3=b2[0].split(" ");
			lsb.setData(b3[0],b3[1]);
			lsb.get();
		    return false;
		}
		String []a=data.split("lat:");
		if(a.length<2){
			m[0]=m[1]=0; return false;
		}
		String []b=a[1].split("long:");
		if(b.length<2)
		{
			m[0]=m[1]=0; return false;
		}	
		String []c=b[1].split("speed:");
		if(c.length<2)
		{
			m[0]=m[1]=0; return false;
		}	
		double lat=Double.valueOf(b[0]);
		double lon=Double.valueOf(c[0]);
		m[0]=lat;
		m[1]=lon;
		return true;
	}
}