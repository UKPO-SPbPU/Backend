package ru.trkpo.crm.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.ClientDetails;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.service.client.ClientService;
import ru.trkpo.common.service.clientDetails.ClientDetailsService;
import ru.trkpo.common.service.phoneNumber.PhoneNumberService;
import ru.trkpo.crm.security.config.JWTService;
import ru.trkpo.crm.security.data.auth.AuthRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordResponse;
import ru.trkpo.crm.security.data.auth.RegisterRequest;
import ru.trkpo.crm.security.data.user.User;
import ru.trkpo.crm.security.data.user.UserRepository;
import ru.trkpo.crm.security.data.user.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private JWTService jwtServiceMock;

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private ClientService clientServiceMock;

    @Mock
    private ClientDetailsService clientDetailsServiceMock;

    @Mock
    private PhoneNumberService phoneNumberServiceMock;

    @InjectMocks
    private AuthService underTestService;

    @Test
    void testRegisterShouldReturnTokenString() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("Lastname Firstname Patronymic")
                .phoneNumber("71112223344")
                .password("Password")
                .build();
        when(passwordEncoderMock.encode(anyString())).thenReturn("Encoded password");
        Client newClient = createClient(
                registerRequest.getFullName().split(" "),
                registerRequest.getPhoneNumber()
        );
        when(clientServiceMock.createNewClient(any(String[].class)))
                .thenReturn(newClient);
        when(phoneNumberServiceMock.createNewPhoneNumber(any(Client.class), anyString()))
                .thenReturn(newClient.getPhoneNumber());
        when(clientDetailsServiceMock.createNewClientDetails(any(Client.class), anyString()))
                .thenReturn(newClient.getClientDetails());
        when(clientServiceMock.saveClient(any(Client.class))).thenReturn(newClient);
        when(userRepositoryMock.save(any(User.class))).thenReturn(null);
        String token = "Token";
        when(jwtServiceMock.generateToken(any(User.class))).thenReturn(token);

        String resultToken = underTestService.register(registerRequest);
        assertThat(resultToken).isEqualTo(token);
        verify(passwordEncoderMock, times(1)).encode(anyString());
        verify(clientServiceMock, times(1)).createNewClient(any(String[].class));
        verify(phoneNumberServiceMock, times(1)).createNewPhoneNumber(any(Client.class), anyString());
        verify(clientDetailsServiceMock, times(1)).createNewClientDetails(any(Client.class), anyString());
        verify(clientServiceMock, times(1)).saveClient(any(Client.class));
        verify(userRepositoryMock, times(1)).save(any(User.class));
        verify(jwtServiceMock, times(1)).generateToken(any(User.class));
    }

    @Test
    void testAuthenticateShouldReturnTokenString() {
        String[] fio = new String[] {"Lastname", "Firstname", "Patronymic"};
        String phoneNumber = "71112223344";
        Client client = createClient(fio, phoneNumber);
        User user = User.builder()
                .id(1L)
                .client(client)
                .role(UserRole.USER)
                .build();
        String token = "Token";
        AuthRequest authRequest = AuthRequest.builder()
                .phoneNumber(phoneNumber).password("password")
                .build();
        when(authenticationManagerMock.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(jwtServiceMock.generateToken(any(User.class))).thenReturn(token);

        String resultToken = underTestService.authenticate(authRequest);

        assertThat(resultToken).isEqualTo(token);
        verify(authenticationManagerMock, times(1)).authenticate(any(Authentication.class));
        verify(userRepositoryMock, times(1)).findByPhoneNumber(anyString());
        verify(jwtServiceMock, times(1)).generateToken(any(User.class));
    }

    @Test
    void testAuthenticateShouldThrowException() {
        AuthRequest authRequest = AuthRequest.builder()
                .phoneNumber("71112223344").password("password")
                .build();
        when(authenticationManagerMock.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepositoryMock.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.authenticate(authRequest)).isInstanceOf(NoSuchElementException.class);

        verify(authenticationManagerMock, times(1)).authenticate(any(Authentication.class));
        verify(userRepositoryMock, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void testChangePasswordShouldReturnSuccessfulChangePasswordResponse() {
        String[] fio = new String[] {"Lastname", "Firstname", "Patronymic"};
        String phoneNumber = "71112223344";
        Client client = createClient(fio, phoneNumber);
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);
        when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(true);
        String newPasswordHash = "new password hash";
        when(passwordEncoderMock.encode(anyString())).thenReturn(newPasswordHash);
        when(clientServiceMock.saveClient(any(Client.class))).thenReturn(null);
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(phoneNumber)
                .currentPassword("current password")
                .newPassword("new password")
                .build();

        ChangePasswordResponse resultResponse = underTestService.changePassword(changePasswordRequest);

        assertThat(resultResponse.isSuccess()).isTrue();
        assertThat(client.getClientDetails().getPassword()).isEqualTo(newPasswordHash);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
        verify(passwordEncoderMock, times(1)).matches(anyString(), anyString());
        verify(passwordEncoderMock, times(1)).encode(anyString());
        verify(clientServiceMock, times(1)).saveClient(any(Client.class));
    }

    @Test
    void testChangePasswordShouldReturnCurrentPasswordMismatchFailureChangePasswordResponse() {
        String[] fio = new String[] {"Lastname", "Firstname", "Patronymic"};
        String phoneNumber = "71112223344";
        Client client = createClient(fio, phoneNumber);
        String oldPasswordHash = client.getClientDetails().getPassword();
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);
        when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(false);
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(phoneNumber)
                .currentPassword("invalid current password")
                .newPassword("new password")
                .build();

        ChangePasswordResponse resultResponse = underTestService.changePassword(changePasswordRequest);

        assertThat(resultResponse.isSuccess()).isFalse();
        assertThat(client.getClientDetails().getPassword()).isEqualTo(oldPasswordHash);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
        verify(passwordEncoderMock, times(1)).matches(anyString(), anyString());
        verify(clientServiceMock, never()).saveClient(any(Client.class));
    }

    @Test
    void testChangePasswordShouldReturnCurrentPasswordMatchesNewPasswordFailureChangePasswordResponse() {
        String[] fio = new String[] {"Lastname", "Firstname", "Patronymic"};
        String phoneNumber = "71112223344";
        Client client = createClient(fio, phoneNumber);
        String oldPasswordHash = client.getClientDetails().getPassword();
        when(clientServiceMock.findByPhoneNumber(anyString())).thenReturn(client);
        when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(true);
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .username(phoneNumber)
                .currentPassword("current password")
                .newPassword("current password")
                .build();

        ChangePasswordResponse resultResponse = underTestService.changePassword(changePasswordRequest);

        assertThat(resultResponse.isSuccess()).isFalse();
        assertThat(client.getClientDetails().getPassword()).isEqualTo(oldPasswordHash);
        verify(clientServiceMock, times(1)).findByPhoneNumber(anyString());
        verify(passwordEncoderMock, times(1)).matches(anyString(), anyString());
        verify(clientServiceMock, never()).saveClient(any(Client.class));
    }

    private Client createClient(String[] fio, String phoneNumberString) {
        LocalDate currentDate = LocalDate.now();
        String lastName = fio[0];
        String firstName = fio[1];
        String patronymic = fio.length == 3 ? fio[2] : null;
        LocalDate birthday = LocalDate.of(2002, 8, 8);
        String email = "temp@email.com";
        String passport = "1111222333";
        String someString = "Some string";
        int someNumber = 1234567890;
        Client client = Client.builder()
                .id(1L).firstName(firstName).lastName(lastName).patronymic(patronymic).birthday(birthday)
                .build();
        PhoneNumber phoneNumber = PhoneNumber.builder()
                .clientId(1L).client(client).phoneNumber(phoneNumberString).balance(BigDecimal.valueOf(500))
                .build();
        ClientDetails clientDetails = ClientDetails.builder()
                .id(1L).client(client).numberPersonalAccount(someNumber).email(email)
                .password("Encoded password").region(someString).passport(passport)
                .contractDate(currentDate).contractNumber(someString)
                .build();
        client.setPhoneNumber(phoneNumber);
        client.setClientDetails(clientDetails);
        return client;
    }
}