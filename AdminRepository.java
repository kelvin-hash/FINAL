

//Find admin by email for authentication
package com.CampusCart.Campus_backend.repository;

import com.CampusCart.Campus_backend.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail (String email);
}
