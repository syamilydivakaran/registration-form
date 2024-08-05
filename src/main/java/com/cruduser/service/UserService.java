package com.cruduser.service;

import java.util.Optional;

import com.cruduser.entity.User;

public interface UserService {
    void saveUser(User user);
    Optional<User> findUserByUsername(String username);
    void updateUser(User user);
    void deleteUser(User user);
}
