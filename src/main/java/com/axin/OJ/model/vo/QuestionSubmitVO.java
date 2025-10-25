package com.axin.OJ.model.vo;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.model.entity.JudgeInfo;
import com.axin.OJ.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 题目
 */
@Data
public class QuestionSubmitVO {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 编程语言
	 */
	private String language;

	/**
	 * 用户代码
	 */
	private String code;

	/**
	 * 判题信息（json 对象）
	 */
	private JudgeInfo judgeInfo;

	/**
	 * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
	 */
	private Integer status;

	/**
	 * 题目 id
	 */
	private Long questionId;

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
	 * @param questionSubmitVO 题目包装类
	 * @return 题目
	 */
	public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
		if (questionSubmitVO == null) {
			return null;
		}
		QuestionSubmit questionSubmit = new QuestionSubmit();
		BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
		JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
		if (judgeInfo != null) {
			questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
		}
		return questionSubmit;
	}

	/**
	 * 对象转包装类
	 *
	 * @param questionSubmit 题目
	 * @return 题目包装类
	 */
	public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
		if (questionSubmit == null) {
			return null;
		}
		QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
		BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
		if (questionSubmit.getJudgeInfo() != null) {
			questionSubmitVO.setJudgeInfo(JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class));
		}
		return questionSubmitVO;
	}


}