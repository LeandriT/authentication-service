package com.seek.authentication_service.service;

import com.seek.authentication_service.dto.request.UserPreferencesRequest;
import com.seek.authentication_service.dto.response.UserPreferencesResponse;
import com.seek.authentication_service.exceptions.RecordAlreadyExistsException;
import com.seek.authentication_service.exceptions.UserNotFoundException;
import com.seek.authentication_service.exceptions.UserPreferencesNotFoundException;
import com.seek.authentication_service.mapper.UserPreferencesMapper;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.model.UserPreferences;
import com.seek.authentication_service.repository.UserPreferencesRepository;
import com.seek.authentication_service.repository.UserRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserRepository userRepository;
    private final UserPreferencesMapper userPreferencesMapper;


    @Override
    public UserPreferencesResponse create(UserPreferencesRequest request) {
        UserPreferences userPreferences = userPreferencesMapper.toModel(request);
        User user = userRepository.findById(request.getUserUuid()).orElseThrow(UserNotFoundException::new);
        boolean keyExistsForUser = userPreferencesRepository.existsByParameterKeyAndUserUuid(
                request.getParameterKey(),
                request.getUserUuid()
        );
        if (keyExistsForUser) {
            String message = String.format("Registro con key ya existe %s", request.getParameterKey());
            throw new RecordAlreadyExistsException(message);
        }
        userPreferences.setUser(user); // Asociar el usuario
        UserPreferences saved = userPreferencesRepository.save(userPreferences);
        return userPreferencesMapper.toDto(saved);
    }

    @Override
    public UserPreferencesResponse update(UUID uuid, UserPreferencesRequest request) {
        UserPreferences userPreferences = userPreferencesRepository.findById(uuid).orElseThrow(
                UserPreferencesNotFoundException::new);
        boolean existsByParameterKey = userPreferencesRepository.existsByParameterKeyAndUserUuidAndUuidNot(
                request.getParameterKey(),
                request.getUserUuid(),
                uuid
        );
        if (existsByParameterKey) {
            String message = String.format(
                    "Registro con key ya existe %s para usuario %s",
                    request.getParameterKey(),
                    request.getUserUuid()
            );
            throw new RecordAlreadyExistsException(message);
        }

        userPreferencesMapper.updateModel(request, userPreferences);
        userPreferencesRepository.save(userPreferences);
        return userPreferencesMapper.toDto(userPreferences);
    }

    @Override
    public UserPreferencesResponse findById(UUID uuid) {
        UserPreferences userPreferences = userPreferencesRepository.findById(uuid).orElseThrow(
                UserPreferencesNotFoundException::new);
        return userPreferencesMapper.toDto(userPreferences);
    }

    @Override
    public List<UserPreferencesResponse> findByUserUuid(UUID userUuid) {
        return userPreferencesRepository.findByUserUuid(userUuid)
                .stream()
                .map(userPreferencesMapper::toDto)
                .toList();
    }

    @Override
    public void delete(UUID uuid) {
        try {
            userPreferencesRepository.deleteById(uuid);
        } catch (DataIntegrityViolationException ex) {
            log.error("user preferences doest not exists, {}", uuid);
            throw new UserPreferencesNotFoundException("preferencias usuario no encontrado");
        }
    }

    @Override
    public Page<UserPreferencesResponse> index(Pageable pageable, String search) {
        Specification<UserPreferences> spec = RSQLJPASupport.toSpecification(search);
        return userPreferencesRepository.findAll(spec, pageable).map(userPreferencesMapper::toDto);
    }
}
