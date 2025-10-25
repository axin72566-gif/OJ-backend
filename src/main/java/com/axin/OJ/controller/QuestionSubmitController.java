package com.axin.OJ.controller;

import com.axin.OJ.common.BaseResponse;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.common.ResultUtils;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.axin.OJ.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.axin.OJ.model.entity.User;
import com.axin.OJ.model.vo.QuestionSubmitVO;
import com.axin.OJ.service.QuestionSubmitService;
import com.axin.OJ.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/question/question_submit")
@Slf4j
public class QuestionSubmitController {



}
