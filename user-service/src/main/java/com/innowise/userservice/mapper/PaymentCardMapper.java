package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.request.PaymentCardCreateRequest;
import com.innowise.userservice.dto.request.PaymentCardUpdateRequest;
import com.innowise.userservice.dto.response.PaymentCardResponse;
import com.innowise.userservice.entity.PaymentCardEntity;
import com.innowise.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentCardEntity toEntity(PaymentCardCreateRequest request, UserEntity user);

    @Mapping(target = "userId", source = "user.id")
    PaymentCardResponse toResponse(PaymentCardEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PaymentCardUpdateRequest request, @MappingTarget PaymentCardEntity entity);
}
