package com.dt.rts.eregusa.audit.config;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dt.rts.eregusa.audit.entity.EntityAudit;

@Service(value = "EntityAuditService")
public class EntityAuditService {

	private static Logger logger = LoggerFactory.getLogger(EntityAuditService.class);
	
	//@Autowired
	//private EntityAuditRepository entityAuditRepository;
	@PersistenceContext
	EntityManager em;
	
	public void EntityAuditService() {
		
	}
	
	@PostConstruct
	public void PostConstruct(){
	     // init code goes here
		logger.info("EntityAuditService PostConstruct");
	}
	
	public void save(EntityAudit entityAudit) {
		logger.info("EntityAuditService save ");
		//entityAuditRepository.save(entityAudit);
		//em.getTransaction().begin();
		em.persist(entityAudit);
		//em.getTransaction().commit();
		
	}
	
	public void print() {
		logger.info("EntityAuditService print");
	}
	
}
