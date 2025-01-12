package com.gdevxy.blog.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Strings {

	public String blankToNull(String s) {

		if (s == null) {
			return null;
		}

		if (s.trim().isEmpty()) {
			return null;
		}

		return s;
	}

}
