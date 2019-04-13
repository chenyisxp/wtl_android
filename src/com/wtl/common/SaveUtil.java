package com.wtl.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveUtil {
    /**
     * 保存数据SharedPreferences文件的名字
     */
    private final static String PREFS_NAME = "MyPrefsFile";
	/**
	 * 获取所有设置值
	 * @return
	 */
	public static Map<String, String> getSettingData(){
		Map<String, String> rstmap=new HashMap<String, String>();
		
		return rstmap;
	}
	/***
	 * 根据主键 获取存储值
	 * @return
	 */
	public static String getDataBykey(Context context,String key){
		SharedPreferences setInfo = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        String info = setInfo.getString(key, null);
		return info;
	}
	/***
	 * 根据主键 更新
	 * @return
	 */
	public static String updateDataBykey(Context context,String key,String value){
		SharedPreferences setInfo = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE );
        SharedPreferences.Editor editor = setInfo.edit();//获取Editor
        //得到Editor后，写入需要保存的数据
        editor.putString(key, value);
        editor.commit();//提交修改
        //取值验证是否更新成功
        String info = setInfo.getString(key, null);
        return info;
	}
}
