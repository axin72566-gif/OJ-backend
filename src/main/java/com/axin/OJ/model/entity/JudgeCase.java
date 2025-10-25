package com.axin.OJ.model.entity;

import lombok.Data;

/**
 * 题目用例
 */
@Data
public class JudgeCase {

	/**
	 * 输入用例  "1 2"
	 */
	private String input;

	/**
	 * 输出用例  "3"
	 */
	private String output;
}

