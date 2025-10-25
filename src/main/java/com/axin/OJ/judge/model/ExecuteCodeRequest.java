package com.axin.OJ.judge.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 调用代码沙箱的请求
 */
@Data
public class ExecuteCodeRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 给代码沙箱一组输入让他执行代码
	 */
	private List<String> inputList;

	/**
	 * 用户输入的代码
	 */
	private String code;

	/**
	 * 用户选择的编程语言
	 */
	private String language;
}
