package com.zz.fangdao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class LBSgetter {
	 String LAC=null;
	 String CELLID=null;
	 String TOKEN="4d6a4833b620b06f92fab3b8e91af98f";
	 String url="http://api.cellid.cn/cellid.php";
	 String result;
	 public void setData(String lac,String cellid){		 
		 this.LAC=lac;
		 this.CELLID=cellid;
	 }
	 public abstract void doGetData(double d,double e); 
	public  void get(){
		if(LAC==null||CELLID==null)return;
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				result=sendGet(url,"lac="+Integer.parseInt(LAC,16)+"&cell_id="+Integer.parseInt(CELLID,16)+"&token="+TOKEN);
				System.out.println(result);
				
				try {
					JSONObject j=new JSONObject(result);
					doGetData(j.getDouble("lat"),j.getDouble("lon")); 
					//doGetData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}).start();
	}
	public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // �򿪺�URL֮�������
            URLConnection connection = realUrl.openConnection();
            // ����ͨ�õ���������
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����ʵ�ʵ�����
            connection.connect();
            // ��ȡ������Ӧͷ�ֶ�
            Map<String, List<String>> map = connection.getHeaderFields();
            // �������е���Ӧͷ�ֶ�
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
