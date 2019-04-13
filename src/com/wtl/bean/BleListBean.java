package com.wtl.bean;

import java.io.Serializable;
/**
 * 扫描出来的蓝牙 数组bean
 * @author chenyi
 *
 */
public class BleListBean implements Serializable{
	private String address;
	private String bleName;
	private String rssi;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBleName() {
		return bleName;
	}
	public void setBleName(String bleName) {
		this.bleName = bleName;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	@Override
	public String toString() {
		return "{\"address\":\"" + address + "\", \"bleName\":\"" + bleName
				+ "\", \"rssi\":\"" + rssi + "\"}";
	}
	
}
