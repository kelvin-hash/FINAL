
package com.CampusCart.Campus_backend.service;

import com.CampusCart.Campus_backend.models.User;
import com.CampusCart.Campus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;



@Service
public class UserServiceImpl implements UserService {
    
    //perform database operations on User
private final UserRepository userRepository;
//hash and verify passwords
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

@Autowired
public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
}
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

   
    @Override
   public User registerUser(User user) {
       
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword); // Set the hashed password

        // Save the user with the hashed password
        return userRepository.save(user);
   }
   
     @Override
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        } else {
            return Optional.empty();
        }
    }
}
   

