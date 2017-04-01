package com.feng.aopframe;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.feng.aopframe.advice.MethodInterceptor;
import com.feng.aopframe.interceptor.MethodInvocation;

public class ProxyFactoryBean {
	private Object target;
	private MethodInterceptor methodInterceptor;

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public MethodInterceptor getMethodInterceptor() {
		return methodInterceptor;
	}

	public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
		this.methodInterceptor = methodInterceptor;
	}

	public Object getProxy(final Object target,
			final MethodInterceptor methodInterceptor) {
							Object proxy = null;
		proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(), new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						MethodInvocation invocation = new MethodInvocation();
						invocation.setThis(target);
						invocation.setMethod(method);
						invocation.setArgs(args);
						Object retVal = methodInterceptor.invoke(invocation);
						return retVal;
					}
				});

		return proxy;
	}

}
