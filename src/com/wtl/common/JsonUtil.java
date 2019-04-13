package com.wtl.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	//JSON转换工具类
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	static {
		//JSON转换Bean初始化
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * 获取Json字符串
	 * @param map
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String getJsonStr(Map<String,Object> map) throws JsonProcessingException{
		final String jsonMessage = OBJECT_MAPPER.writeValueAsString(map);
		return jsonMessage;
	}
	
	/**
	 * 将json转换成HashMap
	 * @param jsonMessage
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static Map<String,Object> convertJsonToHashMap(String jsonMessage) throws JsonParseException, JsonMappingException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> map = OBJECT_MAPPER.readValue(jsonMessage, HashMap.class);
		return map;
	}
}
