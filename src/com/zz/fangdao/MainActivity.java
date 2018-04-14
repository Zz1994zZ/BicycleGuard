package com.zz.fangdao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//�����õ�λ�����ѹ��ܣ���Ҫimport����
import com.baidu.location.Poi;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;	
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	 MapView mMapView = null;  
	 BaiduMap  mBaiduMap=null;
	 public LocationClient mLocationClient = null;
	 public BDLocationListener myListener = new MyLocationListener();
	 ZuoBiao zxcZB;
	 private SmsObserver smsObserver;  
	 private Uri SMS_INBOX = Uri.parse("content://sms/");  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	      //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.activity_main);
		
		 mMapView = (MapView) findViewById(R.id.bmapView);  
		 mBaiduMap=mMapView.getMap();
		 refresh();
		 mBaiduMap.setMyLocationEnabled(true);  
	 mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
	 mLocationClient.registerLocationListener( myListener );    //ע���������
	 initLocation();
	 mLocationClient.start();//��ʼ��λ
	   smsObserver = new SmsObserver(this, smsHandler);  
       getContentResolver().registerContentObserver(SMS_INBOX, true,  
               smsObserver);  
	}
	private String getNumber(){
		String a=PreferenceManager.getDefaultSharedPreferences(this).getString("number",null);
		if(SmsSender.isPhoneNumberValid(a))
			return a;
		return null;
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        ///option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
option.setIgnoreKillProcess(false);//��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
        option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
        mLocationClient.setLocOption(option);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		           case R.id.action_ydjb:
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "�ѷ�����λ��������ָ��",2000).show();
        			 SmsSender.send(this.getNumber(), 2);
		        	   break;
		           case R.id.action_noydjb:
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "�ѷ�����λ�����ر�ָ��",2000).show();
        			 SmsSender.send(this.getNumber(), 3);
		        	   break;
		           case R.id.action_bf://����
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "�ѷ��Ͳ���ָ��",2000).show();
       			 SmsSender.send(this.getNumber(), 4);
		        	   break;
		           case R.id.action_cf://����
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "�ѷ��ͳ���ָ��",2000).show();
       			 SmsSender.send(this.getNumber(), 5);
		        	   break;
//		           case R.id.action_bj://����
//			       		if(null==this.getNumber())
//			    		{
//			    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
//			    		    return super.onMenuItemSelected(featureId, item);
//			    		}
//			       	 Toast.makeText(getApplicationContext(), "�ѷ��ͳ���ָ��",2000).show();
//      			 SmsSender.send(this.getNumber(), 5);
//		        	   break;
		           case R.id.function:
		       		if(null==this.getNumber())
		    		{
		    			 Toast.makeText(getApplicationContext(), "�豸���벻��ȷ�����������ã�",2000).show();
		    		    return super.onMenuItemSelected(featureId, item);
		    		}
		        		switch(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("mode", "-1"))){
		        		case 0://��ʱ�޴�
		        			 Toast.makeText(getApplicationContext(), "�ѷ�����ʱ�޴ζ�λָ��",2000).show();
		        			 SmsSender.send(this.getNumber(), 0);
		        			break;
		        		case 1://��ʱ���޴�
		        			 Toast.makeText(getApplicationContext(), "�ѷ�����ʱ���޴ζ�λָ��",
		    	    			     2000).show();
		        			 SmsSender.send(this.getNumber(), 1);
		        			break;
		        		}
		        	   break;
		           case R.id.action_settings:
		        		Intent b = new Intent(MainActivity.this,
		    					SettingsActivity.class);
		    			startActivity(b);
		        	   break;
		        	  
		           default:
		break;
		           }   
		           return super.onMenuItemSelected(featureId, item);
		      }

