package ua.project.FinalProject.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.project.FinalProject.Enum.SubscriptionEnum;
import ua.project.FinalProject.entity.UserEntity;
import ua.project.FinalProject.repository.UserRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long phoneNumber = Long.parseLong(username);
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }
        String subscriptionRole = userEntity.getSubscription().getSubscriptionEnum().name();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" +subscriptionRole );
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(userEntity.getPhoneNumber()),
                userEntity.getPassword(),
                authorities
        );
        return userDetails;
    }
}