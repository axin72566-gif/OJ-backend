package com.axin.OJ.model.entity;


import lombok.Data;

@Data
public class JudgeInfo {

	/**
	 * 时间消耗（ms）
	 */
	private Long timeConsumption;

	/**
	 * 内存消耗（KB）
	 */
	private Long memoryConsumption;

	/**
	 * 程序执行后返回的信息
	 */
	private String runMessage;
}
