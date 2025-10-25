package com.axin.OJ.service;

import com.axin.OJ.model.dto.question.QuestionQueryRequest;
import com.axin.OJ.model.entity.Question;
import com.axin.OJ.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author kdkt1
* &#064;description 针对表【question(题目)】的数据库操作Service
* &#064;createDate 2025-10-12 19:12:33
*/
public interface QuestionService extends IService<Question> {

	/**
	 * 创建的题目是否合法
	 *
	 * @param question 题目
	 * @param add      是否为创建操作
	 */
	void validQuestion(Question question, boolean add);

	/**
	 * 获取查询条件
	 *
	 * @param questionQueryRequest 查询条件
	 * @return 查询条件
	 */
	QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

	/**
	 * 获取题目封装
	 *
	 * @param question 题目
	 * @param request 请求
	 * @return 题目 vo
	 */
	QuestionVO getQuestionVO(Question question, HttpServletRequest request);

	/**
	 * 分页获取题目封装
	 *
	 * @param questionPage 分页
	 * @param request 请求
	 * @return 分页
	 */
	Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
