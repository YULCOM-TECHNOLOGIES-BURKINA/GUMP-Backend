package com.yulcomtechnologies.usersms.mappers;

import com.yulcomtechnologies.usersms.dtos.UserDto;
import com.yulcomtechnologies.usersms.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );


    UserDto toUserDto(User user);

}
