package com.furkanbegen.routes.controller;

import com.furkanbegen.routes.dto.AuthRequestDTO;
import com.furkanbegen.routes.dto.AuthResponseDTO;
import com.furkanbegen.routes.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.furkanbegen.routes.constant.AppConstant.API_BASE_PATH;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_BASE_PATH)
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> authenticateAndGetToken(
      @Valid @RequestBody AuthRequestDTO authRequest) {
    return ResponseEntity.ok(authService.authenticateAndGetToken(authRequest));
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    authService.logout(request);
    return ResponseEntity.ok().build();
  }
}
