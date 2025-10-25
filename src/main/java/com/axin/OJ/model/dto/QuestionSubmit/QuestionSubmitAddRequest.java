package com.axin.OJ.model.dto.QuestionSubmit;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * 帖子创建请求
 */
@Data
public class QuestionSubmitAddRequest {

	/**
	 * 编程语言
	 */
	private String language;

	/**
	 * 用户代码
	 */
	private String code;

	/**
	 * 题目 id
	 */
	private Long questionId;

}
