package com.feng.aopframe.advice;


public interface ThrowsAdvice {

	/**
	 *
	 * @Title: afterThrowing
	 *
	 * @Description: 处理目标方法抛出的异常
	 *
	 * @param @param e
	 *
	 * @return void 返回类型
	 *
	 * @throws
	 */

	void afterThrowing(Throwable e);

	void afterThrowing(Method method, Object[] args, Object target,
			Throwable throwable);

}
