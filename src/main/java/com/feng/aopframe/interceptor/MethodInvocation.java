package com.feng.aopframe.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.feng.aopframe.BeanFactory;
import com.feng.aopframe.advice.AfterAdvice;
import com.feng.aopframe.advice.BeforeAdvice;
import com.feng.aopframe.advice.MethodInterceptor;

public class MethodInvocation {
	private Object retVal = null;
	private Object target;
	private Method method;
	private Object[] args;
	private static int count = 1;

	public MethodInvocation() {

	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getThis() {
		return target;
	}

	public void setThis(Object target) {
		this.target = target;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getRetVal() {
		return retVal;
	}

	public void setRetVal(Object retVal) {
		this.retVal = retVal;
	}


	/**

	 * @Title: proceed
	 * @Description: proceed方法为实现MethodInterceptor接口的实现类一定要调用的方法，此方法
	 *               为此小型框架的核心，通过调用实现了 BeforeAdvice接口里的方法，而后调用传递对象的invoke方法
	 *               获得返回值，之后调用实现了AfterAdvice接口里的方法，从而完成aop的编程
	 * @param @return
	 * @param @throws Throwable
	 * @return Object    返回类型
	 * @throws

	 */
	public Object proceed() throws Throwable {
		MethodInterceptor interceptor = null;
		if (retVal == null) {
			interceptor = new BeforeAdviceInterceptor(
					(BeforeAdvice) BeanFactory.beanMap.get("beforeAdvice"));
			try {
				this.retVal = this.getMethod().invoke(this.getThis(), getArgs());
				interceptor.invoke(this);
			} catch (Throwable e) {
				invokeException(BeanFactory.beanMap.get("throwsAdvice"), e);
			}

		}
		if(retVal != null && count == 1){
			interceptor = new AfterAdviceInterceptor(
					(AfterAdvice) BeanFactory.beanMap.get("afterAdvice"));
			try {
				count++;
				interceptor.invoke(this);
			} catch (Throwable e) {
				invokeException(BeanFactory.beanMap.get("throwsAdvice"), e);
			}
		}
		return retVal;
	}

	private Map<String, Method> map = new HashMap<String, Method>();

	/***

	 * @Title: getExceptionMethod

	 * @Description: 这个方法是用于获取异常bean中的方法，当方法中的参数有是Throwable对象的
	 * 				子类时即将此方法名和方法放入到Map集合中
	 *               此方法第一次做时不完美，这是不完美版

	 * @param @param exceptionBean

	 * @return void    返回类型

	 * @throws
	 */
	/*@SuppressWarnings("rawtypes")
	private void getExceptionMethod(Object exceptionBean) {
		Method[] methods = exceptionBean.getClass().getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.indexOf("afterThrowing") == 0) {
				Class[] clazzParams = method.getParameterTypes();
				for (Class clazzParam : clazzParams) {
					String clazzParamName = clazzParam.getName();
					try {
						if ((clazzParam.newInstance()) instanceof Throwable) { //
							System.out.println(clazzParamName);
							map.put(clazzParamName, method);
						}
					} catch (InstantiationException e) {
					} catch (IllegalAccessException e) {
					}
					// System.out.println();
				}
			}
		}
	}*/


	/***

	 * @Title: getExceptionMethod

	 * @Description: 这个方法是用于获取异常bean中的方法，当方法中的参数有是Throwable对象的
	 * 				子类时即将此方法名和方法放入到Map集合中

	 * @param @param exceptionBean

	 * @return void    返回类型

	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	private void getExceptionMethod(Object exceptionBean) {
		Method[] methods = exceptionBean.getClass().getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.indexOf("afterThrowing") == 0) {
				Class[] clazzParams = method.getParameterTypes();
				for (Class clazzParam : clazzParams) {
					String clazzParamName = clazzParam.getName();
					if (getExceptionSuperClass(clazzParam)) {
						map.put(clazzParamName, method);
					}
				}
			}
		}
	}


	/**

	 * @Title: invokeException
	 * @Description: 此方法是用于调用异常的方法，通过与传递过来的异常对象进行匹配，当存在相同的
	 *               异常对象时调用对应的方法，用户可能写了两种参数的方法（异常对象相同时），
	 *               一种是4个参数的方法，一种是1个参数的方法，此方法调用时会自动调用4个参数的方法
	 *
	 * @param @param exceptionBean	异常对象bean，实现了ThrowsAdvice的接口
	 * @param @param e               异常对象

	 * @return void    返回类型

	 * @throws

	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void invokeException(Object exceptionBean, Throwable e) throws Throwable{
		//调用此方法用于得到Map里面的内容
		getExceptionMethod(exceptionBean);
		String exceptionName = e.getClass().getName();
		//用Set集合来迭代Map集合
		Set<Map.Entry<String, Method>> set = map.entrySet();
		for (Iterator<Entry<String, Method>> it = (Iterator) set.iterator(); it
				.hasNext();) {
			Map.Entry<String, Method> m = (Entry<String, Method>) it.next();
			//当传递来的异常对象名字与Map中的key对应时则调用相应的方法
			if (m.getKey().equalsIgnoreCase(exceptionName)) {
				Method method = m.getValue();
				int paramLength = method.getParameterTypes().length;
				//System.out.println("执行" + paramLength + "个参数的方法");
				if (paramLength == 1) {
					method.invoke(exceptionBean, e);
				}
				if (paramLength == 4) {
					method.invoke(exceptionBean, method, args, target, e);
				}
			}
		}
	}


	/**
	 * @Title: getExceptionSuperClass
	 * @Description: 此方法是为了获取传递过来的类字节码对应的父类，当为异常类的实现类时则返回true。
	 * @param @param exceptionClass
	 * @param @return
	 * @return boolean    返回类型
	 * @throws

	 */
	@SuppressWarnings("rawtypes")
	private boolean getExceptionSuperClass(Class exceptionClass) {
		if (exceptionClass.getName().equalsIgnoreCase("java.lang.Object")) {
			return false;
		}
		if (exceptionClass.getName().equalsIgnoreCase("java.lang.Throwable")) {
			return true;
		}

		return getExceptionSuperClass(exceptionClass.getSuperclass());
	}
}
