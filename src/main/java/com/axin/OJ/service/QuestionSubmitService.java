package com.axin.OJ.service;

import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.axin.OJ.model.entity.QuestionSubmit;
import com.axin.OJ.model.entity.User;
import com.axin.OJ.model.vo.QuestionSubmitVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author kdkt1
* &#064;description  针对表【question_submit(题目提交)】的数据库操作Service
* &#064;createDate 2025-10-12 19:16:35
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

	/**
	 * 提交题目
	 * @param questionSubmitAddRequest 提交题目请求
	 * @param loginUser 当前登录用户
	 * @return 题目提交 ID
	 */
	Long addQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

	/**
	 * 获取我题目提交列表
	 * @param questionSubmitQueryRequest 查询题目提交请求
	 * @param loginUser 当前登录用户
	 * @return 题目提交列表
	 */
	Page<QuestionSubmitVO> listQuestionSubmitByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser);
}
