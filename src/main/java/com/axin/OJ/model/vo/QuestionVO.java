package com.axin.OJ.model.vo;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.model.entity.JudgeConfig;
import com.axin.OJ.model.entity.Question;

import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 题目
 */
@Data
public class QuestionVO {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 标签列表（json 数组）
	 */
	private List<String> tags;

	/**
	 * 题目提交数
	 */
	private Integer submitNum;

	/**
	 * 题目通过数
	 */
	private Integer acceptedNum;


	/**
	 * 判题配置（json 对象）
	 */
	private JudgeConfig judgeConfig;

	/**
	 * 点赞数
	 */
	private Integer thumbNum;

	/**
	 * 收藏数
	 */
	private Integer favourNum;

	/**
	 * 创建用户 id
	 */
	private Long userId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 创建人信息
	 */
	private UserVO user;

	/**
	 * 包装类转对象
	 *
	 * @param questionVO 题目包装类
	 * @return 题目
	 */
	public static Question voToObj(QuestionVO questionVO) {
		if (questionVO == null) {
			return null;
		}
		Question question = new Question();
		BeanUtils.copyProperties(questionVO, question);
		List<String> tagList = questionVO.getTags();
		if (tagList != null) {
			question.setTags(JSONUtil.toJsonStr(tagList));
		}
		if (questionVO.getJudgeConfig() != null) {
			question.setJudgeConfig(JSONUtil.toJsonStr(questionVO.getJudgeConfig()));
		}
		return question;
	}

	/**
	 * 对象转包装类
	 *
	 * @param question 题目
	 * @return 题目包装类
	 */
	public static QuestionVO objToVo(Question question) {
		if (question == null) {
			return null;
		}
		QuestionVO questionVO = new QuestionVO();
		BeanUtils.copyProperties(question, questionVO);
		if (question.getJudgeConfig() != null) {
			questionVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));
		}
		if (question.getTags() != null) {
			questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
		}
		return questionVO;
	}

}
