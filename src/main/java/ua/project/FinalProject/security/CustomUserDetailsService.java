package ua.project.FinalProject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        int phoneNumber = Integer.parseInt(username);
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }

        UserDetails userDetails = User.builder()
                .username(String.valueOf(userEntity.getPhoneNumber()))
                .password(userEntity.getPassword())
                .roles(String.valueOf(userEntity.getPhoneNumber()))
                .build();

        return userDetails;
    }
}
