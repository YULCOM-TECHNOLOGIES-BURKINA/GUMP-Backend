package com.yulcomtechnologies.anpems.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDto {

    private Long id;
    private String nom;
    private String adresse;
    private String email;
    private String email_verified_at;
    private String created_at;
    private String updated_at;


}
