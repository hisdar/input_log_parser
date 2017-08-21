package cn.hisdar.touchpaneltool.common;

import cn.hisdar.lib.log.HLog;


public class HDate {

	public int year = 0;
	public int month = 0;
	public int day = 0;
	public int hour = 0;
	public int minute = 0;
	public int second = 0;
	public int millisecond = 0;
	
	/**
	 * @description format string to HDate, the string format mast yyyy-mm-dd hh:mm:ss
	 * 				or  yyyy-mm-dd hh:mm:ss.mmmm
	 * @param dateString string to format
	 * @return HDate
	 */
	public static HDate format(String dateString) {
		if (dateString == null) {
			HLog.el("HDate.format(): dataString is null");
			return null;
		}
		
		HDate date = new HDate();
		int startIndex = 0;
		int endIndex  = 0;
		
		// parse year
		endIndex = dateString.indexOf('-');
		if (endIndex < 0) {
			HLog.el("HDate.format(): year string not found");
			return null;
		} else {
			date.year = parseInt(dateString.substring(startIndex, endIndex).trim(), 0);
		}
		
		// parse month
		startIndex = endIndex + 1;
		endIndex = dateString.indexOf('-', startIndex);
		if (endIndex < 0) {
			HLog.el("HDate.format(): month string not found");
			return null;
		} else {
			date.month = parseInt(dateString.substring(startIndex, endIndex), 0);
		}
		
		// parse day
		startIndex = endIndex + 1;
		endIndex = dateString.indexOf(' ', startIndex);
		if (endIndex < 0) {
			HLog.el("HDate.format(): day string not found");
			return null;
		} else {
			date.day = parseInt(dateString.substring(startIndex, endIndex), 0);
		}
		
		// parse hour
		startIndex = endIndex + 1;
		endIndex = dateString.indexOf(':', startIndex);
		if (endIndex < 0) {
			HLog.el("HDate.format(): hour string not found");
			return null;
		} else {
			date.hour = parseInt(dateString.substring(startIndex, endIndex), 0);
		}
		
		// parse minute
		startIndex = endIndex + 1;
		endIndex = dateString.indexOf(':', startIndex);
		if (endIndex < 0) {
			HLog.el("HDate.format(): minute string not found");
			return null;
		} else {
			date.minute = parseInt(dateString.substring(startIndex, endIndex), 0);
		}
		
		// parse second
		startIndex = endIndex + 1;
		endIndex = dateString.indexOf('.', startIndex);
		if (endIndex < 0) {
			date.second = parseInt(dateString.substring(startIndex).trim(), 0);
		} else {
			date.minute = parseInt(dateString.substring(startIndex, endIndex), 0);
		}
		
		// parse millisecond 
		startIndex = dateString.indexOf('.');
		if (startIndex >= 0) {
			date.millisecond = parseInt(dateString.substring(startIndex).trim(), 0);
		}

		return date;
	}
	
	/**
	 * @description compare to date 
	 * @param date 
	 * @return if early than date, return -1,
	 * 			if equals date, return 0;
	 * 			if later than date, return 1
	 */
	public int compareTo(HDate date) {
		
		if (date == null) {
			return 1;
		}
		
		// compare year
		if (this.year > date.year) {
			return 1;
		} else if (this.year < date.year) {
			return -1;
		}
		
		// compare month
		if (this.month > date.month) {
			return 1;
		} else if (this.month < date.month) {
			return -1;
		}
		
		// compare day
		if (this.day > date.day) {
			return 1;
		} else if (this.day < date.day) {
			return -1;
		}
		
		// compare hour
		if (this.hour > date.hour) {
			return 1;
		} else if (this.hour < date.hour) {
			return -1;
		}
		
		// compare minute
		if (this.minute > date.minute) {
			return 1;
		} else if (this.minute < date.minute) {
			return -1;
		}
		
		// compare second
		if (this.second > date.second) {
			return 1;
		} else if (this.second < date.second) {
			return -1;
		}
		
		// compare millisecond
		if (this.millisecond > date.millisecond) {
			return 1;
		} else if (this.millisecond < date.millisecond) {
			return -1;
		}
		
		return 0;
	}
	
	private static int parseInt(String number, int defaultNumber) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			HLog.el(e);
		}
		
		return defaultNumber;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getMillisecond() {
		return millisecond;
	}

	public void setMillisecond(int millisecond) {
		this.millisecond = millisecond;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + hour;
		result = prime * result + millisecond;
		result = prime * result + minute;
		result = prime * result + month;
		result = prime * result + second;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HDate other = (HDate) obj;
		if (day != other.day)
			return false;
		if (hour != other.hour)
			return false;
		if (millisecond != other.millisecond)
			return false;
		if (minute != other.minute)
			return false;
		if (month != other.month)
			return false;
		if (second != other.second)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HDate [year=" + year + ", month=" + month + ", day=" + day
				+ ", hour=" + hour + ", minute=" + minute + ", second="
				+ second + ", millisecond=" + millisecond + "]";
	}
}
