package com.axin.OJ.judge.model;

import com.axin.OJ.model.entity.JudgeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 代码沙箱执行完代码的返回结果
 */
@Data
public class ExecuteCodeResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 执行状态
	 */
	private Integer status;

	/**
	 * 执行结果信息 judgeInfo里面也有
	 */
	private String message;

	/**
	 * 时间消耗 内存消耗 程序执行信息
	 */
	private JudgeInfo judgeInfo;

	/**
	 * 输出用例
	 */
	private List<String> outputList;

}
