package com.axin.OJ.judge.codesandbox;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.axin.OJ.common.ErrorCode;
import com.axin.OJ.exception.BusinessException;
import com.axin.OJ.judge.model.ExecuteCodeRequest;
import com.axin.OJ.judge.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

	//内部调用时，用来鉴权的密钥
	private static final String HEADER_API_KEY = "request-key";

	private static final String SECRET_KEY = "secretKey";

	/**
	 * 执行代码
	 * @param request 执行代码请求
	 * @return 执行代码响应
	 */
	@Override
	public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
		System.out.println("远程代码沙箱");
		String url = "http://localhost:8090/code/sandbox/execute";
		String json = JSONUtil.toJsonStr(request);
		String responseStr = HttpUtil.createPost(url)
				.header(HEADER_API_KEY,SECRET_KEY)
				.body(json)
				.execute()
				.body();
		if (StringUtils.isBlank(responseStr)) {
			throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error,message = " + responseStr);
		}
		return JSONUtil.toBean(responseStr,ExecuteCodeResponse.class);

	}
}
