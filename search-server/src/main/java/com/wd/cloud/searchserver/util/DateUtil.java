package com.wd.cloud.searchserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	public static String getDate(String date,int year,int mounth,int day){
		String reStr="";
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
		Calendar rightNow = Calendar.getInstance();
		Date dt=null;
		try {
			dt = sf.parse(date);
		} catch (ParseException e) {
			System.out.println("日期格式错误");
			e.printStackTrace();
		}
        rightNow.setTime(dt);
        rightNow.add(Calendar.YEAR,year);//日期减1年
        rightNow.add(Calendar.MONTH,mounth);//日期加3个月
        rightNow.add(Calendar.DAY_OF_YEAR,day);//日期加10天
        Date dt1=rightNow.getTime();
        reStr = sf.format(dt1);
		return reStr;
	}
	
	/**
	 * 根据开始、结束时间获取时间差
	 * @param endTime
	 * @param beginTime
	 * @return
	 */
	public static int getDayNum(String endTime,String beginTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date now = null;
		Date date = null;
		try {
			now = df.parse(endTime);
			date=df.parse(beginTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long l=now.getTime()-date.getTime();
		int day=(int) (l/(24*60*60*1000));
		return day;
	}
	/**
	 * 获取时间差 秒s
	 * @param endTime
	 * @param beginTime
	 * @return
	 */
	public static int getTimeNum(String endTime,String beginTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		Date date = null;
		try {
			now = df.parse(endTime);
			date=df.parse(beginTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long l=now.getTime()-date.getTime();
		int time=(int) (l/1000);
		return time;
	}
	
	/**
	 * 判断某一时间是否在一个区间内
	 * 
	 * @param sourceTime
	 *            时间区间,半闭合,如[10:00-20:00)
	 * @param curTime
	 *            需要判断的时间 如10:00
	 * @return 
	 * @throws IllegalArgumentException
	 */
	public static boolean isInTime(String sourceTime, String curTime) {
	    if (sourceTime == null || !sourceTime.contains("/") || !sourceTime.contains("-")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }
	    if (curTime == null || !curTime.contains("-")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
	    }
	    String[] args = sourceTime.split("/");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    try {
	        long now = sdf.parse(curTime).getTime();
	        long start = sdf.parse(args[0]).getTime();
	        long end = sdf.parse(args[1]).getTime();
	        if ("00:00".equals(args[1])) {
	            args[1] = "24:00";
	        }
	        if (end < start) {
	            if (now >= end && now < start) {
	                return false;
	            } else {
	                return true;
	            }
	        } 
	        else {
	            if (now >= start && now <= end) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }
	}
	
	public static int getMonth(String beginTime, String endTime) {
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		try {
			beginDate = sf.parse(beginTime);
		} catch (ParseException e) {
			 throw new IllegalArgumentException("Illegal Argument arg:" + beginTime);
		}
		Date endDate;
		try {
			endDate = sf.parse(endTime);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Illegal Argument arg:" + endTime);
		}
		return (endDate.getYear() - beginDate.getYear()) * 12 + endDate.getMonth() - beginDate.getMonth();
	}
	
	/**
	 * 根据日期得到当前月份开始时间
	 * @param date
	 * @param num
	 * @return
	 */
	public static String getMonthBeginDate(String date,int num){
		String reStr="";
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		try {
			beginDate = sf.parse(date);
		} catch (ParseException e) {
			 throw new IllegalArgumentException("Illegal Argument arg:" + date);
		}
		beginDate.setDate(1);
		beginDate.setMonth(beginDate.getMonth()+num);
		Calendar   cDay1   =   Calendar.getInstance();  
        cDay1.setTime(beginDate);  
//      int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
//		beginDate.setDate(1);
        reStr = sf.format(beginDate);
		return reStr;
	}
	
	/**
	 * 根据日期得到当前月份（结束时间）
	 * @param date
	 * @param num
	 * @return
	 */
	public static String getMonthEndDate(String date,int num){
		String reStr="";
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		try {
			beginDate = sf.parse(date);
		} catch (ParseException e) {
			 throw new IllegalArgumentException("Illegal Argument arg:" + date);
		}
		beginDate.setMonth(beginDate.getMonth()+num);
		Calendar   cDay1   =   Calendar.getInstance();  
        cDay1.setTime(beginDate);  
        int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		beginDate.setDate(lastDay);
        reStr = sf.format(beginDate);
		return reStr;
	}
	
	/**
	 * 根据开始时间和结束时间获取所有时间格式
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static List<String> dayList(String beginTime, String endTime)  {
		List<String> dayList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式  
		Date beginDate = null;
		Date endDate =null;
		try {
			beginDate = sdf.parse(beginTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
        Calendar beginCal = Calendar.getInstance();  
        beginCal.setTime(beginDate);  
        
        Calendar endCal = Calendar.getInstance();  
        endCal.setTime(endDate); 
        endCal.set(Calendar.DATE, endCal.get(Calendar.DATE) + 1);
        
        while(!beginCal.after(endCal)) {
        	dayList.add(sdf.format(beginCal.getTime()));
        	beginCal.set(Calendar.DATE, beginCal.get(Calendar.DATE) + 1);
        }
        return dayList;
	}
	
	/**
	 * 根据开始时间和结束时间获取所有时间格式
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static List<String> dayList(String beginTime, String endTime,int day)  {
		List<String> dayList = new ArrayList<String>();
		//按日、按周、按月
		if(day == 1 ) {
			SimpleDateFormat sdf = null;
			if(beginTime.equals(endTime)) {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH"); 
				beginTime = beginTime + " 00";
				endTime = endTime + " 00";
			} else {
				sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式  
			}
			Date beginDate = null;
			Date endDate =null;
			try {
				beginDate = sdf.parse(beginTime);
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        Calendar beginCal = Calendar.getInstance();  
	        beginCal.setTime(beginDate);  
	        Calendar endCal = Calendar.getInstance();  
	        endCal.setTime(endDate); 
	        endCal.set(Calendar.DATE, endCal.get(Calendar.DATE) + 1);
	        while(!beginCal.after(endCal)) {
	        	dayList.add(sdf.format(beginCal.getTime()));
	        	if(beginTime.equals(endTime)) {
	        		beginCal.set(Calendar.HOUR_OF_DAY, beginCal.get(Calendar.HOUR_OF_DAY) + 1);
	        	} else {
	        		beginCal.set(Calendar.DATE, beginCal.get(Calendar.DATE) + 1);
	        	}
	        }
		}
		String bgtime="",edtime="";
		if(2 == day) {
			long dayNum =DateUtil.getDayNum(endTime, beginTime)+1;
			for(int i=0;i<dayNum;i+=7){
				bgtime=DateUtil.getDate(beginTime, 0, 0, i);
				edtime=DateUtil.getDate(beginTime, 0, 0, i+6);
				if(i == 0) {
                    bgtime =beginTime;
                }
				if((dayNum - i) < 7) {
                    edtime = endTime;
                }
				dayList.add(bgtime+"/"+edtime);
			} 
			 return dayList;
		}
		if(3==day) {
			long monthNum = DateUtil.getMonth(beginTime, endTime)+1;
			for(int i=0;i<monthNum;i++){
				bgtime=DateUtil.getMonthBeginDate(beginTime,i);
				edtime=DateUtil.getMonthEndDate(beginTime,i);
				if(i == 0) {
                    bgtime =beginTime;
                }
				if(i == monthNum-1) {
                    edtime = endTime;
                }
				dayList.add(bgtime+"/"+edtime);
			}
			 return dayList;
		}
        return dayList;
	}
	/**
	 * 根据最初时间和增加时间获取结尾时间
	 * @param beginTime
	 * @param time
	 * @return
	 */
	public static String getEndTime(String beginTime , int time) {
		String endTime = null;
		SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		try {
			now = sf.parse(beginTime);
			now = new Date(now.getTime() + time*1000);
			endTime = sf.format(now);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endTime;
	}
	
	/**
	 * 生成随机时间
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static String randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = format.parse(beginDate);// 构造开始日期
			Date end = format.parse(endDate);// 构造结束日期
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());
			return format.format(new Date(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}
	/**
	 * 处理开始时间（如果beginTime为空就给当前时间）
	 * @return
	 */
	public static String handleBeginTime(String beginTime) {
		Date date = new Date();
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
		if(beginTime == null) {
			return sp.format(date);
		} else {
			try {
				Date beginDate = sp.parse(beginTime);
				if(beginDate.after(date)) {
					return sp.format(date);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return beginTime;
	}
	/**
	 * 处理结束时间（如果endTime为空或者是当天就给当前时间，如果是今天之前就给当天时间+23:59:59）
	 * @param endTime
	 * @return
	 */
	public static String handleEndTime(String endTime) {
		Date date = new Date();
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(endTime == null) {
			return sp.format(date);
		} else {
			try {
				endTime = endTime.substring(0,10) + " 23:59:59";
				Date endDate = sp.parse(endTime);
				if(endDate.after(date)) {
					return sp.format(date);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return endTime;
	}
	
	public static Date toDate(String time) {
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sp.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	
	public static String format(Date date){
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sp.format(date);
	}
	/**
	 * 生成随机时间  根据时间段0~6；6~12；12~18；18~24
	 * @param num 从第几个时间段生成随机时间
	 * @return
	 */
	public static String randomDate(int num) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int time = 0;
			switch (num) { //时间段：0~6；6~12；12~18；18~24
			case 0:
				time = 0;
				break;
			case 1:
				time = 6;
				break;
			case 2:
				time = 12;
				break;
			default:
				time = 18;
				break;
			}
			Date start = new Date();// 构造开始日期
			start.setHours(time);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = new Date();// 构造结束日期
			end.setHours(time+6);
			end.setMinutes(0);
			end.setSeconds(0);
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());
			return format.format(new Date(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getNowMonth() {
		Date nowTime = new Date();
		return nowTime.getMonth()+1;
	}

}
