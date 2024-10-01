package com.seek.authentication_service.service.impl;


import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.exceptions.UserAlreadyExistsException;
import com.seek.authentication_service.exceptions.UserNotFoundException;
import com.seek.authentication_service.mapper.UserMapper;
import com.seek.authentication_service.model.Token;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.TokenRepository;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.service.AuthenticationService;
import com.seek.authentication_service.service.JwtService;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(UserRepository repository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService,
                                     TokenRepository tokenRepository,
                                     AuthenticationManager authenticationManager,
                                     UserMapper userMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public UserResponse register(UserRequest request) {
        log.info("** Registering user **");
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("User already exists " + request.getUsername());
            throw new UserAlreadyExistsException("User already exists");
        }
        if (repository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            log.warn("User already exists with phone number " + request.getPhoneNumber());
            throw new UserAlreadyExistsException("User with phone number already exists");
        }
        String password = passwordEncoder.encode(request.getPassword());
        request.setPassword(password);
        User user = userMapper.toModel(request);
        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new GenericException("Ocurrio un error al registrar el usuario");
        }
        String jwt = jwtService.generateToken(user);
        saveUserToken(jwt, user);
        log.info("Success create user");
        return userMapper.toDto(user);
    }

    public UserResponse update(UUID uuid, UserRequest request) {
        log.info("** Updating user **");
        User userFound =
                repository.findById(uuid).orElseThrow(() -> new UserNotFoundException("User doest not exists"));
        if (repository.findByUsernameAndUuidNot(request.getUsername(), uuid).isPresent()) {
            log.warn("User already exists " + request.getUsername());
            throw new UserAlreadyExistsException("User already exists");
        }
        if (repository.findByPhoneNumberAndUuidNot(request.getPhoneNumber(), uuid).isPresent()) {
            log.warn("User already exists with phone number " + request.getPhoneNumber());
            throw new UserAlreadyExistsException("User with phone number already exists");
        }
        String password = passwordEncoder.encode(request.getPassword());
        userFound.setPassword(password);
        userFound.setUsername(request.getUsername());
        userFound.setPhoneNumber(request.getPhoneNumber());
        userFound.setRole(request.getRole());
        userFound.setEmail(request.getEmail());
        try {
            repository.save(userFound);
        } catch (DataIntegrityViolationException ex) {
            throw new GenericException("Ocurrio un error al registrar el usuario");
        }
        log.info("Success update user");
        return userMapper.toDto(userFound);
    }

    public TokenResponse authenticate(LoginRequest request) {
        log.info("Start authenticate user");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String message = String.format("User doest not exists %s", request.getUsername());
        User user =
                repository.findByUsername(request.getUsername()).orElseThrow(() -> new UserNotFoundException(message));
        String jwt = jwtService.generateToken(user);
        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);
        log.info("End authenticate user");
        return new TokenResponse(jwt);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getUuid());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t -> t.setLoggedOut(true));
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }
}
