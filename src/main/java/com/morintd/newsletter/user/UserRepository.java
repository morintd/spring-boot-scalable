package com.morintd.newsletter.user;

import com.morintd.newsletter.user.dao.User;
import com.morintd.newsletter.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRepository {
    private final UserDAO dao;

    @Autowired
    public UserRepository(UserDAO dao) {
        this.dao = dao;
    }

    public Optional<User> findByEmail(String email) {
        return this.dao.findByEmail(email);
    }

    public Optional<User> findByRefresh(String userId, String refreshId) {
        return this.dao.findByIdAndRefreshId(userId, refreshId);
    }

    public User create(User user) {
        return this.dao.save(user);
    }

    public User update(User user) {
        return this.dao.save(user);
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return dao.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }
}
