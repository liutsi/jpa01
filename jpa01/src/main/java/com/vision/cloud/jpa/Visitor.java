
/*
 *  Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vision.cloud.jpa;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This class represent a visitor who leaves the comments/feedback after his/her visit.
 * This is the object that is persisted in the database.
 * 
 */
@Entity
public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String allComments;
    
    @Temporal(TemporalType.DATE)
    private Date dateVisited;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        
        this.name = name;
    }
    
    public void setAllComments(String allComments) {
        this.allComments = allComments;
    }

    public String getAllComments() {
        return allComments;
    }

    public void setDateVisited(Date dateVisited) {
        this.dateVisited = dateVisited;
    }

    public Date getDateVisited() {
        return dateVisited;
    }

    public Visitor() {
    }
    
    public Visitor(String name, String allComments, Date dateVisited) {
        this.name = name;
        this.dateVisited = dateVisited;
        this.allComments = allComments;
        
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (name != null ? name.hashCode() : 0);
        hash += (allComments != null ? allComments.hashCode() : 0);
        hash += (dateVisited != null ? dateVisited.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof Visitor)) {
            return false;
        }
        Visitor other = (Visitor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        
        if ((this.allComments == null && other.allComments != null) || (this.allComments != null && !this.allComments.equals(other.allComments))) {
            return false;
        }

        if ((this.dateVisited == null && other.dateVisited != null) || (this.dateVisited != null && !this.dateVisited.equals(other.dateVisited))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.vision.cloud.jpa.Visitor[ id=" + id + 
                                           "name=" + name +
                                           "allComments=" + allComments +
                                           "dateVisited=" + dateVisited +
                                         " ]";
    }

}
