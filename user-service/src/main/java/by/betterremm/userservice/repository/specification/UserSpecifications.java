package by.betterremm.userservice.repository.specification;

import by.betterremm.userservice.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<UserEntity> byNameAndSurname(String name, String surname) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (surname != null && !surname.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%"));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
