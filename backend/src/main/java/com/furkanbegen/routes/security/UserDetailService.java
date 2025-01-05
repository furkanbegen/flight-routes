package com.furkanbegen.routes.security;

import com.furkanbegen.routes.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var userOptional = userRepository.findByEmail(username);
    var securityUser =
        userOptional.orElseThrow(
            () -> new UsernameNotFoundException("User not found: " + username));

    return new SecurityUser(
        securityUser.getId(),
        securityUser.getEmail(),
        securityUser.getPassword(),
        securityUser.getName(),
        securityUser.getSurname());
  }
}
