
package com.CampusCart.Campus_backend.service;



import com.CampusCart.Campus_backend.models.Admin;
import com.CampusCart.Campus_backend.models.User;
import com.CampusCart.Campus_backend.models.Report;
import java.util.Optional;
import java.util.List;

public interface AdminService {
    Optional<Admin> findByEmail(String email);
    void deleteUser(Long userId);
    List<Admin> getAllAdmins();
     long getTotalUsers();
      List<User> getAllUsers();
    List<Report> getReportedUsers();
}

