package com.xpoint.demo.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.xpoint.demo.DTO.ShopWithDistance;
import com.xpoint.demo.models.Shop;



public interface ShopService {

	
    public String insertShop(String shopName, String ownerName, String email, String contactInfo, String location,String latitude ,String longitude , MultipartFile file);
    public List<Shop> getAllShops();
    public Optional<Shop> getShopByContactNo(String contactNo);
   public  List<ShopWithDistance> getNearbyShops(double userLatitude, double userLongitude);
}

