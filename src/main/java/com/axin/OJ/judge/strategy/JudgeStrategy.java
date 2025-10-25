package com.axin.OJ.judge.strategy;

import com.axin.OJ.model.entity.JudgeInfo;

/**
 * 判题策略  是代码沙箱执行完成之后反馈的数据和题目的要求都有了才开始判题
 */
public interface JudgeStrategy {
	/**
	 * 执行判题
	 * @param judgeContext 判题上下文
	 * @return 判题信息
	 */
	JudgeInfo doJudge(JudgeContext judgeContext);
}
