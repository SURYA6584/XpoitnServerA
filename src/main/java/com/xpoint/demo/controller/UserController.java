
package com.xpoint.demo.controller;



import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xpoint.demo.Service.UserService;
import com.xpoint.demo.models.User;


@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173" ,"http://192.168.60.196:8080"}) // Allow specific origin

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUser")
    public String insertUser(@RequestBody User user) {
        // Call the service layer to insert a new user
        userService.insertUser(
            user.getName(),
            user.getEmail(),
            user.getContactNo(),  // Use camelCase field name
            user.getAddress(),    // Use camelCase field name
            user.getProfilePic()  // Use camelCase field name
        );

        return "User Added Successfully";
    }
    
    
    
    @GetMapping("/checkUser")
    public ResponseEntity<?> checkUserByContactNo(@RequestParam String contactNo) {
        Optional<User> user = userService.getUserByContactNo(contactNo);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get()); // Return the user data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User not found"));
        }
    }

    
    

}
