package com.axin.OJ.judge.codesandbox;

import com.axin.OJ.judge.model.ExecuteCodeRequest;
import com.axin.OJ.judge.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
	/**
	 * 执行代码
	 * @param request 执行代码请求
	 * @return 执行代码响应
	 */
	@Override
	public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
		return null;
	}
}
