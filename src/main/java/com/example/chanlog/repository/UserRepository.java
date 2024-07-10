package com.example.chanlog.repository;

import com.example.chanlog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);

    //oauth
    Optional<User> findByProviderAndSocialId(String provider, String socialId);
}
