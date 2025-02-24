package com.xpoint.demo.controller;


import com.xpoint.demo.DTO.ShopWithDistance;
import com.xpoint.demo.Service.ShopService;
import com.xpoint.demo.models.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*") // Allow specific origin
@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    // Get all shops
    @GetMapping("/all")
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }
    
    
    @PostMapping("/addShop")
    public String uploadShopProfilePic(
        @RequestParam("shopName") String shopName,
        @RequestParam("ownerName") String ownerName,
        @RequestParam("email") String email,
        @RequestParam("contactInfo") String contactInfo,
        @RequestParam("location") String location,
        @RequestParam("latitude") String latitude,
        @RequestParam("longitude") String longitude,
        @RequestParam("profilePic") MultipartFile file
    ) {
        
    	return shopService.insertShop(shopName, ownerName, email, contactInfo, location, latitude, longitude, file);
    }
    
    
    
    
    @GetMapping("/getShopByContactNo")
    public ResponseEntity<?> getShopByContactNo(@RequestParam String contactNo) {
        Optional<Shop> shop = shopService.getShopByContactNo(contactNo);
        
        if (shop.isPresent()) {
            return ResponseEntity.ok(shop.get()); // Return shop data if found
        } else {
            return ResponseEntity.status(404).body("Shop not found");
        }
    }
    
    
    
    
    
    @GetMapping("/getNearestShops")
    public  List<ShopWithDistance> getNearbyShops(@RequestParam double userLatitude, double userLongitude){
    
    	return shopService.getNearbyShops(userLatitude, userLongitude);
    }


}