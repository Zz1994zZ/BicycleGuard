package com.zz.fangdao;

public class ZuoBiao {
double longitude;
private double latitude;
ZuoBiao(double data,double data2){
	longitude=data2;
	latitude=data;
}
public double getLatitude() {
	return latitude;
}

public void setLatitude(double latitude) {
	this.latitude = latitude;
}

public double getLongitude() {
	return longitude;
}

public void setLongitude(double longitude) {
	this.longitude = longitude;
}
}
