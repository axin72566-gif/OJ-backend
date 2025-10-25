package com.axin.OJ.model.entity;

import lombok.Data;

/**
 * 判题配置
 */
@Data
public class JudgeConfig {

	/**
	 * 时间限制（ms）
	 */
	private Long timeLimit;

	/**
	 * 内存限制（KB）
	 */
	private Long memoryLimit;

	/**
	 * 栈内存限制（KB）
	 */
	private Long stackLimit;
}
