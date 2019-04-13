package com.wtl.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
	// 日期格式yyyyMMdd
	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
	// 日期格式yyyy/MM/dd
	public static final String FORMAT_YYYYMMDDWITHBACKSLASH= "yyyy/MM/dd";
	// 日期格式yyyy-MM-dd
	public static final String FORMAT_YYYYMMDDSUB = "yyyy-MM-dd";
	// 日期格式yyyy-MM-dd HH:mm:ss
	public static final String FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	// 日期格式yyyy-MM-dd HH:mm:ss:SS
	public static final String FORMAT_YYYYMMDDHHMMSSss = "yyyy-MM-dd HH:mm:ss:SS";
	 
	
	/**
	 * 获取当前系统时间 精确到毫秒
	 * @return String
	 */
	public static String getCurrentDateyyyyMMddHHmmssSS() {
		SimpleDateFormat sf = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSSss);
		return sf.format(new Date());
	}
	
	public static String getCurrentDateyyyyMMddHHmmss() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(new Date());
	}

	public static String getCurrentDateYYYYMMDD() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		return sf.format(new Date());
	}
	
	public static String getDateYYYYMMDD() {
		SimpleDateFormat sf = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
		return sf.format(new Date());
	}

	public static Date paseDateyyyyMMddHHmmss(String strDate)
			throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sf.parse(strDate);
		return date;
	}

	public static Date paseDateyyyyMMddHHmmss(String strDate, String format) 
			throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		//精确匹配
		sf.setLenient(false);
		Date date = sf.parse(strDate);
		return date;
	}

	/**
	 * 转换日期格式
	 * 
	 * @param strDate
	 * @return
	 */
	public static String formatDate(String strDate, String formatBefore, String formatAfter) 
			throws ParseException {
		if (strDate == null) {
			return "";
		}
		SimpleDateFormat sfBefore = new SimpleDateFormat(formatBefore);
		SimpleDateFormat sfAfter = new SimpleDateFormat(formatAfter);
		Date date = sfBefore.parse(strDate);
		return sfAfter.format(date);
	}
	/**
	 * 把月份控件获取出来的值转换成YYYYMMDD格式
	 * 
	 * @param time
	 * @return
	 */
	public static String paseDateYYYYMMToDateYYYYMMDD(String time) {
		return time.substring(0, 10).replaceAll("-", "");
	}

	/**
	 * 把YYYYMMDD转换成YYYY-MM-DD HH:mm:ss
	 * @param time
	 * @return 字符串
	 */
	public static String paseDateYYYYMMToDateYYYYMMDDHHMMSS(String time) throws ParseException{
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	    SimpleDateFormat sf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     
	    return sf2.format(sf1.parse(time+"000000"));
	}
	
	/**
	 * 把YYYY-MM-DD HH:mm:ss转换成YYYYMMDDHHmmss
	 * @param time
	 * @return 字符串
	 */
	public static String paseDateYYYYMMDDHHMMSS(String time) throws ParseException{
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat sf2 =new SimpleDateFormat("yyyyMMddHHmmss");
	     
	    return sf2.format(sf1.parse(time));
	}

	/**
	 * 把YYYY-MM-DD hh:mm:ss转换成YYYYMMDD
	 * 
	 * @param time
	 * @return
	 */
	public static String paseDateYYYYMMDDHHmmssToDateYYYYMMDD(String time) {
		return time.substring(0, 10).replaceAll("-", "");
	}

	/***
	 * 判断和当前日期是否相差60秒
	 * 
	 * @throws ParseException
	 */
	public static boolean compareDate(String strDate) throws ParseException {
		if (StringUtil.isNotEmpty(strDate)) {
			Date date = paseDateyyyyMMddHHmmss(strDate);
			Date currDate = new Date();
			if (((currDate.getTime() / 1000) - (date.getTime() / 1000)) < 60) {
				// 相差值在60秒之内
				return false;
			}
		}
		return true;
	}

	/***
	 * 判断和当前日期是否相差seconds秒
	 * 
	 */
	public static boolean compareDate(String strDate, int seconds) throws ParseException {
		if (StringUtil.isNotEmpty(strDate)) {
			Date date = paseDateyyyyMMddHHmmss(strDate);
			Date currDate = new Date();
			if (((currDate.getTime() / 1000) - (date.getTime() / 1000)) < seconds) {
				// 相差值在seconds秒之内
				return false;
			}
		}
		return true;
	}

	/****
	 * 验证日期是否大于当前日期
	 * 
	 * @throws ParseException
	 */
	public static boolean compareInpAndCurr(String strDate)
			throws ParseException {
		Date date = paseDateyyyyMMddHHmmss(strDate);
		Date currDate = new Date();
		if (date.getTime() > currDate.getTime()) {
			return true;
		}
		return false;
	}
	
	/****
	 * 验证日期是否小于当前日期
	 * 
	 * @throws ParseException
	 */
	public static boolean checkInputTimeThanCurrDate(String strDate)
			throws ParseException {
		Date date = paseDateyyyyMMddHHmmss(strDate);
		Date currDate = new Date();
		if (date.getTime() < currDate.getTime()) {
			return true;
		}
		return false;
	}

	/****
	 * 验证日期1是否大于日期2
	 * 
	 * @throws ParseException
	 */
	public static boolean compareBiggerInpDates(String strDate, String strDateCompared, String format)
			throws ParseException {
		if (strDate == null || strDateCompared == null) {
			return false;
		}
		Date date = paseDateyyyyMMddHHmmss(strDate, format);
		Date dateCompared;
		if (strDateCompared.length() == 19) {
			dateCompared = paseDateyyyyMMddHHmmss(strDateCompared);
		} else {
			dateCompared = paseDateyyyyMMddHHmmss(strDateCompared, format);
		}
		if (date.getTime() > dateCompared.getTime()) {
			return true;
		}
		return false;
	}

	/****
	 * 验证日期1是否小于日期2
	 * 
	 * @throws ParseException
	 */
	public static boolean compareLesserInpDates(String strDate, String strDateCompared, String format)
			throws ParseException {
		if (strDate == null || strDateCompared == null) {
			return false;
		}
		Date date = paseDateyyyyMMddHHmmss(strDate, format);
		Date dateCompared = paseDateyyyyMMddHHmmss(strDateCompared, format);
		if (date.getTime() < dateCompared.getTime()) {
			return true;
		}
		return false;
	}
	
	/****
	 * 根据指定format，将Date型日期转换成字符型日期
	 * 
	 */
	public static String getDateByFormat(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/****
	 * 根据指定format，将字符型日期转换成字符型日期
	 * @param format1 传入的格式
	 * @param format2 转换后的格式
	 * @param strDate 日期
	 * 
	 */
	public static String getDateByFormat(String format1, String format2,
			String strDate) {
		try {
			if (StringUtil.isNotEmpty(format1)
					&& StringUtil.isNotEmpty(format2)
					&& StringUtil.isNotEmpty(strDate)) {

				SimpleDateFormat sdf = new SimpleDateFormat(format1);
				Date date = sdf.parse(strDate);
				return getDateByFormat(format2, date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取当前时分秒
	 */
	public static String getCurrentHHmmss() {
		return getDateByFormat("HHmmss", new Date());
	}

	/******
	 * 系统日期比对象日期晚几日
	 */
	public static long getQuot(String objectDt) {
		if (null == objectDt) {
			return 0;
		}
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date today = new Date();
			today= ft.parse(ft.format(today));
			Date date2 = ft.parse(objectDt);
			quot = (today.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}
	
	public static String getYearMonthDay(String str){
		    
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			 try {
				Date date = sf.parse(str);
				if(null!=date){
					return sf.format(date);
					}
			} catch (ParseException e) {
				e.printStackTrace();
			}  
			
		return "";
	}
	
	
	/**
	 * 对时间进行按数据库格式转换:
	 * 把YYYY-MM-DD HH:mm:ss转换成YYYYMMDDHHmmss
	 */
	public static String converDate1(String dateStr) {
		String string = "";
		// 对时间进行按数据库格式转换:
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtil.isNotBlank(dateStr)) {
				string = format1.format(format2.parse(dateStr));
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		
		return string;
	}
	
	/**
	 * 转换时间为页面显示的格式
	 * 把YYYYMMDDHHmmss转换成YYYY-MM-DD HH:mm:ss
	 */
	public static String converDate2(String dateStr) {
		String string = "";
		// 对时间进行按数据库格式转换:
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (StringUtil.isNotBlank(dateStr)){
				string = format2.format(format1.parse(dateStr));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return string;
	}

	/**
	 * 支付时间 : "yyyy-MM-dd HH:mm:ss" 转换成Date
	 */
	public static Date converDate3(String dateStr){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		 return date;
		
	}
	
	/**
	 * 获得当前日期的yyyy-MM-dd 00:00:00格式
	 * 
	 * @return currentDate	格式化后的日期
	 */
	public static String getCurrentDateWeeHours(){
		String currentDate="";
		try {
			String startDateTmp=DateUtil.getCurrentDateYYYYMMDD();
			currentDate=DateUtil.paseDateYYYYMMToDateYYYYMMDDHHMMSS(startDateTmp);
		} catch (Exception e) {
			
		} 
		return currentDate;
		
	}
	
	/**
	 * 根据原始日期，加上一定天数和秒数,计算出新的目标日期
	 * 
	 * @param originalDate	原始日期，格式yyyy-MM-dd HH:mm:ss
	 * @param days			天数
	 * @param seconds		秒数
	 * @return	targetDate	加上一定天数的新日期
	 */
	public static String getNewDateByAddDays(String originalDate,int days,int seconds){
		String targetDate="";
		try {
			//原始日期+天数=目标日期
			Calendar cal=Calendar.getInstance();
			//先把字符串转化为日期
			cal.setTime(DateUtil.paseDateyyyyMMddHHmmss(originalDate));
			cal.add(Calendar.DAY_OF_MONTH, days);
			cal.add(Calendar.SECOND, seconds);
			//将日期再转化为字符串
			targetDate=DateUtil.getDateByFormat("yyyy-MM-dd HH:mm:ss",cal.getTime());
		} catch (Exception e) {
		}
		return targetDate;
	}

	/**
	 * 计算两个日期之间相差多少天 ，根据实际情况，可能需要做+1操作
	 * 示例：
	 * 1）、 参数("2015-12-16 00:00:00","2015-12-18 23:59:59","yyyy-MM-dd HH:mm:ss") ,返回2
	 * 2）、 参数("2015-12-16 00:00:00","2015-12-18 00:00:00","yyyy-MM-dd HH:mm:ss") ,返回2
	 * 3）、 参数("2015-12-16 23:59:59","2015-12-18 00:00:00","yyyy-MM-dd HH:mm:ss") ,返回1
	 * 4）、 参数("2015-12-16","2015-12-18","yyyy-MM-dd") ,返回2
	 * @author huangchuankun
	 * @since 2015年12月18日下午1:54:12
	 * @param startDate 开始时间 
	 * @param endDate 结束时间
	 * @param format 日期格式化字符串
	 * @return
	 */
    public static Integer discrepantDays(String startDate,String endDate,String format){
    	try{
	        SimpleDateFormat sdf=new SimpleDateFormat(format);  
	        Date beginDate=sdf.parse(startDate);
	        Date endDat=sdf.parse(endDate);
	        long begin=beginDate.getTime();
	        long end=endDat.getTime();
	        long between_days=(end-begin)/(1000*3600*24); 
	        return Integer.valueOf(String.valueOf(between_days));
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }  
    
    /**
     * 赎回日期,系统当前日期往后加一天(暂未考虑节假日)
     * @return String
     */
    public static String getRedeemNextDayDate(String format){
    	Date date =new Date();  
        Calendar calendar = new GregorianCalendar(); 
        calendar.setTime(date); 
        calendar.add(Calendar.DATE,1);//把日期往后增加一天.
        date=calendar.getTime();  
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date) ;  
    }
    
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期；
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
        	// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
        	// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
        	convertSuccess=false;
        } 
        return convertSuccess;
    }
    
	/**
	 * 用正则表达式判断日期格式是否正确
	 * 		支持yyyy-MM-dd
	 * 	           废止支持yyyy/MM/dd
	 * @author Lee
	 */
	public static boolean isDate(String date) {
		//日期正则: 支持yyyy-MM-dd 或 yyyy/MM/dd
		//String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		//日期正则: 支持yyyy-MM-dd
		String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\s]?((((0?[13578])|(1[02]))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
				
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(date);
		return mat.matches();
	}
    
    
    /**
	 * 获取指定日期加/减N天的日期
	 * 
	 * @param date 指定日期
	 * @param n  指定日期(加/减)的天数
	 * @author Lee
	 * @return Date
	 */
	public static Date getDateAddN(Date date, int n) {
		if(date==null){
			return null;
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		// 把日期往增加一天.整数往后推,负数往前移动
		calendar.add(Calendar.DATE, n);
		return calendar.getTime();
	}
	
	/**
	 * 获取当前月的第一天
	 * @author Lee
	 * @return
	 */
	public static String getMonthFirstDay(Date date){
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		cDay.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
		return fomat.format(cDay.getTime());
	}
	
	/**
	 * 获取当前月的最后一天
	 * @author Lee
	 */
	public static String getMonthLastDay(Date date){
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
		return fomat.format(cDay.getTime());
	}
	
	/**
	 * 获取当前季度第一天
	 * @author Lee
	 */
	public static String getSeasonFirstDay(Date date){
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		int curMonth = cDay.get(Calendar.MONTH);
		if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
			cDay.set(Calendar.MONTH, Calendar.JANUARY);
		}
		if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
			cDay.set(Calendar.MONTH, Calendar.APRIL);
		}
		if (curMonth >= Calendar.JULY && curMonth <= Calendar.SEPTEMBER) {
			cDay.set(Calendar.MONTH, Calendar.JULY);
		}
		if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {
			cDay.set(Calendar.MONTH, Calendar.OCTOBER);
		}
		cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMinimum(Calendar.DAY_OF_MONTH));
		
		SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
        return fomat.format(cDay.getTime());
	}	
	
	/**
	 * 获取当前季度最后一天
	 * @author Lee
	 */
	public static String getSeasonLastDay(Date date){
		Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);
        int curMonth = cDay.get(Calendar.MONTH);  
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH){    
            cDay.set(Calendar.MONTH, Calendar.MARCH);  
        }  
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE){    
            cDay.set(Calendar.MONTH, Calendar.JUNE);  
        }  
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.SEPTEMBER) {    
            cDay.set(Calendar.MONTH, Calendar.SEPTEMBER);  
        }  
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {    
            cDay.set(Calendar.MONTH, Calendar.DECEMBER);  
        }  
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));  
       
        SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
        return fomat.format(cDay.getTime()); 
	}	
	
	/**
	 * 获取年度第一天
	 * @author Lee
	 */
	public static String getYearFirstDay(Date date){
		Calendar cDay=Calendar.getInstance();  
		cDay.setTime(date);  
        int currentYear = cDay.get(Calendar.YEAR); 
        cDay.clear();  
        cDay.set(Calendar.YEAR, currentYear);  
        SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
        return fomat.format(cDay.getTime()); 
	}	
	
	/**
	 * 获取年度最后一天
	 */
	public static String getYearLastDay(Date date){
		Calendar cDay=Calendar.getInstance();    
		cDay.setTime(date);  
        int currentYear = cDay.get(Calendar.YEAR); 
        cDay.clear();  
        cDay.set(Calendar.YEAR, currentYear);  
        cDay.roll(Calendar.DAY_OF_YEAR, -1);  
        
        SimpleDateFormat fomat = new SimpleDateFormat(FORMAT_YYYYMMDDSUB);
        return fomat.format(cDay.getTime()); 
	}	
		
}

