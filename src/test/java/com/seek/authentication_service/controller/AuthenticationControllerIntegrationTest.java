package com.seek.authentication_service.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;
    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("testPassword");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setRole(Role.USER);

        userResponse = new UserResponse();
        userResponse.setUsername("testUser");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setRole(Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        tokenResponse = new TokenResponse();
        tokenResponse.setToken("mockedJwtToken");
    }

    @Test
    void testRegisterSuccess() throws Exception {
        when(authService.register(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mockedJwtToken"))
                .andReturn();
    }

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        when(authService.register(any(UserRequest.class)))
                .thenThrow(new RuntimeException("User already exists"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Ha ocurrido un error inesperado."))
                .andReturn();
    }

    @Test
    void testLoginFailure() throws Exception {
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Ha ocurrido un error inesperado."))
                .andReturn();
    }
    */
}
