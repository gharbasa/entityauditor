package com.dt.rts.eregusa.audit.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

   private static ApplicationContext applicationContext;

   @Override
   public void setApplicationContext(ApplicationContext applicationContext) 
   throws BeansException {
      SpringContextUtil.applicationContext = applicationContext;
   }

   public static ApplicationContext getApplicationContext() {
      return applicationContext;
   }
   
   public static String getLoginUser() {
	   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       String createdBy = auth.getName();
       return createdBy;
   }
}
