package com.axin.OJ.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.model.entity.*;
import com.axin.OJ.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {


	/**
	 * 执行判题
	 * @param judgeContext 判题上下文
	 * @return 判题信息
	 */
	@Override
	public JudgeInfo doJudge(JudgeContext judgeContext) {
		// 要返回的结果
		JudgeInfo judgeInfoResult = new JudgeInfo();
		// 获取沙箱执行完返回的参数和题目要求参数
		// 获取执行代码的时间，内存和运行信息
		JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
		// 从题目获取的输入用例
		List<String> inputList = judgeContext.getInputList();
		// 输出用例
		List<String> outputList = judgeContext.getOutputList();
		// 标准答案
		List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
		// 原题
		Question question = judgeContext.getQuestion();
		// 先设置执行本身消耗的时间和内存
		judgeInfoResult.setMemoryConsumption(judgeInfo.getMemoryConsumption());
		judgeInfoResult.setTimeConsumption(judgeInfo.getTimeConsumption());
		// 根据代码沙箱执行的结果，设置本次提交的状态和信息 也就是runMessage
		JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.WAITING;
		// 下面根据沙箱返回的结果和题目设置的标准对比设置judgeInfoMessageEnum
		// 首先根据输出用例个数和输入用例个数判断是否匹配
		if (outputList.size() != inputList.size()) {
			judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
			judgeInfoResult.setRunMessage(judgeInfoMessageEnum.getValue());
			return judgeInfoResult;
		}
		// 接着一个一个比对用例是否匹配
		for (int i = 0; i < judgeCaseList.size(); i++) {
			if (!outputList.get(i).equals(judgeCaseList.get(i).getOutput())) {
				judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
				judgeInfoResult.setRunMessage(judgeInfoMessageEnum.getValue());
				return judgeInfoResult;
			}
		}
		// 比对时间和内存消耗
		String judgeConfig = question.getJudgeConfig();
		if (judgeConfig == null || judgeConfig.isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目判题配置为空");
		}
		// 把json字符串转换为JudgeConfig对象
		JudgeConfig judgeConfigObj = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
		if (judgeConfigObj == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目判题配置格式错误");
		}
		// 对比时间和内存消耗
		if (judgeContext.getJudgeInfo() != null) {
			// 时间消耗对比
			if (judgeContext.getJudgeInfo().getTimeConsumption() > judgeConfigObj.getTimeLimit()) {
				judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
				judgeInfoResult.setRunMessage(judgeInfoMessageEnum.getValue());
				return judgeInfoResult;
			}
			// 内存消耗对比
			if (judgeContext.getJudgeInfo().getMemoryConsumption() > judgeConfigObj.getMemoryLimit()) {
				judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
				judgeInfoResult.setRunMessage(judgeInfoMessageEnum.getValue());
				return judgeInfoResult;
			}
		}
		// 都没有问题，那么就是AC了
		judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPT;
		judgeInfoResult.setRunMessage(judgeInfoMessageEnum.getValue());
		return judgeInfoResult;
	}
}
