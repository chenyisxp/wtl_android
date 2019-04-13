package com.wtl.bean;

public class WeldInfo {
	private int id;
	private String machinedId;
	private String machinedName;
	private String weldType;
	private String dataType;
	private String setInfo;
	private String noteInfo;
	private String weldBeginTime;
	private String weldEndTime;
	private String weldConnectTime;
	private String memo;
	private String rec_stat;
	private String creator;
	private String modifier;
	private String cre_tm;
	private String up_tm;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMachinedId() {
		return machinedId;
	}
	public void setMachinedId(String machinedId) {
		this.machinedId = machinedId;
	}
	public String getMachinedName() {
		return machinedName;
	}
	public void setMachinedName(String machinedName) {
		this.machinedName = machinedName;
	}
	public String getWeldType() {
		return weldType;
	}
	public void setWeldType(String weldType) {
		this.weldType = weldType;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getSetInfo() {
		return setInfo;
	}
	public void setSetInfo(String setInfo) {
		this.setInfo = setInfo;
	}
	public String getNoteInfo() {
		return noteInfo;
	}
	public void setNoteInfo(String noteInfo) {
		this.noteInfo = noteInfo;
	}
	public String getWeldBeginTime() {
		return weldBeginTime;
	}
	public void setWeldBeginTime(String weldBeginTime) {
		this.weldBeginTime = weldBeginTime;
	}
	public String getWeldEndTime() {
		return weldEndTime;
	}
	public void setWeldEndTime(String weldEndTime) {
		this.weldEndTime = weldEndTime;
	}
	public String getWeldConnectTime() {
		return weldConnectTime;
	}
	public void setWeldConnectTime(String weldConnectTime) {
		this.weldConnectTime = weldConnectTime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getRec_stat() {
		return rec_stat;
	}
	public void setRec_stat(String rec_stat) {
		this.rec_stat = rec_stat;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getCre_tm() {
		return cre_tm;
	}
	public void setCre_tm(String cre_tm) {
		this.cre_tm = cre_tm;
	}
	public String getUp_tm() {
		return up_tm;
	}
	public void setUp_tm(String up_tm) {
		this.up_tm = up_tm;
	}
	@Override
	public String toString() {
		return "{\"id\":\"" + id + "\", \"machinedId\":\"" + machinedId
				+ "\", \"machinedName\":\"" + machinedName + "\", \"weldType\":\"" + weldType
				+ "\", \"dataType\":\"" + dataType + "\", \"setInfo\":" + setInfo
				+ ", \"noteInfo\":\"" + noteInfo + "\", \"weldBeginTime\":\"" + weldBeginTime
				+ "\",\" weldEndTime\":\"" + weldEndTime + "\",\"weldConnectTime\":\""
				+ weldConnectTime + "\", \"memo\":\"" + memo + "\", \"rec_stat\":\"" + rec_stat
				+ "\", \"creator\":\"" + creator + "\",\"modifier\":\"" + modifier
				+ "\", \"cre_tm\":\"" + cre_tm + "\", \"up_tm\":\"" + up_tm + "\"}";
	}
}
