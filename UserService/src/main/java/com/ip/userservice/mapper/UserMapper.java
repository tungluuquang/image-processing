package com.ip.userservice.mapper;

import com.ip.userservice.dto.request.UserRequest;
import com.ip.userservice.dto.response.UserResponse;
import com.ip.userservice.model.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authProvider", ignore = true)
    void updateUserFromRequest(UserRequest request, @MappingTarget User user);
}