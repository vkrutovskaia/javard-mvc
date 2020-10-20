package com.example.javardmvc.service;

import static com.example.javardmvc.domain.Role.USER;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.example.javardmvc.domain.User;
import com.example.javardmvc.repository.UserRepo;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserSevice implements UserDetailsService {

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private MailSender mailSender;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepo.findByUsername(username);
  }

  public boolean addUser(User user) {
    User userFromDb = userRepo.findByUsername(user.getUsername());
    if (nonNull(userFromDb)) {
      return false;
    }
    user.setActive(true);
    user.setRoles(singleton(USER));
    user.setActivationCode(UUID.randomUUID().toString());

    userRepo.save(user);

    if (!StringUtils.isEmpty(user.getEmail())) {
      String message = format(
          "Hello, %s! \n" +
              "Welcome to JavaRD. Please, visit next link: http://localhost:8080/activate/%s",
          user.getUsername(),
          user.getActivationCode()
      );
      mailSender.send(user.getEmail(), "Activation code", message);

    }

    return true;
  }

  public boolean activateUser(String code) {
    User user = userRepo.findByActivationCode(code);

    if (isNull(user)) {
      return false;
    }
    user.setActivationCode(null);

    userRepo.save(user);
    return false;
  }
}
