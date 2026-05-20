package by.betterremm.userservice.mapper;

import by.betterremm.userservice.dto.request.PaymentCardCreateRequest;
import by.betterremm.userservice.dto.request.PaymentCardUpdateRequest;
import by.betterremm.userservice.dto.response.PaymentCardResponse;
import by.betterremm.userservice.entity.PaymentCardEntity;
import by.betterremm.userservice.entity.UserEntity;
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
