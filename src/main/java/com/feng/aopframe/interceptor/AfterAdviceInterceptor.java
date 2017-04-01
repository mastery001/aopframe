package com.feng.aopframe.interceptor;

import com.feng.aopframe.advice.AfterAdvice;
import com.feng.aopframe.advice.MethodInterceptor;

public class AfterAdviceInterceptor implements MethodInterceptor{

	private AfterAdvice afterAdvice;
	
	public AfterAdviceInterceptor(AfterAdvice afterAdvice) {
		this.afterAdvice = afterAdvice;
	}
	
	@Override
	//这个invoike方法的拦截器的回调方法，会在代理对象的方法被调用的时候被回调
	public Object invoke(MethodInvocation invocation) throws Throwable {
		this.afterAdvice.afterMethod(invocation.getRetVal(),invocation.getThis(), invocation.getMethod(),invocation.getArgs());
		return invocation.proceed();
	}
	
	
}
