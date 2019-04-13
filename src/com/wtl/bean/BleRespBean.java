package com.wtl.bean;


/**
 * 封装蓝牙 返回值
 * @author chenyi
 *
 */
public class BleRespBean {
	private String dataStatus;//当前的数据状态
	private String preCurrentNum;//预置电流
	private String realCurrentNum;//实际电流
	private String preVoltageNum;//预置电压
	private String realVoltageNum;//预置电压
	
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getPreCurrentNum() {
		return preCurrentNum;
	}
	public void setPreCurrentNum(String preCurrentNum) {
		this.preCurrentNum = preCurrentNum;
	}
	public String getRealCurrentNum() {
		return realCurrentNum;
	}
	public void setRealCurrentNum(String realCurrentNum) {
		this.realCurrentNum = realCurrentNum;
	}
	public String getPreVoltageNum() {
		return preVoltageNum;
	}
	public void setPreVoltageNum(String preVoltageNum) {
		this.preVoltageNum = preVoltageNum;
	}
	public String getRealVoltageNum() {
		return realVoltageNum;
	}
	public void setRealVoltageNum(String realVoltageNum) {
		this.realVoltageNum = realVoltageNum;
	}
	@Override
	public String toString() {
		return "{dataStatus:'" + dataStatus + "', preCurrentNum:'"
				+ preCurrentNum + "', realCurrentNum:'" + realCurrentNum
				+ "', preVoltageNum:'" + preVoltageNum + "', realVoltageNum:'"
				+ realVoltageNum + "'}";
	}
}
