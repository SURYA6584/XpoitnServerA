package com.xpoint.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpoint.demo.models.Attachment;
import com.xpoint.demo.models.User;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	
	Optional<User> findByUserId(Long userId);
    Optional<User> findByContactNo(String contactNo);  // Query by contact number

}
