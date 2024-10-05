package ru.trkpo.crm.security.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trkpo.crm.security.AuthService;
import ru.trkpo.crm.security.data.auth.AuthRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordRequest;
import ru.trkpo.crm.security.data.auth.ChangePasswordResponse;
import ru.trkpo.crm.security.data.auth.RegisterRequest;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authService.changePassword(changePasswordRequest));
    }
}
