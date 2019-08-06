package com.dt.rts.eregusa.audit.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "eregusaaudit")
public class InterceptorConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);
	
	private String sqsQueue;
	private boolean jdbcAudit;
	
	private List<AuditableEntity> entities = new ArrayList<>();
	
	public InterceptorConfig() {
		logger.info("InterceptorConfig ctor ");
	}
	
	@PostConstruct
	public void PostConstruct(){
	     // init code goes here
		logger.info("InterceptorConfig PostConstruct sqsQueue=" + sqsQueue);
	}
	
	public String getSqsQueue() {
		return sqsQueue;
	}


	public void setSqsQueue(String sqsQueue) {
		this.sqsQueue = sqsQueue;
	}


	public List<AuditableEntity> getEntities() {
		return entities;
	}


	public void setEntities(List<AuditableEntity> entities) {
		this.entities = entities;
	}
	
	public boolean isQueueConfigured() {
		return (sqsQueue != null && sqsQueue.trim().length() > 0);
	}
	
	public boolean isAuditableEntity() {
		return (entities != null && entities.size()> 0);
	}
	
	public boolean isJdbcAudit() {
		return jdbcAudit;
	}

	public void setJdbcAudit(boolean jdbcAudit) {
		this.jdbcAudit = jdbcAudit;
	}
	
	@Override
	public String toString() {
		return "InterceptorConfig [sqsQueue=" + sqsQueue + ", entities size=" + entities.size() + "]";
	}


}
