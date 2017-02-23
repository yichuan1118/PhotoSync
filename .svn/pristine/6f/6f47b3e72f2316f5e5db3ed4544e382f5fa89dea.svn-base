package com.metaisle.util;

import java.util.*;
import java.util.regex.*;

public class FindUrls {

	// public static Pattern pattern = Pattern.compile(
	// "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
	// "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
	// "|mil|biz|info|mobi|name|aero|jobs|museum" +
	// "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
	// "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
	// "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
	// "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
	// "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
	// "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
	// "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

	public static Pattern pattern = Pattern
			.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	public static List<String> extractUrls(String input) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			result.add(matcher.group());
		}

		return result;
	}
}
