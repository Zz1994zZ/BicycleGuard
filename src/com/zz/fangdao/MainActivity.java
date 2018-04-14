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
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
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
	      //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.activity_main);
		
		 mMapView = (MapView) findViewById(R.id.bmapView);  
		 mBaiduMap=mMapView.getMap();
		 refresh();
		 mBaiduMap.setMyLocationEnabled(true);  
	 mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	 mLocationClient.registerLocationListener( myListener );    //注册监听函数
	 initLocation();
	 mLocationClient.start();//开始定位
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
        ///option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
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
			    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "已发送移位警报启动指令",2000).show();
        			 SmsSender.send(this.getNumber(), 2);
		        	   break;
		           case R.id.action_noydjb:
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "已发送移位警报关闭指令",2000).show();
        			 SmsSender.send(this.getNumber(), 3);
		        	   break;
		           case R.id.action_bf://布防
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "已发送布防指令",2000).show();
       			 SmsSender.send(this.getNumber(), 4);
		        	   break;
		           case R.id.action_cf://撤防
			       		if(null==this.getNumber())
			    		{
			    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
			    		    return super.onMenuItemSelected(featureId, item);
			    		}
			       	 Toast.makeText(getApplicationContext(), "已发送撤防指令",2000).show();
       			 SmsSender.send(this.getNumber(), 5);
		        	   break;
//		           case R.id.action_bj://报警
//			       		if(null==this.getNumber())
//			    		{
//			    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
//			    		    return super.onMenuItemSelected(featureId, item);
//			    		}
//			       	 Toast.makeText(getApplicationContext(), "已发送撤防指令",2000).show();
//      			 SmsSender.send(this.getNumber(), 5);
//		        	   break;
		           case R.id.function:
		       		if(null==this.getNumber())
		    		{
		    			 Toast.makeText(getApplicationContext(), "设备号码不正确，请重新设置！",2000).show();
		    		    return super.onMenuItemSelected(featureId, item);
		    		}
		        		switch(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("mode", "-1"))){
		        		case 0://限时限次
		        			 Toast.makeText(getApplicationContext(), "已发送限时限次定位指令",2000).show();
		        			 SmsSender.send(this.getNumber(), 0);
		        			break;
		        		case 1://限时不限次
		        			 Toast.makeText(getApplicationContext(), "已发送限时不限次定位指令",
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
    //实现Home键效果
    //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
    Intent i= new Intent(Intent.ACTION_MAIN);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.addCategory(Intent.CATEGORY_HOME);
    startActivity(i); 
}
	  @Override  
	    protected void onDestroy() {  
	        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
	    	mLocationClient.stop();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		    getContentResolver().unregisterContentObserver(smsObserver);
			super.onDestroy();
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
	        mMapView.onResume(); 
	        refresh();
	        }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
	        mMapView.onPause();  
	        }  
	    private void refresh(){
	    	if(zxcZB==null)
	    	{
	    		 Toast.makeText(getApplicationContext(), "正在定位自行车...",
	    			     Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    		
	    	//定义Maker坐标点  
	    	mBaiduMap.clear();
	    	LatLng point = new LatLng(zxcZB.getLatitude(), zxcZB.getLongitude());  
	    	//构建Marker图标  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(R.drawable.zxc);  
	    	//构建MarkerOption，用于在地图上添加Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point) 
	    	    .title("自行车位置")
	    	    .icon(bitmap);  
	    	//在地图上添加Marker，并显示  
	    	Marker marker = (Marker) (mBaiduMap.addOverlay(option));
	    	    }
	    private  class MyLocationListener implements BDLocationListener {
	    	 
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	        	// map view 销毁后不在处理新接收的位置
				if (location == null || mMapView == null)
					return;
	        	
	        	// 开启定位图层  
	        	
	        	// 构造定位数据  
	        	MyLocationData locData = new MyLocationData.Builder()  
	        	    .accuracy(location.getRadius())  
	        	    // 此处设置开发者获取到的方向信息，顺时针0-360  
	        	    .direction(100).latitude(location.getLatitude())  
	        	    .longitude(location.getLongitude()).build();  
	        	// 设置定位数据  
	        	mBaiduMap.setMyLocationData(locData);  
	        	// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）  
	        	BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
	        	    .fromResource(R.drawable.icon_geo);  
	        	MyLocationConfiguration config = new MyLocationConfiguration(LocationMode.FOLLOWING, true, mCurrentMarker);  
	        	mBaiduMap.setMyLocationConfigeration(config);  
	        	// 当不需要定位图层时关闭定位图层  
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
	            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
	                sb.append("\nspeed : ");
	                sb.append(location.getSpeed());// 单位：公里每小时
	                sb.append("\nsatellite : ");
	                sb.append(location.getSatelliteNumber());
	                sb.append("\nheight : ");
	                sb.append(location.getAltitude());// 单位：米
	                sb.append("\ndirection : ");
	                sb.append(location.getDirection());// 单位度
	                sb.append("\naddr : ");
	                sb.append(location.getAddrStr());
	                sb.append("\ndescribe : ");
	                sb.append("gps定位成功");
	 
	            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
	                sb.append("\naddr : ");
	                sb.append(location.getAddrStr());
	                //运营商信息
	                
	                sb.append("\noperationers : ");
	                sb.append(location.getOperators());
	                sb.append("\ndescribe : ");
	                sb.append("网络定位成功");
	            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
	                sb.append("\ndescribe : ");
	                sb.append("离线定位成功，离线定位结果也是有效的");
	            } else if (location.getLocType() == BDLocation.TypeServerError) {
	                sb.append("\ndescribe : ");
	                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
	            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
	                sb.append("\ndescribe : ");
	                sb.append("网络不同导致定位失败，请检查网络是否通畅");
	            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
	                sb.append("\ndescribe : ");
	                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
	            }
	sb.append("\nlocationdescribe : ");
	                sb.append(location.getLocationDescribe());// 位置语义化信息
	                List<Poi> list = location.getPoiList();// POI数据
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
	        //这里可以进行回调的操作  
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
		 System.out.println("更新坐标成功:"+zxcZB.getLatitude()+":"+zxcZB.getLongitude());
	 }
	    };  
	    
	
 
}
