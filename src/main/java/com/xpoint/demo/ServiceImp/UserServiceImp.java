package com.xpoint.demo.ServiceImp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpoint.demo.Service.UserService;
import com.xpoint.demo.models.User;
import com.xpoint.demo.repository.UserRepository;


@Service
public class UserServiceImp implements UserService {
	
	
	
	
	
	
	 @Autowired
	    private UserRepository userRepository;

	    // Method to insert user into DB by setting individual fields
	    public User insertUser(String name, String email, String contactNo, String address, String profilePic) {
	        // Create a new User object
	        User user = new User();

	        // Set all the fields of the User object
	        user.setName(name);
	        user.setEmail(email); 
	        user.setContactNo(contactNo);
	        user.setAddress(address); // Assuming 'Addres' is a typo, fix it to 'address'
	        user.setProfilePic(profilePic);;

	        // Set createdAt to the current time (optional, depending on your use case)
	        user.setCreatedAt(LocalDateTime.now());

	        // Save the user object to the database
	        return userRepository.save(user);
	    }
	    
	    
	    
	    public Optional<User> getUserByContactNo(String contactNo) {
	        return userRepository.findByContactNo(contactNo);
	    }

}
