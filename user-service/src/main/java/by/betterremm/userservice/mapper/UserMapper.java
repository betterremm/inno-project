package by.betterremm.userservice.mapper;

import by.betterremm.userservice.dto.request.UserCreateRequest;
import by.betterremm.userservice.dto.request.UserUpdateRequest;
import by.betterremm.userservice.dto.response.UserResponse;
import by.betterremm.userservice.dto.response.UserWithCardsResponse;
import by.betterremm.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserCreateRequest request);

    UserResponse toResponse(UserEntity entity);

    UserWithCardsResponse toResponseWithCards(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget UserEntity entity);
}
