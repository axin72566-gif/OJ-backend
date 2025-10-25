package com.axin.OJ.judge;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.judge.codesandbox.CodeSandbox;
import com.axin.OJ.judge.codesandbox.CodeSandboxFactory;
import com.axin.OJ.judge.codesandbox.CodeSandboxProxy;
import com.axin.OJ.judge.model.ExecuteCodeRequest;
import com.axin.OJ.judge.model.ExecuteCodeResponse;
import com.axin.OJ.judge.strategy.JudgeContext;
import com.axin.OJ.model.entity.*;
import com.axin.OJ.model.enums.JudgeInfoMessageEnum;
import com.axin.OJ.model.enums.QuestionSubmitStatusEnum;
import com.axin.OJ.service.QuestionService;
import com.axin.OJ.service.QuestionSubmitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

	@Resource
	private QuestionSubmitService questionSubmitService;

	@Resource
	private QuestionService questionService;

	/**
	 * 代码沙箱类型 后面可以在配置中指定沙箱的类型
	 */
	private final String codeSandboxType = "remote";

	/**
	 * 从数据库里面取题目提交记录
	 *
	 * @param questionSubmitId 用户提交记录id 判这个记录
	 */
	@Override
	public QuestionSubmit doJudge(Long questionSubmitId) {
		log.info("开始进行异步判题，题目提交记录id为：{}", questionSubmitId);
		if (questionSubmitId == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交记录id不存在");
		}
		// 查出来提交记录  此时judgeInfo是空的
		QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
		if (questionSubmit == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
		}
		// 获取题目
		Long questionId = questionSubmit.getQuestionId();
		if (questionId == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目id不存在");
		}
		// 查出来题目
		Question question = questionService.getById(questionId);
		if (question == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
		}
		// 现在提交记录和题目都获取了
		// 看看提交记录的状态是不是等待判题中  数据库中默认待判题
		if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交记录状态不是等待判题中");
		}
		// 提交记录处于等待中，先修改状态为判题中
		QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
		questionSubmitUpdate.setId(questionSubmitId);
		questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
		boolean updateStatus = questionSubmitService.updateById(questionSubmitUpdate);
		if (!updateStatus) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新提交记录状态失败");
		}
		// 封装代码沙箱请求
		ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
		// 获取判题用的输入用例 只有题目对象里有
		String judgeCaseStr = question.getJudgeCase();
		if (judgeCaseStr == null || judgeCaseStr.isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目判题用例为空");
		}
		// 把json字符串转换为JudgeCase对象的列表
		List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
		if (judgeCaseList == null || judgeCaseList.isEmpty()) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目判题用例格式错误");
		}
		// 从JudgeCase对象中提取输入用例
		List<String> inputList = judgeCaseList.stream()
				.map(JudgeCase::getInput)
				.collect(Collectors.toList());
		executeCodeRequest.setInputList(inputList);
		executeCodeRequest.setCode(questionSubmit.getCode());
		executeCodeRequest.setLanguage(questionSubmit.getLanguage());
		// 获取代码沙箱实例
		CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(codeSandboxType);
		// 创建代码沙箱代理
		codeSandbox = new CodeSandboxProxy(codeSandbox);
		// 调用代码沙箱执行代码 (执行状态 执行信息 JudgeInfo 输出用例)
		ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
		log.info("代码沙箱返回结果：{}", executeCodeResponse);
		// 判题上下文 条件和答案都有了，交给判题模块
		JudgeContext judgeContext = new JudgeContext();
		// 提交记录
		judgeContext.setQuestionSubmit(questionSubmit);
		// 题目
		judgeContext.setQuestion(question);
		// 输入输出用例
		judgeContext.setJudgeCaseList(judgeCaseList);
		judgeContext.setInputList(inputList);
		judgeContext.setOutputList(executeCodeResponse.getOutputList());
		// 沙箱返回的信息
		judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
		// 判题策略管理器 选择一个判题策略
		JudgeManager judgeManager = new JudgeManager();
		// 判题
		JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
		log.info("判题结果：{}", judgeInfo);
		//6.修改数据库中的判题结果
		questionSubmitUpdate = new QuestionSubmit();
		questionSubmitUpdate.setId(questionSubmitId);
		if (!judgeInfo.getRunMessage().equals(JudgeInfoMessageEnum.ACCEPT.getValue())) {
			questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
			questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
			updateStatus = questionSubmitService.updateById(questionSubmitUpdate);
			if (!updateStatus) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新提交记录状态失败");
			}
		} else {
			questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
			// 更新题目的通过数
			boolean updateAcceptedNum = questionService.
					update(new UpdateWrapper<Question>().eq("id", questionId).
							setSql("acceptedNum = acceptedNum + 1"));
			if (!updateAcceptedNum) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新题目通过数失败");
			}
			questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
			updateStatus = questionSubmitService.updateById(questionSubmitUpdate);
			if (!updateStatus) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新提交记录状态失败");
			}
		}
		questionSubmit = questionSubmitService.getById(questionSubmitId);
		return questionSubmit;
	}
}
