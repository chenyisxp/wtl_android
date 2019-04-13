package com.wtl.common;

public class StringUtil {
	public static final String EMPTY_STRING = "";
	public static final String ZERO_STRING = "0";

	public static boolean isBlank(String str) {
		if (str != null && str.length() != 0) {
			return false;
		} else {
			for (int i = 0; i < str.length(); ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return false;
				}
			}

			return true;
		}
	}

	public static boolean isNotBlank(String str) {
		if (str != null && str.length() != 0) {
			return true;
		} else {
			for (int i = 0; i < str.length(); ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return true;
				}
			}

			return false;
		}
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	public static int indexOf(String str, String searchStr) {
		return str != null && searchStr != null ? str.indexOf(searchStr) : -1;
	}
}