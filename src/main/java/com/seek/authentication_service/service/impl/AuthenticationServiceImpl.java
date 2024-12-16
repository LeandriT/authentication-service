package com.seek.authentication_service.service.impl;


import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.request.UserUpdateRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserPasswordResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.exceptions.LocationNotFoundException;
import com.seek.authentication_service.exceptions.UserAlreadyExistsException;
import com.seek.authentication_service.exceptions.UserNotFoundException;
import com.seek.authentication_service.mapper.UserMapper;
import com.seek.authentication_service.model.Location;
import com.seek.authentication_service.model.Token;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.LocationRepository;
import com.seek.authentication_service.repository.TokenRepository;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.service.AuthenticationService;
import com.seek.authentication_service.service.EmailService;
import com.seek.authentication_service.service.JwtService;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public AuthenticationServiceImpl(UserRepository repository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService,
                                     TokenRepository tokenRepository,
                                     AuthenticationManager authenticationManager,
                                     UserMapper userMapper,
                                     LocationRepository locationRepository,
                                     EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.locationRepository = locationRepository;
        this.emailService = emailService;
    }

    public UserResponse register(UserRequest request) {
        log.info("** Registering user **");
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            final String message =
                    String.format("El usuario ya ha sido registrado con el email: %s.", request.getEmail());
            log.warn(message);
            throw new UserAlreadyExistsException(message);
        }
        if (repository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            final String message =
                    String.format("El usuario ya ha sido registrado con el número de telefono: %s.",
                            request.getPhoneNumber());
            log.warn(message);
            throw new UserAlreadyExistsException(message);
        }
        String password = passwordEncoder.encode(request.getPassword());
        request.setPassword(password);

        User user = userMapper.toModel(request);
        Location location = locationRepository.findById(request.getLocationUuid())
                .orElseThrow(() -> new LocationNotFoundException("Localidad no encontrada"));
        user.setLocation(location);
        user.setUsername(this.generateUniqueUsername(user.getFullName()));
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

    public UserResponse update(UUID uuid, UserUpdateRequest request) {
        log.info("** Updating user {} **", request.getFullName());
        User userFound =
                repository.findById(uuid).orElseThrow(() -> new UserNotFoundException("User doest not exists"));
        if (repository.findByEmailAndUuidNot(request.getEmail(), uuid).isPresent()) {
            final String message = String.format("Usuario ya existente con email, %s.", request.getEmail());
            log.warn(message);
            throw new UserAlreadyExistsException(message);
        }
        if (repository.findByPhoneNumberAndUuidNot(request.getPhoneNumber(), uuid).isPresent()) {
            final String message = String.format("Usuario ya existente con nro telefono, %s", request.getPhoneNumber());
            log.warn(message);
            throw new UserAlreadyExistsException(message);
        }
        String password = passwordEncoder.encode(request.getPassword());

        userFound.setFullName(request.getFullName());
        userFound.setPhoneNumber(request.getPhoneNumber());
        userFound.setEmail(request.getEmail());
        userFound.setRate(request.getRate());
        userFound.setBirthDay(request.getBirthDay());
        userFound.setPassword(password);
        Location location = locationRepository.findById(request.getLocationUuid())
                .orElseThrow(() -> new LocationNotFoundException("Ciudad no encontrada"));
        userFound.setLocation(location);
        try {
            repository.save(userFound);
        } catch (DataIntegrityViolationException ex) {
            throw new GenericException("Ocurrio un error al registrar el usuario");
        }
        log.info("Success update user");
        return userMapper.toDto(userFound);
    }

    @Override
    public UserPasswordResponse updatePassword(UUID uuid) {
        User userFound =
                repository.findById(uuid).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));
        String passwordDigest = this.generateTemporaryPassword();
        String password = passwordEncoder.encode(passwordDigest);
        userFound.setPassword(password);
        repository.save(userFound);
        this.sendHtmlMail(userFound.getFullName(), passwordDigest, userFound.getEmail());
        String messageResponse =
                String.format("Se ha enviado un email a: %s con la contraseña temporal.", userFound.getEmail());
        return new UserPasswordResponse(messageResponse);
    }

    private void sendHtmlMail(String fullName, String password, String email) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullName", fullName);
        variables.put("password", password);

        emailService.sendHtmlEmail(
                email,
                "Contraseña Temporal - Parqueo Express",
                variables,
                "email-template"
        );
    }

    @Override
    public UserResponse show(UUID uuid) {
        User userFound = repository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no existe"));
        return userMapper.toDto(userFound);
    }

    @Override
    public UserResponse showByPhoneNumberAndBirthDay(String phoneNumber, LocalDate birthDay) {
        String message = String.format("Usuario no encontrado: nro telefono %s, fecha cumpleaños %s", phoneNumber,
                birthDay.toString());
        User userFound = repository.findFirstByPhoneNumberAndBirthDayOrderByCreatedAtAsc(phoneNumber, birthDay)
                .orElseThrow(() -> new UserNotFoundException(message));
        return userMapper.toDto(userFound);
    }

    public TokenResponse authenticate(LoginRequest request) {
        log.info("Start authenticate user");

        // Verificar si el usuario existe en la base de datos
        User user = repository.findByEmailOrUsernameOrPhoneNumber(request.getEmail())
                .orElseThrow(() -> {
                    String message =
                            String.format("El Usuario que intenta iniciar sesion no existe: %s.", request.getEmail());
                    log.error(message);
                    return new UserNotFoundException(message);
                });

        // Intentar autenticar al usuario
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for user: {}", request.getEmail(), ex);
            throw new BadCredentialsException("No se pudo iniciar sesión. Revisa tu usuario y contraseña.");
        }

        // Generar el token JWT y realizar las operaciones necesarias
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

    /**
     * Genera un nombre de usuario único basado en el nombre completo.
     *
     * @param fullName El nombre completo del usuario.
     * @return Un nombre de usuario único.
     */
    private String generateUniqueUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }

        // Dividir el nombre completo en partes
        String[] parts = fullName.trim().toLowerCase().split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Full name must contain at least a first name and a last name");
        }

        // Identificar el primer nombre, segundo nombre (si existe) y primer apellido
        String firstName = parts[0];
        String secondName = parts.length >= 3 ? parts[1] : "";
        String lastName = parts[parts.length >= 4 ? 2 : 1];

        // Lista para almacenar variaciones del nombre de usuario
        List<String> usernameVariants = new ArrayList<>();

        // Generar combinaciones de usuario
        if (!secondName.isEmpty()) {
            for (int i = 1; i <= secondName.length(); i++) {
                String secondNameSubstring = secondName.substring(0, i);
                usernameVariants.add(firstName.charAt(0) + secondNameSubstring +
                        lastName); // Ejemplo: glcuasapas, galcuasapas, ganlcuasapas
            }
        }
        for (int i = 2; i <= firstName.length(); i++) {
            String firstNameSubstring = firstName.substring(0, i);
            usernameVariants.add(firstNameSubstring + lastName); // Ejemplo: gancuasapas, gandcuasapas
        }

        // Agregar las variantes base
        usernameVariants.add(firstName.charAt(0) + lastName); // Ejemplo: gcuasapas

        // Intentar encontrar un nombre de usuario único basado en las combinaciones generadas
        String uniqueUsername = null;
        for (String candidate : usernameVariants) {
            if (repository.findByUsername(candidate).isEmpty()) {
                uniqueUsername = candidate;
                break;
            }
        }

        // Si todas las combinaciones ya existen, agregar un número al final
        if (uniqueUsername == null) {
            uniqueUsername = addSuffixUntilUnique(firstName.charAt(0) + lastName);
        }

        return uniqueUsername;
    }

    /**
     * Agrega un número al final del nombre de usuario hasta que sea único.
     *
     * @param baseUsername El nombre de usuario base.
     * @return Un nombre de usuario único.
     */
    private String addSuffixUntilUnique(String baseUsername) {
        int suffix = 1;
        String uniqueUsername;
        do {
            uniqueUsername = baseUsername + String.format("%02d", suffix); // Ejemplo: gcuasapas01, gcuasapas02
            suffix++;
        } while (repository.findByUsername(uniqueUsername).isPresent());
        return uniqueUsername;
    }


    String generateTemporaryPassword() {
        // Longitud deseada de la contraseña
        final int passwordLength = 10;

        // Conjunto de caracteres permitidos (mayúsculas, minúsculas, números)
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final SecureRandom random = new SecureRandom();

        // Genera la contraseña aleatoria
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }
}
