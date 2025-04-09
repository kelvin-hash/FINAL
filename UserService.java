
//Manages user accounts
package com.CampusCart.Campus_backend.service;

import com.CampusCart.Campus_backend.models.User;

import java.util.Optional;

public interface UserService {
    User getUserById(Long id);
   
    User registerUser(User user);
    ///Authenticates user login by matching email and hashed password.
    Optional<User> authenticateUser(String email, String password);
   
}

