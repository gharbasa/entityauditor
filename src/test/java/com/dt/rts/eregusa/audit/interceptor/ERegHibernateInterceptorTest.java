package com.dt.rts.eregusa.audit.interceptor;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class ERegHibernateInterceptorTest {

	Logger logger = LoggerFactory.getLogger(ERegHibernateInterceptorTest.class);
	
	ERegHibernateInterceptor testObj = new ERegHibernateInterceptor();
	
	@Test
	public void entityAuditable() {
		String className = "com.dt.rts.eregusa.cpa.entity.User";
		String auditingClass = "User";
		Assert.assertTrue(className.endsWith("." + auditingClass));
		
		Child2 child2 = new Child2();
		child2.setId(100L);
		String fieldName = "id";
		String value = testObj.getFieldValue(child2, child2.getClass(), "getId");
		System.out.print("value=" + value);
	}
}
