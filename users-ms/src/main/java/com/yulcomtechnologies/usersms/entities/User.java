package com.yulcomtechnologies.usersms.entities;

import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "keycloak_user_id")
    private String keycloakUserId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "forename")
    private String forename;

    @Column(name = "region")
    private String region;

    @Column(name = "username")
    private String username;

    @Column(name = "cnss_number")
    private String cnssNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;
}


