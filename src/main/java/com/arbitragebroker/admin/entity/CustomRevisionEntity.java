package com.arbitragebroker.admin.entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.*;

@Entity
@Table(name = "revinfo")
@SequenceGenerator(
    name = "revinfo_seq",
    sequenceName = "revinfo_seq", // Ensure both applications use this
    allocationSize = 1)
@RevisionEntity
public class CustomRevisionEntity extends DefaultRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_seq")
    private int id;
}
