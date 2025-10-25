package com.axin.OJ.judge.strategy;

import com.axin.OJ.model.entity.JudgeCase;
import com.axin.OJ.model.entity.JudgeInfo;
import com.axin.OJ.model.entity.Question;
import com.axin.OJ.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在判题策略中传递的参数）
 */
@Data
public class JudgeContext {

	/**
	 * 沙箱执行后的反馈，时间内存和运行结果
	 */
	private JudgeInfo judgeInfo;

	/**
	 * 输入用例
	 */
	private List<String> inputList;

	/**
	 * 输出用例
	 */
	private List<String> outputList;

	/**
	 * 题目的多个判题用例
	 */
	private List<JudgeCase> judgeCaseList;

	/**
	 * 题目
	 */
	private Question question;

	/**
	 * 提交记录
	 */
	private QuestionSubmit questionSubmit;
}
