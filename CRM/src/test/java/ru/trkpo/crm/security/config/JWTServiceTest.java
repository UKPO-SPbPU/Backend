package ru.trkpo.crm.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.ClientDetails;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.crm.security.data.user.User;
import ru.trkpo.crm.security.data.user.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class JWTServiceTest {

    private static final String JWT_SECRET_KEY = "47C6DFD979C465D64E72CAEF4595222F985B470FE6E85A776B50B9D6F86D6D5E";
    private static final long JWT_EXPIRATION = 86400000;
    private static final JWTService underTestService = new JWTService();

    @BeforeAll
    static void beforeAll() {
        setField(underTestService, "jwtSecretKey", JWT_SECRET_KEY);
        setField(underTestService, "jwtExpiration", JWT_EXPIRATION);
    }

    @Test
    void testExtractUsernameShouldReturnUsernameString() {
        String username = "71112223344";
        String token = generateTestToken(username);
        String resultString = underTestService.extractUsername(token);
        assertThat(resultString).isEqualTo(username);
    }

    @Test
    void testExtractClaimShouldReturnSubjectClaim() {
        String username = "71112223344";
        String token = generateTestToken(username);
        String resultString = underTestService.extractClaim(token, Claims::getSubject);
        assertThat(resultString).isEqualTo(username);
    }

    @Test
    void testGenerateTokenShouldReturnTokenString() {
        String username = "71112223344";
        String token = generateTestToken(username);
        User user = createUser(username);

        String resultToken = underTestService.generateToken(user);

        assertThat(resultToken).isNotNull().isNotEmpty();
        assertThat(resultToken.length()).isEqualTo(token.length());
    }

    @Test
    void testIsTokenValidShouldReturnTrue() {
        String username = "71112223344";
        String token = generateTestToken(username);
        User user = createUser(username);

        boolean result = underTestService.isTokenValid(token, user);

        assertThat(result).isTrue();
    }

    @Test
    void testIsTokenValidShouldReturnFalse() {
        String username = "71112223344";
        String token = Jwts.builder()
                .claims(new HashMap<>())
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY)), Jwts.SIG.HS256)
                .compact();
        User user = createUser(username);

        boolean result = underTestService.isTokenValid(token, user);

        assertThat(result).isFalse();
    }

    private String generateTestToken(String username) {
        return Jwts.builder()
                .claims(new HashMap<>())
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY)), Jwts.SIG.HS256)
                .compact();
    }

    private User createUser(String username) {
        LocalDate currentDate = LocalDate.now();
        String lastName = "Lastname";
        String firstName = "Firstname";
        String patronymic = "Patronymic";
        LocalDate birthday = LocalDate.of(2002, 8, 8);
        String email = "temp@email.com";
        String passport = "1111222333";
        String someString = "Some string";
        int someNumber = 1234567890;
        Client client = Client.builder()
                .id(1L).firstName(firstName).lastName(lastName).patronymic(patronymic).birthday(birthday)
                .build();
        PhoneNumber phoneNumber = PhoneNumber.builder()
                .clientId(1L).client(client).phoneNumber(username).balance(BigDecimal.valueOf(500))
                .build();
        ClientDetails clientDetails = ClientDetails.builder()
                .id(1L).client(client).numberPersonalAccount(someNumber).email(email)
                .password("Encoded password").region(someString).passport(passport)
                .contractDate(currentDate).contractNumber(someString)
                .build();
        client.setPhoneNumber(phoneNumber);
        client.setClientDetails(clientDetails);
        return new User(1L, client, UserRole.USER);
    }
}