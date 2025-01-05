package com.furkanbegen.routes.security;

import com.furkanbegen.routes.model.User;
import com.furkanbegen.routes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsertUserComponent implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(final String... args) {

    var users = userRepository.findAll();

    if (users.isEmpty()) {
      var user = new User();
      user.setEmail("test@test.com");
      user.setPassword(passwordEncoder.encode("123456"));
      user.setName("Test");
      user.setSurname("User");

      userRepository.save(user);
    }
  }
}
