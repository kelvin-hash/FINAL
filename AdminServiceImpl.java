
package com.CampusCart.Campus_backend.service;

import com.CampusCart.Campus_backend.models.Admin;
import com.CampusCart.Campus_backend.models.User;
import com.CampusCart.Campus_backend.models.Report;
import com.CampusCart.Campus_backend.repository.AdminRepository;
import com.CampusCart.Campus_backend.repository.UserRepository;
import com.CampusCart.Campus_backend.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public AdminServiceImpl(AdminRepository adminRepository, UserRepository userRepository,ReportRepository reportRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
     @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public List<Report> getReportedUsers() {
        return reportRepository.findAll();
    }
}
