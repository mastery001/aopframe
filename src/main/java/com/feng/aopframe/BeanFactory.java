package com.feng.aopframe;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.feng.aopframe.advice.MethodInterceptor;

public class BeanFactory {
	private Properties properties = new Properties();
	public static Map<String,Object> beanMap = new HashMap<String,Object>();

	public BeanFactory(InputStream is) {
		try {
			properties.load(is);
			is.close();
		} catch(IOException e ) {
			System.out.println("文件读取错误：" + e);
		}
	}
	
	public Object getBean(String beanName) {
		String className = properties.getProperty(beanName);
		beanMap = getPropertiesBean(beanName, properties);
		Object bean = null;
		try {
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(className);
			bean = clazz.newInstance();
		} catch(ClassNotFoundException e ) {
			System.out.println("尚未找到此类：" + e);
		} catch(LinkageError e1) {
			System.out.println("错误：" + e1);
		} catch(InstantiationException e2) {
			System.out.println("类初始化失败：" + e2);
		} catch (IllegalAccessException e) {
			System.out.println("非法进入操作：" + e);
		}
		if(bean instanceof ProxyFactoryBean) {
			Object proxy = null;
			ProxyFactoryBean proxyBean = (ProxyFactoryBean)bean;
			try {
				Object target = beanMap.get("target");
				MethodInterceptor methodInterceptor = (MethodInterceptor)beanMap.get("methodInterceptor");
				proxyBean.setTarget(target);
				proxyBean.setMethodInterceptor(methodInterceptor);
				proxy = proxyBean.getProxy(target, methodInterceptor);
			} catch (Exception e) {
				System.out.println("代理类出错： " + e);
			}
			return proxy;
		}
		return bean;
	}
	
	
	private static Map<String,Object> getPropertiesBean(String beanName,Properties properties) {
		Map<String,Object> beanMap = new HashMap<String,Object>();
		beanMap.put("target", getProperties("target",beanName,properties));
		beanMap.put("beforeAdvice", getProperties("beforeAdvice",beanName,properties));
		beanMap.put("afterAdvice", getProperties("afterAdvice",beanName,properties));
		beanMap.put("methodInterceptor", getProperties("methodInterceptor",beanName,properties));
		beanMap.put("throwsAdvice", getProperties("throwsAdvice",beanName,properties));
		return beanMap;
	}
	
	private static Object getProperties(String name,String beanName,Properties properties) {
		Object bean = null;
		try {
			bean = Class.forName(properties.getProperty(beanName + "." + name)).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}
}
