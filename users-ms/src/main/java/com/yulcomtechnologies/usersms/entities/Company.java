package com.yulcomtechnologies.usersms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_address")
    private String postalAddress;

    @Column(name = "location")
    private String location;

    @Column(name = "ifu", nullable = false, unique = true, length = 255)
    private String ifu;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 255)
    private String phone;

    @Column(name = "representant_lastname")
    private String representantLastname;

    @Column(name = "representant_firstname")
    private String representantFirstname;

    @Column(name = "representant_phone")
    private String representantPhone;

    @Column(name = "representant_nip")
    private String representantNip;

    @ManyToOne
    @JoinColumn(name = "id_document_id", referencedColumnName = "id")
    private File idDocument;

    @ManyToOne
    @JoinColumn(name = "enterprise_status_id", referencedColumnName = "id")
    private File enterpriseStatut;

    @Column(name = "rccm")
    private String rccm;

    @Column(name = "nes")
    private String nes;

    public Company(String ifu, String name, String address, String email, String phone,String nes) {
        this.ifu = ifu;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.nes = nes;
    }
}
