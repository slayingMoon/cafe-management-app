package com.example.cafebackend.repository;

import com.example.cafebackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    @Query("select new com.example.cafebackend.model.dto.UserBindingModel(u.id, u.name, u.email, u.contactNumber, u.isVerified)" +
//            "from User u where u.role = 'ROLE_USER'")
//    List<UserBindingModel> getAllUsers();
}
