package com.feng.aopframe.interceptor;


import com.feng.aopframe.advice.BeforeAdvice;
import com.feng.aopframe.advice.MethodInterceptor;

public class BeforeAdviceInterceptor implements MethodInterceptor {

	private BeforeAdvice beforeAdvice;
	
	public BeforeAdviceInterceptor(BeforeAdvice beforeAdvice) {
		this.beforeAdvice = beforeAdvice;
	}
	
	@Override
	//这个invoike方法的拦截器的回调方法，会在代理对象的方法被调用的时候被回调
	public Object invoke(MethodInvocation invocation) throws Throwable {
		this.beforeAdvice.beforeMethod(invocation.getThis(), invocation.getMethod(),invocation.getArgs());
		return invocation.proceed();
	}

}
