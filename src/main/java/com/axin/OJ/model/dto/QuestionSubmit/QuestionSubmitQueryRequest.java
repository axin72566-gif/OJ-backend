package com.axin.OJ.model.dto.QuestionSubmit;


import com.axin.OJ.common.PageRequest;
import com.axin.OJ.model.entity.JudgeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 帖子查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 编程语言
	 */
	private String language;

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

}