@Override
public void onBackPressed() {
    //ʵ��Home��Ч��
    //super.onBackPressed();��仰һ��Ҫע��,��Ȼ��ȥ����Ĭ�ϵ�back����ʽ��
    Intent i= new Intent(Intent.ACTION_MAIN);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.addCategory(Intent.CATEGORY_HOME);
    startActivity(i); 
}
	  @Override  
	    protected void onDestroy() {  
	        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
	    	mLocationClient.stop();
			// �رն�λͼ��
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		    getContentResolver().unregisterContentObserver(smsObserver);
			super.onDestroy();
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
	        mMapView.onResume(); 
	        refresh();
	        }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
	        mMapView.onPause();  
	        }  
	    private void refresh(){
	    	if(zxcZB==null)
	    	{
	    		 Toast.makeText(getApplicationContext(), "���ڶ�λ���г�...",
	    			     Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    		
	    	//����Maker�����  
	    	mBaiduMap.clear();
	    	LatLng point = new LatLng(zxcZB.getLatitude(), zxcZB.getLongitude());  
	    	//����Markerͼ��  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(R.drawable.zxc);  
	    	//����MarkerOption�������ڵ�ͼ�����Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point) 
	    	    .title("���г�λ��")
	    	    .icon(bitmap);  
	    	//�ڵ�ͼ�����Marker������ʾ  
	    	Marker marker = (Marker) (mBaiduMap.addOverlay(option));
	    	    }
	    private  class MyLocationListener implements BDLocationListener {
	    	 
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	        	// map view ���ٺ��ڴ����½��յ�λ��
				if (location == null || mMapView == null)
					return;
	        	
	        	// ������λͼ��  
	        	
	        	// ���춨λ����  
	        	MyLocationData locData = new MyLocationData.Builder()  
	        	    .accuracy(location.getRadius())  
	        	    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
	        	    .direction(100).latitude(location.getLatitude())  
	        	    .longitude(location.getLongitude()).build();  
	        	// ���ö�λ����  
	        	mBaiduMap.setMyLocationData(locData);  
	        	// ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩  
	        	BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
	        	    .fromResource(R.drawable.icon_geo);  
	        	MyLocationConfiguration config = new MyLocationConfiguration(LocationMode.FOLLOWING, true, mCurrentMarker);  
	        	mBaiduMap.setMyLocationConfigeration(config);  
	        	// ������Ҫ��λͼ��ʱ�رն�λͼ��  
	        	//mBaiduMap.setMyLocationEnabled(false);
	        	refresh();
	            //Receive Location
	            StringBuffer sb = new StringBuffer(256);
	            sb.append("time : ");
	            sb.append(location.getTime());
	            sb.append("\nerror code : ");
	            sb.append(location.getLocType());
	            sb.append("\nlatitude : ");
	            sb.append(location.getLatitude());
	            sb.append("\nlontitude : ");
	            sb.append(location.getLongitude());
	            sb.append("\nradius : ");
	            sb.append(location.getRadius());
	            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
	                sb.append("\nspeed : ");
	                sb.append(location.getSpeed());// ��λ������ÿСʱ
	                sb.append("\nsatellite : ");
	                sb.append(location.getSatelliteNumber());
	                sb.append("\nheight : ");
	                sb.append(location.getAltitude());// ��λ����
	                sb.append("\ndirection : ");
	                sb.append(location.getDirection());// ��λ��
	                sb.append("\naddr : ");
	                sb.append(location.getAddrStr());
	                sb.append("\ndescribe : ");
	                sb.append("gps��λ�ɹ�");
	 
	            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
	                sb.append("\naddr : ");
	                sb.append(location.getAddrStr());
	                //��Ӫ����Ϣ
	                
	                sb.append("\noperationers : ");
	                sb.append(location.getOperators());
	                sb.append("\ndescribe : ");
	                sb.append("���綨λ�ɹ�");
	            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
	                sb.append("\ndescribe : ");
	                sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
	            } else if (location.getLocType() == BDLocation.TypeServerError) {
	                sb.append("\ndescribe : ");
	                sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
	            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
	                sb.append("\ndescribe : ");
	                sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
	            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
	                sb.append("\ndescribe : ");
	                sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
	            }
	sb.append("\nlocationdescribe : ");
	                sb.append(location.getLocationDescribe());// λ�����廯��Ϣ
	                List<Poi> list = location.getPoiList();// POI����
	                if (list != null) {
	                    sb.append("\npoilist size = : ");
	                    sb.append(list.size());
	                    for (Poi p : list) {
	                        sb.append("\npoi= : ");
	                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
	                    }
	                }
	            Log.i("BaiduLocationApiDem", sb.toString());
	        }
	    }
	    public Handler smsHandler = new Handler() {  
	        //������Խ��лص��Ĳ���  
	        //TODO  
	 @Override
	 public void handleMessage(Message msg) {
		double []data= msg.getData().getDoubleArray("data");
		if(data[0]==0&&data[1]==0)
			return;
		 if(zxcZB==null)
			 zxcZB=new ZuoBiao(data[0],data[1]);
		 else 
		 {
			 zxcZB.setLatitude(data[0]);
			 zxcZB.setLongitude(data[1]); 
		 }
		 refresh();
		 System.out.println("��������ɹ�:"+zxcZB.getLatitude()+":"+zxcZB.getLongitude());
	 }
	    };  
	    
	
 
}
