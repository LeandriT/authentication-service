package com.seek.authentication_service.repository;

import com.seek.authentication_service.model.UserPreferences;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID>,
        JpaSpecificationExecutor<UserPreferences> {
    List<UserPreferences> findByUserUuid(UUID userUuid);

    boolean existsByParameterKeyAndUserUuid(String parameterKey, UUID uuid);

    boolean existsByParameterKeyAndUserUuidAndUuidNot(String parameterKey, UUID userUuid, UUID uuid);
}
