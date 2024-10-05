package ru.trkpo.crm.security.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trkpo.crm.security.AuthService;
import ru.trkpo.crm.security.data.auth.AuthRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordResponse;
import ru.trkpo.crm.security.data.auth.RegisterRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authServiceMock;

    @InjectMocks
    private AuthController underTestController;

    @Test
    void testRegisterShouldReturnResponseEntityWithStatusOK() {
        String someString = "Some String";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("LastName FirstName Patronymic")
                .phoneNumber("71112223344")
                .password("Encrypted password")
                .build();
        when(authServiceMock.register(any(RegisterRequest.class))).thenReturn(someString);
        ResponseEntity<String> resultResponse = underTestController.register(registerRequest);
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(someString);
        verify(authServiceMock, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testLoginShouldReturnResponseEntityWithStatusOK() {
        String someString = "Some String";
        AuthRequest authRequest = AuthRequest.builder()
                .phoneNumber("71112223344")
                .password("Encrypted password")
                .build();
        when(authServiceMock.authenticate(any(AuthRequest.class))).thenReturn(someString);
        ResponseEntity<String> resultResponse = underTestController.login(authRequest);
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(someString);
        verify(authServiceMock, times(1)).authenticate(any(AuthRequest.class));
    }

    @Test
    void testChangePasswordShouldReturnResponseEntityWithStatusOK() {
        String someString = "Some String";
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(someString)
                .currentPassword(someString)
                .newPassword(someString)
                .build();
        ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.builder()
                .success(true)
                .message(someString)
                .build();
        when(authServiceMock.changePassword(any(ChangePasswordRequest.class))).thenReturn(changePasswordResponse);
        ResponseEntity<ChangePasswordResponse> resultResponse = underTestController
                .changePassword(changePasswordRequest);
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultResponse.getBody()).isEqualTo(changePasswordResponse);
        verify(authServiceMock, times(1)).changePassword(any(ChangePasswordRequest.class));
    }
}
