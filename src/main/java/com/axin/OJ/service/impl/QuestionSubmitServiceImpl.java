package com.axin.OJ.service.impl;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.judge.JudgeService;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.axin.OJ.model.entity.Question;
import com.axin.OJ.model.entity.User;
import com.axin.OJ.model.enums.QuestionSubmitLanguageEnum;
import com.axin.OJ.model.vo.QuestionSubmitVO;
import com.axin.OJ.model.vo.UserVO;
import com.axin.OJ.service.QuestionService;
import com.axin.OJ.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.axin.OJ.model.entity.QuestionSubmit;
import com.axin.OJ.service.QuestionSubmitService;
import com.axin.OJ.mapper.QuestionSubmitMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author kdkt1
* &#064;description 针对表【question_submit(题目提交)】的数据库操作Service实现
* &#064;createDate 2025-10-12 19:16:35
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

	@Resource
	private UserService userService;

	@Resource
	@Lazy
	private JudgeService judgeService;

	@Resource
	private QuestionService questionService;

	/**
	 * 提交题目
	 * @param questionSubmitAddRequest 提交题目请求
	 * @param loginUser 当前登录用户
	 * @return 题目提交 id
	 */
	@Override
	public Long addQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
		// 参数校验
		if (questionSubmitAddRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 要添加的记录
		QuestionSubmit questionSubmit = new QuestionSubmit();
		// 组装信息
		// 判断题目 id 是否合法
		Long questionId = questionSubmitAddRequest.getQuestionId();
		if (questionId == null || questionId <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目 id 错误");
		}
		// 题目提交数 + 1
		boolean updateSubmitNum = questionService.update(new UpdateWrapper<Question>().eq("id", questionId).setSql("submitNum = submitNum + 1"));
		if (!updateSubmitNum) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目提交数增加失败");
		}
		questionSubmit.setQuestionId(questionId);
		// 编程语言是否为空
		if (questionSubmitAddRequest.getLanguage() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
		}
		// 判断语言是否在规定范围内
		QuestionSubmitLanguageEnum questionSubmitLanguageEnum = QuestionSubmitLanguageEnum.getEnumByValue(questionSubmitAddRequest.getLanguage());
		if (questionSubmitLanguageEnum == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不在规定范围内");
		}
		questionSubmit.setLanguage(questionSubmitLanguageEnum.getValue());
		// 判断代码是否为空
		if (questionSubmitAddRequest.getCode() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码为空");
		}
		questionSubmit.setCode(questionSubmitAddRequest.getCode());
		// 用户信息是否为空
		if (loginUser == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未登录");
		}
		questionSubmit.setUserId(loginUser.getId());
		questionSubmit.setJudgeInfo("{}");
		boolean result = this.save(questionSubmit);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交失败");
		}
		// 获取提交记录的id
		Long questionSubmitId = questionSubmit.getId();
		// 对这条记录判题 异步
//		CompletableFuture.runAsync(() -> {judgeService.doJudge(questionSubmitId);});
		judgeService.doJudge(questionSubmitId);
		return questionSubmitId;

	}

	/**
	 * 获取题目提交列表
	 * @param questionSubmitQueryRequest 查询题目提交请求
	 * @param loginUser 当前登录用户
	 * @return 题目提交列表
	 */
	@Override
	public Page<QuestionSubmitVO> listQuestionSubmitByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser) {
		if (questionSubmitQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		if (loginUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
		if (questionSubmitQueryRequest.getId() != null) {
			queryWrapper.eq("id", questionSubmitQueryRequest.getId());
		}
		if (questionSubmitQueryRequest.getLanguage() != null) {
			queryWrapper.eq("language", questionSubmitQueryRequest.getLanguage());
		}
		if (questionSubmitQueryRequest.getJudgeInfo() != null) {
			queryWrapper.eq("judgeInfo", JSONUtil.toJsonStr(questionSubmitQueryRequest.getJudgeInfo()));
		}
		if (questionSubmitQueryRequest.getStatus() != null) {
			queryWrapper.eq("status", questionSubmitQueryRequest.getStatus());
		}
		if (questionSubmitQueryRequest.getQuestionId() != null) {
			queryWrapper.eq("questionId", questionSubmitQueryRequest.getQuestionId());
		}
		if (questionSubmitQueryRequest.getUserId() != null) {
			queryWrapper.eq("userId", questionSubmitQueryRequest.getUserId());
		}
		// 分页查出来
		Page<QuestionSubmit> questionSubmitPage = this.page(new Page<>(questionSubmitQueryRequest.getCurrent(), questionSubmitQueryRequest.getPageSize()), queryWrapper);
		// 用户id分出来
		Set<Long> userIdSet = questionSubmitPage.getRecords().stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
		if (userIdSet.isEmpty()) {
			return new Page<>();
		}
		Map<Long, List<User>> userIdUserMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
		// 转换成VO
		List<QuestionSubmitVO> questionSubmitVOList = questionSubmitPage.getRecords().stream().map(questionSubmit -> {
			QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
			questionSubmitVO.setUser(UserVO.objToVo(userIdUserMap.get(questionSubmit.getUserId()).get(0)));
			return questionSubmitVO;
		}).collect(Collectors.toList());
		// 分页返回
		Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitQueryRequest.getCurrent(), questionSubmitQueryRequest.getPageSize());
		questionSubmitVOPage.setRecords(questionSubmitVOList);
		return questionSubmitVOPage;
	}
}




