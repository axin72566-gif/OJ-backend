package com.axin.OJ.judge;


import com.axin.OJ.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public interface JudgeService {

	/**
	 * 判题
	 *
	 * @param questionSubmitId 用户提交记录id
	 */
	QuestionSubmit doJudge(Long questionSubmitId);
}
