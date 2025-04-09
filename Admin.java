
package com.CampusCart.Campus_backend.models;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    
    @Column(name = "name")
    private String name;

    @Column(name="email")
    private String email; 
    
     @Column(name="password_hash")
    private String password; 

}
