package com.yulcomtechnologies.usersms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regions")
public class Region {
    @Id
    public String code;

    public String name;
}
