package com.axin.OJ.controller;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.annotation.AuthCheck;
import com.axin.OJ.common.BaseResponse;
import com.axin.OJ.common.DeleteRequest;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.common.ResultUtils;
import com.axin.OJ.constant.UserConstant;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.exception.ThrowUtils;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.axin.OJ.model.dto.question.QuestionAddRequest;
import com.axin.OJ.model.dto.question.QuestionEditRequest;
import com.axin.OJ.model.dto.question.QuestionQueryRequest;
import com.axin.OJ.model.dto.question.QuestionUpdateRequest;
import com.axin.OJ.model.entity.JudgeCase;
import com.axin.OJ.model.entity.JudgeConfig;
import com.axin.OJ.model.entity.Question;
import com.axin.OJ.model.entity.User;
import com.axin.OJ.model.vo.QuestionSubmitVO;
import com.axin.OJ.model.vo.QuestionVO;
import com.axin.OJ.service.QuestionService;
import com.axin.OJ.service.QuestionSubmitService;
import com.axin.OJ.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

	@Resource
	private QuestionService questionService;

	@Resource
	private UserService userService;

	@Resource
	private QuestionSubmitService questionSubmitService;

	/**
	 * 创建
	 *
	 * @param questionAddRequest 题目添加请求
	 * @param request 请求
	 * @return 创建成功 id
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
		if (questionAddRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Question question = new Question();
		JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
		if (judgeConfig != null) {
			question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
		}
		List<JudgeCase> judgeCases = questionAddRequest.getJudgeCase();
		if (judgeCases != null) {
			question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
		}
		BeanUtils.copyProperties(questionAddRequest, question);
		List<String> tags = questionAddRequest.getTags();
		if (tags != null) {
			question.setTags(JSONUtil.toJsonStr(tags));
		}
		questionService.validQuestion(question, true);
		User loginUser = userService.getLoginUser(request);
		question.setUserId(loginUser.getId());
		question.setFavourNum(0);
		question.setThumbNum(0);
		boolean result = questionService.save(question);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		long newQuestionId = question.getId();
		return ResultUtils.success(newQuestionId);
	}

	/**
	 * 删除
	 *
	 * @param deleteRequest 删除请求
	 * @param request 请求
	 * @return 删除成功
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Question oldQuestion = questionService.getById(id);
		ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		boolean b = questionService.removeById(id);
		return ResultUtils.success(b);
	}

	/**
	 * 更新（仅管理员）
	 *
	 * @param questionUpdateRequest 修改请求
	 * @return 更新成功
	 */
	 @PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
		if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Question question = new Question();
		BeanUtils.copyProperties(questionUpdateRequest, question);
		List<String> tags = questionUpdateRequest.getTags();
		if (tags != null) {
			question.setTags(JSONUtil.toJsonStr(tags));
		}
		// 参数校验
		questionService.validQuestion(question, false);
		long id = questionUpdateRequest.getId();
		// 判断是否存在
		Question oldQuestion = questionService.getById(id);
		ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		boolean result = questionService.updateById(question);
		return ResultUtils.success(result);
	}

	/**
	 * 根据 id 获取
	 *
	 * @param id 题目 id
	 * @param request 请求
	 * @return 题目 vo
	 */
	@GetMapping("/get/vo")
	public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Question question = questionService.getById(id);
		if (question == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		return ResultUtils.success(questionService.getQuestionVO(question, request));
	}

	/**
	 * 分页获取列表（仅管理员）
	 *
	 * @param questionQueryRequest 查询请求
	 * @return 题目分页
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
		long current = questionQueryRequest.getCurrent();
		long size = questionQueryRequest.getPageSize();
		Page<Question> questionPage = questionService.page(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest));
		return ResultUtils.success(questionPage);
	}

	/**
	 * 分页获取列表（封装类）
	 *
	 * @param questionQueryRequest 查询请求
	 * @param request 请求
	 * @return 题目分页 vo
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
		long current = questionQueryRequest.getCurrent();
		long size = questionQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<Question> questionPage = questionService.page(new Page<>(current, size),
				questionService.getQueryWrapper(questionQueryRequest));
		return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
	}

	/**
	 * 分页获取当前用户创建的题目列表
	 *
	 * @param questionQueryRequest 查询请求
	 * @param request 请求
	 * @return 题目分页 vo
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
		if (questionQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		questionQueryRequest.setUserId(loginUser.getId());
		long current = questionQueryRequest.getCurrent();
		long size = questionQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<Question> questionPage = questionService.page(new Page<>(current, size),
				questionService.getQueryWrapper(questionQueryRequest));
		return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
	}

	/**
	 * 编辑（用户）
	 *
	 * @param questionEditRequest 修改请求
	 * @param request 请求
	 * @return 更新成功
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
		if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Question question = new Question();
		BeanUtils.copyProperties(questionEditRequest, question);
		List<String> tags = questionEditRequest.getTags();
		if (tags != null) {
			question.setTags(JSONUtil.toJsonStr(tags));
		}
		// 参数校验
		questionService.validQuestion(question, false);
		User loginUser = userService.getLoginUser(request);
		long id = questionEditRequest.getId();
		// 判断是否存在
		Question oldQuestion = questionService.getById(id);
		ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		boolean result = questionService.updateById(question);
		return ResultUtils.success(result);
	}

	/**
	 * 提交题目
	 * @param questionSubmitAddRequest 提交题目请求
	 * @param request 请求
	 * @return 题目提交 ID
	 */
	@PostMapping("/question_submit/do")
	public BaseResponse<Long> addQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
		// 参数校验
		if (questionSubmitAddRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交题目参数为空");
		}
		// 获取当前提交的用户
		User loginUser = userService.getLoginUser(request);
		return ResultUtils.success(questionSubmitService.addQuestionSubmit(questionSubmitAddRequest, loginUser));
	}

	/**
	 * 获取提交题目列表（分页）
	 * @param questionSubmitQueryRequest 查询请求
	 * @param request 请求
	 * @return 题目提交列表
	 */
	@PostMapping("/question_submit/list/page")
	public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
		// 参数校验
		if (questionSubmitQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交题目参数为空");
		}
		// 获取当前提交的用户
		User loginUser = userService.getLoginUser(request);
		if (loginUser ==  null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
		}
		return ResultUtils.success(questionSubmitService.listQuestionSubmitByPage(questionSubmitQueryRequest, loginUser));
	}
}
