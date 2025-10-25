package com.axin.OJ.judge.codesandbox;

import com.axin.OJ.judge.model.ExecuteCodeRequest;
import com.axin.OJ.judge.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

	/**
	 * 代码沙箱接口，用来接受传进来的代码沙箱实例
	 */
	private final CodeSandbox codeSandbox;

	/**
	 * 构造方法，用来接受传进来的代码沙箱实例
	 * @param codeSandbox 代码沙箱实例
	 */
	public CodeSandboxProxy(CodeSandbox codeSandbox) {
		this.codeSandbox = codeSandbox;
	}

	/**
	 * 执行代码
	 * @param request 执行代码请求
	 * @return 执行代码响应
	 */
	@Override
	public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
		log.info("代码沙箱请求开始执行:{}", request);
		ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(request);
		log.info("代码沙箱响应结果");
//		log.info("代码沙箱执行完成:{}", executeCodeResponse);
		return executeCodeResponse;
	}
}
