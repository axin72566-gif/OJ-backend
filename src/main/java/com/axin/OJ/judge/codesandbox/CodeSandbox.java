package com.axin.OJ.judge.codesandbox;


import com.axin.OJ.judge.model.ExecuteCodeRequest;
import com.axin.OJ.judge.model.ExecuteCodeResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * 代码沙箱接口
 */
public interface CodeSandbox {

	/**
	 * 执行代码
	 * @param request 执行代码请求
	 * @return 执行代码响应
	 */
	ExecuteCodeResponse executeCode(ExecuteCodeRequest request);
}
