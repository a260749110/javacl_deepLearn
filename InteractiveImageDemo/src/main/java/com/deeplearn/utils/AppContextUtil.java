package com.deeplearn.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.deeplearn.service.DeeplearnService;

public class AppContextUtil {
	private static final AppContextUtil instance = new AppContextUtil();
	private static ApplicationContext context;

	private AppContextUtil() {

		context = (new ClassPathXmlApplicationContext("spring.xml"));

	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static DeeplearnService getDeeplearnService() {
		return context.getBean(DeeplearnService.class);
	}

}
