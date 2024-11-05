package com.yulcomtechnologies.usersms.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetAdminTokenDto(@JsonProperty("access_token") String accessToken) {
}
