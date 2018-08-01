package es.uca.allergio.backend.services;

import es.uca.allergio.backend.entities.Rol;
import es.uca.allergio.backend.entities.User;
import es.uca.allergio.backend.repositories.RolRepository;
import es.uca.allergio.backend.repositories.UserRepository;
import es.uca.allergio.backend.security.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    CustomAuthenticationProvider authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    public User login(String username, String password) {

        try {
            Authentication token = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            if(token == null)
                return null;
            return userRepository.findByUsername(username);
        }catch (AuthenticationException ex) {
            return null;
        }

    }

    public Boolean register(String name, String surname, String username, String password) {

        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            List<Rol> roles = new ArrayList<>();
            Optional<Rol> clientRol = rolRepository.findByName("CLIENT_ROL");

            clientRol.ifPresent(roles::add);
            user.setName(name);
            user.setSurname(surname);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);

            save(user);

            return true;
        }

        return false;

    }

    public User save (User user) {
        return userRepository.save(user);
    }

}
