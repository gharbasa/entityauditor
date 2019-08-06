package com.dt.rts.eregusa.audit.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dt.rts.eregusa.audit.config.AuditableEntity;
import com.dt.rts.eregusa.audit.config.EntityAuditService;
import com.dt.rts.eregusa.audit.config.InterceptorConfig;
import com.dt.rts.eregusa.audit.config.SpringContextUtil;
import com.dt.rts.eregusa.audit.entity.EntityAudit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ERegEntityAuditor extends EmptyInterceptor {
	
	Logger logger = LoggerFactory.getLogger(ERegEntityAuditor.class);
	
    
	public boolean onSave(Object entity,Serializable id,
			Object[] state,String[] propertyNames,Type[] types)
			throws CallbackException {
		//Inserts
		return super.onSave(entity, id, state, propertyNames, types);
		
	}
	
	public void onDelete(Object entity, Serializable id, 
			Object[] state, String[] propertyNames, 
			Type[] types) {
			//System.out.println("onDelete");
		super.onDelete(entity, id, state, propertyNames, types);
	}
	
    public boolean onFlushDirty(Object entity,Serializable id,
    		Object[] currentState,Object[] previousState,
    		String[] propertyNames,Type[] types)
    		throws CallbackException {
    	
    	//Updates
    	InterceptorConfig interceptorConfig = SpringContextUtil.getApplicationContext().getBean(InterceptorConfig.class);
    	EntityAuditService myService = SpringContextUtil.getApplicationContext().getBean(EntityAuditService.class);
    	
    	if(!interceptorConfig.isJdbcAudit() && !interceptorConfig.isQueueConfigured()) {
    		logger.info("ERegHibernateInterceptor neither JDBC is enabled nor Queue is configured... returning.");
    		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types); 
    	}
    	
    	logger.info("ERegHibernateInterceptor onFlushDirty11");
    	logger.info("ERegHibernateInterceptor entity.name11=" + entity.getClass().getName() + ", id=" + id); //class com.dt.rts.eregusa.cpa.entity.User,
    	logger.info("ERegHibernateInterceptor onFlushDirty11 interceptorConfig.getSqsQueue=" + interceptorConfig.getSqsQueue());
    	
    	myService.print();
    	AuditableEntity auditableEntity = entityAuditable(entity.getClass().getName(), interceptorConfig); 
    	if(auditableEntity == null) {
    		logger.info(entity.getClass().getName() + " is not configured to audit"); //class com.dt.rts.eregusa.cpa.entity.User,
    		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    	} else {
    		logger.info("ERegHibernateInterceptor auditing entity.name=" + entity.getClass().getName()); //class com.dt.rts.eregusa.cpa.entity.User,
    	}
    	
    	EntityAudit entityAudit = new EntityAudit();
    	entityAudit.setOperation("UPDATE");
    	entityAudit.setEntity(auditableEntity.getEntity());
    	entityAudit.setRowId(id.toString());
    	entityAudit.setCreatedBy(SpringContextUtil.getLoginUser());
    	
    	ObjectMapper mapper = new ObjectMapper();
    	ObjectNode oldNode = mapper.createObjectNode(); 
    	ObjectNode newNode = mapper.createObjectNode();
    	boolean isThereaChange = false;
    	for(int i=0; i< propertyNames.length; i++) {
    		String property = propertyNames[i];
    		logger.info("ERegHibernateInterceptor property typeName=" + types[i].getName() + ", returnedClass=" + types[i].getReturnedClass().getName() + ", type.isEntityType()=" + types[i].isEntityType());
    		if(auditableEntity.isAttributeAuditable(property)) {
    			logger.info(property + " is set to auditable");
    			String oldValue = "";
				String newValue = "";
    			if(!types[i].isEntityType()) {
    				if(!"json".equals(types[i].getName())) {
    					logger.info(property + " is NOT Json type");
    					oldValue = (previousState[i] != null)?previousState[i].toString(): "";
    	   				newValue = (currentState[i] != null)?currentState[i].toString(): "";
    				} else {
    					logger.info(property + " is a json Type");
   						try {
   							ObjectMapper oldValuemapper = new ObjectMapper();
   							oldValue = oldValuemapper.writeValueAsString(previousState[i]);
   						}
   						catch(Exception e) {
   							logger.error("ERegHibernateInterceptor Json writeValueAsString error oldValue " + e.getMessage());
   							oldValue = previousState[i].toString();
   						}
   						
   						try {
   							ObjectMapper newValuemapper = new ObjectMapper();
   							newValue = newValuemapper.writeValueAsString(currentState[i]);
   						}
   						catch(Exception e) {
   							logger.error("ERegHibernateInterceptor Json writeValueAsString error newValue " + e.getMessage());
   							newValue = currentState[i].toString();
   						}
   					}
    			} else {
    				//If @Type(type = "json") exists on the attribute, then the types[i].getName() returns 'json'
    				if("json".equals(types[i].getName())) {
    					
    					
    				}
    				Object oldValueEntity = previousState[i];
    				Object newValueEntity = currentState[i];
    				logger.info(property + " is an entity, its instance class=" + oldValueEntity.getClass());
    				if(oldValueEntity != null) {
    					logger.debug(property + " reading old value");
    					oldValue = getFieldValue(oldValueEntity,oldValueEntity.getClass(), "getId");
    				}
    				
    				if(newValueEntity != null) {
    					logger.debug(property + " reading new value");
    					newValue = getFieldValue(newValueEntity,oldValueEntity.getClass(), "getId");
    				}
    			}
    			logger.info(property + " old value=" + oldValue);
    			logger.info(property + " new value=" + newValue);
    			if (!oldValue.equals(newValue)) {
    				oldNode.put(property, oldValue);
    				newNode.put(property, newValue);
    				isThereaChange = true;
    			}
    		} else {
    			logger.debug(property + " is NOT set to auditable");
    		}
    	}
    	
    	if(isThereaChange) {
	    	try {
	    		String oldValues = mapper.writeValueAsString(oldNode);
	    		entityAudit.setOldValues(oldValues);
	    	}catch(Exception e) {
	    		logger.error("There is a change to writeValueAsString of oldValues");
	    	}
	    	
	    	try {
	    		String newValues = mapper.writeValueAsString(newNode);
	    		entityAudit.setNewValues(newValues);
	    	}catch(Exception e) {
	    		logger.error("There is a change to writeValueAsString of newValues");
	    	}
	    	
	    	myService.save(entityAudit);
    	} else {
    		logger.info("NO changes have been found on the entity " + entity.getClass().getName() + ", id=" + id);
    	}
    	
    	/**
    	for(Type type:types) {
    		logger.info("ERegHibernateInterceptor property typeName=" + type.getName() + ", returnedClass=" + type.getReturnedClass().getName() + ", type.isEntityType()=" + type.isEntityType());
    	}
    	*/
    	/**
    	
    	for(Object state:currentState) {
    		if(state != null)
    			logger.debug("ERegHibernateInterceptor property currentState=" + state.toString());
    		else
    			logger.debug("ERegHibernateInterceptor property currentState=null");
    	}
    	for(Object state:previousState) {
    		if(state != null)
    			logger.debug("ERegHibernateInterceptor property previousState=" + state.toString());
    		else
    			logger.debug("ERegHibernateInterceptor property previousState=null");
    	}
    	*/
    	
    	/**
    	if (entity instanceof User) {
            logger.info("ERegHibernateInterceptor its an user entity " + ((User) entity).toString());
        }
        */
    	
    	return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    		
    }
    
    //called after committed into database
  	public void postFlush(Iterator iterator) {
  		logger.info("ERegHibernateInterceptor postFlush");
  	}
  	
  	public AuditableEntity entityAuditable(String entity, InterceptorConfig interceptorConfig) {
		for(AuditableEntity auditableEntity: interceptorConfig.getEntities()) {
			if(entity.endsWith("." + auditableEntity.getEntity()))
			{
				return auditableEntity;
			}
		}
		return null;
	}
  	
  	/**
  	public String getFieldValue(Object entity, String fieldName) {
  		String value = "";
  		try {
  			Field field = ReflectionUtils.findField(entity.getClass(), fieldName);
			if(field == null) {
				logger.error("ERegHibernateInterceptor entity.getClass().getDeclaredField(\"" + fieldName + "\") returned null, field doesn't exists");
			} else {
				//field.setAccessible(true);
				ReflectionUtils.makeAccessible(field);
				logger.info("ERegHibernateInterceptor field.getName()=" + field.getName() + ", getType=" + field.getType());
				Object valueObj = ReflectionUtils.getField(field, entity);
				logger.info("ERegHibernateInterceptor valueObj=" + valueObj);
				return valueObj.toString();
				//value = field.get(entity).toString();
			}
		}
		catch(Exception e) {
			logger.error("ERegHibernateInterceptor.getFieldValue(\"" + fieldName + "\") is throwing error=" + e.getMessage());
			e.printStackTrace();
		}
  		return value;
  	}
  	*/
  	
  	public String getFieldValue(Object object, Class objClass, String methodName) {
		String value = "";
		try {
			 //Field field = objClass.getDeclaredField(fieldName);
			 //Field field = ReflectionUtils.findField(client.getClass(), "id");
			Method getNameMethod = objClass.getMethod(methodName);
			// if(field == null) {
			if(getNameMethod == null) {
				 logger.warn("ERegHibernateInterceptor entity.getClass().getDeclaredField(\"" + getNameMethod + "\") returned null, field doesn't exists");
			 } else {
				 value = getNameMethod.invoke(object).toString(); // explicit cast
				 logger.debug("ERegHibernateInterceptor.getFieldValue(\"" + methodName + "\") value=" + value);
				 //field.setAccessible(true);
				 //ReflectionUtils.makeAccessible(field);
				 //String value = ReflectionUtils.getField(field, client).toString();
				 //value = field.get(object).toString();
				 //value = field.get(entity).toString();
			}
		}
		catch(NoSuchMethodException e) {
			logger.error("ERegHibernateInterceptor.getFieldValue(\"" + methodName + "\") is throwing error=" + e.getMessage());
			//e.printStackTrace();
			Class superClass = objClass.getSuperclass();
			if(superClass != null) {
				logger.error("ERegHibernateInterceptor.getFieldValue(\"" + methodName + "\") trying super class=" + superClass.getName());
				value = getFieldValue(object, superClass, methodName);
			}
		}
		catch(Exception e) {
			logger.error("ERegHibernateInterceptor.getFieldValue(\"" + methodName + "\") is throwing error=" + e.getMessage());
			//e.printStackTrace();
		}
		return value; 
	}
	
}
