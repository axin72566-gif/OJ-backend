package com.axin.OJ.judge.codesandbox;

/**
 * 代码沙箱静态工厂
 */
public class CodeSandboxFactory {

	/**
	 * 创建代码沙箱
	 * @param type 沙箱类型
	 * @return 代码沙箱实例
	 */
	public static CodeSandbox newInstance(String type) {
		switch (type) {
			case "remote":
				return new RemoteCodeSandbox();
			case "third_party":
				return new ThirdPartyCodeSandbox();
			default:
				return new ExampleCodeSandbox();
		}
	}
}
