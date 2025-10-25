package com.axin.OJ.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.constant.CommonConstant;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.exception.ThrowUtils;
import com.axin.OJ.model.dto.question.QuestionQueryRequest;
import com.axin.OJ.model.entity.*;
import com.axin.OJ.model.vo.QuestionVO;
import com.axin.OJ.model.vo.UserVO;
import com.axin.OJ.service.UserService;
import com.axin.OJ.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.axin.OJ.service.QuestionService;
import com.axin.OJ.mapper.QuestionMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author kdkt1
* &#064;description  针对表【question(题目)】的数据库操作Service实现
* &#064;createDate 2025-10-12 19:12:33
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

	@Resource
	private UserService userService;

	/**
	 * 校验创建的题目是否合法，这个方法应该是你准备往数据库中写数据时会调用
	 *
	 * @param question 题目
	 * @param add      是否为创建操作
	 */
	@Override
	public void validQuestion(Question question, boolean add) {
		// 判空
		if (question == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
		}
		// 创建时，参数不能为空
		if (add) {
			ThrowUtils.throwIf(StringUtils.isAnyBlank(question.getTitle(), question.getContent(), question.getTags()), ErrorCode.PARAMS_ERROR);
		}
		if (StringUtils.isNotBlank(question.getTitle()) && question.getTitle().length() > 80) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
		}
		if (StringUtils.isNotBlank(question.getContent()) && question.getContent().length() > 8192) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
		}
		if (StringUtils.isNotBlank(question.getTags()) && question.getTags().length() > 1024) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签过长");
		}
		if (StringUtils.isNotBlank(question.getAnswer()) && question.getAnswer().length() > 8192) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
		}
		if (StringUtils.isNotBlank(question.getJudgeCase()) && question.getJudgeCase().length() > 1024) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
		}
		if (StringUtils.isNotBlank(question.getJudgeConfig()) && question.getJudgeConfig().length() > 1024) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
		}
	}

	/**
	 * 获取查询包装类，前端传过来查询参数，方法将他封装到QueryWrapper中，用于数据库查询
	 *
	 * @param questionQueryRequest 查询条件
	 * @return 查询包装类
	 */
	@Override
	public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
		QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
		// 如果查询参数为空，直接返回空查询条件
		if (questionQueryRequest == null) {
			return queryWrapper;
		}
		Long id = questionQueryRequest.getId();
		String title = questionQueryRequest.getTitle();
		String content = questionQueryRequest.getContent();
		List<String> tags = questionQueryRequest.getTags();
		String sortField = questionQueryRequest.getSortField();
		String sortOrder = questionQueryRequest.getSortOrder();
		if (ObjectUtils.isNotEmpty(id)) {
			queryWrapper.eq("id", id);
		}
		if (StringUtils.isNotBlank(title)) {
			queryWrapper.like("title", title);
		}
		if (StringUtils.isNotBlank(content)) {
			queryWrapper.like("content", content);
		}
		if (CollUtil.isNotEmpty(tags)) {
			for (String tag : tags) {
				queryWrapper.like("tags", "\"" + tag + "\"");
			}
		}
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}

	/**
	 * 获取题目封装类，包含题目信息、用户信息、点赞、收藏状态
	 * @param question 题目实体类
	 * @param request  HTTP 请求对象，用于获取登录用户信息
	 * @return 包含题目信息、用户信息、点赞、收藏状态的题目视图对象
	 */
	@Override
	public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
		QuestionVO questionVO = QuestionVO.objToVo(question);
		// 1. 关联查询用户信息
		Long userId = question.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user);
		questionVO.setUser(userVO);
		return questionVO;
	}

	/**
	 * 分页查询，获取题目的封装类
	 * @param questionPage 待分页的题目列表
	 * @param request      HTTP 请求对象，用于获取登录用户信息
	 * @return 分页包含题目信息、用户信息、点赞、收藏状态的题目视图对象
	 */
	@Override
	public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
		// 获取从数据库分页查出来的题目列表
		List<Question> questionList = questionPage.getRecords();
		// 创建一个盒子，把上面的参数处理之后放到这个盒子里
		Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
		if (CollUtil.isEmpty(questionList)) {
			return questionVOPage;
		}
		// 1. 关联查询用户信息
		Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
		// 防止查出来重复的用户
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		List<QuestionVO> questionVOList = questionList.stream().map(question -> {
			QuestionVO questionVO = QuestionVO.objToVo(question);
			Long userId = question.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				// 一般集合中只有一个用户，即使有多个也都是一样的
				user = userIdUserListMap.get(userId).get(0);
			}
			questionVO.setUser(userService.getUserVO(user));
			return questionVO;
		}).collect(Collectors.toList());
		questionVOPage.setRecords(questionVOList);
		return questionVOPage;
	}

}




