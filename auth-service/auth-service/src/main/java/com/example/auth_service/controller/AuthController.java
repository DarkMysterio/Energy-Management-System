package com.example.auth_service.controller;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost"})
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private final JWTService jwtService;

    public AuthController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already created")
    })
    @PostMapping("/register")// folosim valid ca validarile sa fie facute inainte de intrarea in metoda
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }


    @Operation(
            summary = "User login",
            description = "Authenticate user and return JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
    @Operation(
            summary = "Validate JWT token",
            description = "Validate the provided JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token")
    })
    @GetMapping("/validate")  // Changed to GET for Traefik forwardAuth
    public ResponseEntity<Void> validate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletResponse response) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        String email  = jwtService.extractEmail(token);
        String role   = jwtService.extractRole(token);
        String userId = jwtService.extractUserId(token);

        response.setHeader("H-User-Email", email);
        response.setHeader("H-User-Role",  role);
        response.setHeader("H-User-Id",    userId);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/validate", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> validateOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "http://localhost:3000")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Authorization, Content-Type")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }
    
    @Operation(
            summary = "Delete user by email",
            description = "Delete user authentication credentials by email"
    )
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/user/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        authService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    

}
