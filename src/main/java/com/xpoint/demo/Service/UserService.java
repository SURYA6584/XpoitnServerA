package com.xpoint.demo.Service;

import java.util.Optional;

import com.xpoint.demo.models.User;

public  interface UserService {
	
	
	
    public User insertUser(String name, String email, String contactNo, String address, String profilePic);
    public Optional<User> getUserByContactNo(String contactNo);

}
