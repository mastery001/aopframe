package com.feng.aopframe.advice;

import com.feng.aopframe.interceptor.MethodInvocation;


public interface MethodInterceptor {
	public Object invoke(MethodInvocation invocation) throws Throwable;
}
