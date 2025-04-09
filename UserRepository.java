
// This allows to fetch user by email, useful for authentication
package com.CampusCart.Campus_backend.repository;

import com.CampusCart.Campus_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository; //import JpaRepository which provides built-in database methods.
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}