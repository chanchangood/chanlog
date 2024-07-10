package com.example.chanlog.service;

import com.example.chanlog.domain.Role;
import com.example.chanlog.domain.User;
import com.example.chanlog.repository.RoleRepository;
import com.example.chanlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @Transactional
    public User registUser(User user){
        //role 추가
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Collections.singleton(userRole));
        //비밀번호 인코딩해서 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return  userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    //oauth
    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndSocialId(String provider, String socialId) {
        return userRepository.findByProviderAndSocialId(provider, socialId);
    }

    @Transactional(readOnly = false)
    public User saveUser(String username, String name, String email, String socialId, String provider){
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setSocialId(socialId);
        user.setProvider(provider);
        user.setPassword(passwordEncoder.encode("")); // 비밀번호는 소셜 로그인 사용자의 경우 비워둡니다.
        return userRepository.save(user);
    }


    //jwt
    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
}
