package com.axin.OJ.judge;

import com.axin.OJ.judge.strategy.DefaultJudgeStrategy;
import com.axin.OJ.judge.strategy.JavaJudgeStrategy;
import com.axin.OJ.judge.strategy.JudgeContext;
import com.axin.OJ.judge.strategy.JudgeStrategy;
import com.axin.OJ.model.entity.JudgeInfo;
import com.axin.OJ.model.entity.QuestionSubmit;

/*
 * @description: 判题策略管理器
 */
public class JudgeManager {

	/**
	 * 选定判题策略
	 * @param judgeContext 判题上下文
	 * @return 判题信息
	 */
	public JudgeInfo doJudge(JudgeContext judgeContext) {
		// 获取判题记录
		QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
		// 获取语言 实际上目前选择策略就是根据语言来选择策略
		String language = questionSubmit.getLanguage();
		JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
		if("java".equals(language)){
			judgeStrategy = new JavaJudgeStrategy();
		}
		return judgeStrategy.doJudge(judgeContext);
	}
}
