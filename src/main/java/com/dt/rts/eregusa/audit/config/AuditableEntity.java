package com.dt.rts.eregusa.audit.config;

import java.io.Serializable;
import java.util.Arrays;

public class AuditableEntity implements Serializable {
	private String entity;
	private String[] attributes;
	
	public AuditableEntity() {
		// empty ctor
	}
	
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String[] getAttributes() {
		return attributes;
	}
	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}
	
	public boolean isAttributeAuditable(String attribute) {
		return Arrays.stream(attributes).anyMatch(attribute::equals);
	}
	
	@Override
	public String toString() {
		return "AuditableEntity [entity=" + entity + ", attributes size=" + attributes.length + "]";
	}
}
