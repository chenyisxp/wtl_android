package com.wtl.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBUtil extends SQLiteOpenHelper {

	public static final String NAME = "practiceDataBases";// 数据库名
	public static final int VERSION = 1;// 版本号

	/**
	 * content上下文对象 name数据库名称 factory数据库工厂 version版本
	 *
	 * @param context
	 */
	public SQLiteDBUtil(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	//
	@Override
	public void onCreate(SQLiteDatabase db) {// 执行创建数据表的sql语句
//		String userSQL = "create table practice"
//				+ "(id integer primary key autoincrement," + "name varchar(5),"
//				+ "age integer)";
//		String wtlSQL = "create table weldInfo"
//				+ "(id integer primary key autoincrement," 
//				+ "machinedId varchar(20),"
//				+ "machinedName varchar(20),"
//				+ "weldType varchar(10),"
//				+ "dataType char(1),"
//				+ "setInfo varchar(200),"
//				+ "noteInfo varchar(100),"
//				+ "weldBeginTime  DATA  DEFAULT CURRENT_TIMESTAMP,"
//				+ "weldEndTime DATA  DEFAULT CURRENT_TIMESTAMP,"
//				+ "weldConnectTime varchar(20),"
//				+ "memo varchar(100),"
//				+ "rec_stat varchar(1),"
//				+ "creator varchar(20),"
//				+ "modifier varchar(20),"
//				+ "cre_tm DATA  DEFAULT CURRENT_TIMESTAMP,"
//				+ "up_tm  DATA  DEFAULT CURRENT_TIMESTAMP)";
//		db.execSQL(userSQL);
		//1、焊机存储模块值得记录
		String wtlSQL = "create table memoryList"
						+ "(id integer primary key autoincrement," 
						+ "pupNum varchar(2),"
						+ "remarkInfo varchar(500))";
		db.execSQL(wtlSQL);
		 ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
		for(int i=1;i<10;i++){
			 cv.put("pupNum",i);//添加title
			 cv.put("remarkInfo","NONE"); //添加weather
			 db.insert("memoryList",null,cv);//执行插入操作
		}
		//2、焊接蓝牙名称数据库 type 类型0默认 1、最后一次连接的蓝牙信息 2、记录原始名称用于重置
		String bleSQL = "create table bleList"
				+ "(id integer primary key autoincrement," 
				+ "address varchar(30),"
				+ "remarkInfo varchar(50),"
				+ "realBleName varchar(50),"
				+ "realAddress varchar(30),"
				+ "type varchar(2))";
		db.execSQL(bleSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String wtlSQL = "create table weldInfo"
//				+ "(id integer primary key autoincrement," 
//				+ "machinedId varchar(20),"
//				+ "machinedName varchar(20),"
//				+ "weldType varchar(10),"
//				+ "dataType char(1),"
//				+ "setInfo varchar(200),"
//				+ "noteInfo varchar(100),"
//				+ "weldBeginTime  DATETIME DEFAULT CURRENT_TIMESTAMP,"
//				+ "weldEndTime DATETIME DEFAULT CURRENT_TIMESTAMP,"
//				+ "weldConnectTime varchar(20),"
//				+ "memo varchar(100),"
//				+ "rec_stat varchar(1),"
//				+ "creator varchar(20),"
//				+ "modifier varchar(20),"
//				+ "cre_tm DATETIME DEFAULT CURRENT_TIMESTAMP,"
//				+ "up_tm  DATETIME DEFAULT CURRENT_TIMESTAMP)";
		String wtlSQL = "create table memoryList"
				+ "(id integer primary key autoincrement," 
				+ "pupNum varchar(5),"
				+ "remarkInfo varchar(500))";
			db.execSQL(wtlSQL);
		    ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
			for(int i=1;i<10;i++){
				 cv.put("pupNum",i);//添加title
				 cv.put("remarkInfo","NONE"); //添加weather
				 db.insert("memoryList",null,cv);//执行插入操作
			}
	}

}
