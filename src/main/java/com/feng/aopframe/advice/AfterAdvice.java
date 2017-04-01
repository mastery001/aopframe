package com.feng.aopframe.advice;

import java.lang.reflect.Method;


public interface AfterAdvice {

	/**
	 *
	 * @Title: afterMethod
	 *
	 * @Description: 代理对象中执行的方法之后的操作
	 *
	 * @param @param target 目标操作类
	 * @param @param method 执行的方法
	 * @param @param args 传递的参数
	 *
	 * @return void 返回类型
	 *
	 * @throws
	 */
	void afterMethod(Object retVal, Object target, Method method, Object args[])
			throws Throwable;

}
