package com.dt.rts.eregusa.audit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Persistent class for entity stored in table "EntityAudit"
 *
 */

@Entity
@Table(name = "entity_audit")
// Define named queries here
@NamedQueries({ @NamedQuery(name = "EntityAudit.countAll", query = "SELECT COUNT(x) FROM EntityAudit x") })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "entity_audit")
public class EntityAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    
    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS
    //----------------------------------------------------------------------
    @Column(name="created_by")
    private String createdBy;
    
	@Column(name="old_values")
	@Lob
    private String oldValues;
	
	@Column(name="new_values")
	@Lob
    private String newValues;

	@Column(name="operation")
    private String operation; //insert/update/delete
	
	@Column(name="row_id")
    private String rowId; //primary key of the entity
	
	@Column(name="entity")
    private String entity;

	public Long getId() {
        return id;
	}
	public void setId(Long id) {
        this.id = id;
	}
   
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getOldValues() {
		return oldValues;
	}

	public void setOldValues(String oldValues) {
		this.oldValues = oldValues;
	}

	public String getNewValues() {
		return newValues;
	}

	public void setNewValues(String newValues) {
		this.newValues = newValues;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
	

}
