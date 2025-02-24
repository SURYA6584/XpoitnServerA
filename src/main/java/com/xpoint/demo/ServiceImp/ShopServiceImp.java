package com.xpoint.demo.ServiceImp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.xpoint.demo.DTO.ShopWithDistance;
import com.xpoint.demo.Service.ShopService;
import com.xpoint.demo.models.Shop;
import com.xpoint.demo.repository.ShopRepository;


@Service
public class ShopServiceImp implements ShopService {
	
	
	
	
	
	

    @Autowired
    private ShopRepository shopRepository;
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploadProfilePic/";

    
    
    
    

    private String generateQRCode(String shopId) {
        try {
            // Ensure QR code directory exists
            File qrCodeDir = new File(UPLOAD_DIR);
            if (!qrCodeDir.exists()) {
                qrCodeDir.mkdirs();
            }

            // Generate unique filename
            String qrFileName = "QR_" + shopId+ ".png";
            Path qrFilePath = Paths.get(UPLOAD_DIR, qrFileName);

            // Generate QR code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    "https://http://localhost:5173/shopHome" + shopId, // The data to encode (shop link)
                    BarcodeFormat.QR_CODE,
                    200, 200  // Width x Height
            );

            // Save QR code as image
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrFilePath);

            // Return the relative URL
            return "/uploadProfilePic/" + qrFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle errors properly in production
        }
    }
    
    
    public String insertShop(String shopName, String ownerName, String email, String contactInfo, String location,
            String latitude, String longitude, MultipartFile file) {
try {
// Create the upload directory if it doesn't exist
File uploadDir = new File(UPLOAD_DIR);
if (!uploadDir.exists()) {
uploadDir.mkdirs();  // Make sure the directory is created
}

// Save the uploaded file
String fileName = file.getOriginalFilename();
Path filePath = Paths.get(UPLOAD_DIR, fileName);
Files.write(filePath, file.getBytes());

// Save shop details in the database
Shop shop = new Shop();
shop.setShopName(shopName);
shop.setOwnerName(ownerName);
shop.setEmail(email);
shop.setContactInfo(contactInfo);
shop.setLocation(location);
shop.setLatitude(latitude);
shop.setLongitude(longitude);
shop.setProfile_pic("/uploadProfilePic/" + fileName); // Save relative path

//Save the shop first to get shopId
shopRepository.save(shop);

// Generate and save QR code
String qrCodeUrl = generateQRCode(shop.getShopId().toString());
System.out.println("qrCodeUrl"+qrCodeUrl);
shop.setQrCodeUrl(qrCodeUrl);

// Save again with the QR code URL
shopRepository.save(shop);

return "Shop and profile picture uploaded successfully!";
} catch (IOException e) {
e.printStackTrace();
return "Error uploading profile picture: " + e.getMessage();
}
}

   
        // Save the shop object to the database
    
    
    
    
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
    
    
    public Optional<Shop> getShopByContactNo(String contactNo) {
        return shopRepository.findByContactInfo(contactNo);
    }
    
    
 // Haversine formula to calculate the distance between two points on the Earth's surface
    public  double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }


    public List<ShopWithDistance> getNearbyShops(double userLatitude, double userLongitude) {
        final double radius = 30.0; // 30 km radius threshold

        // Bounding box approach (approximate)
        double latDiff = radius / 111.0; // ~1 degree = 111 km
        double lonDiff = radius / (111.0 * Math.cos(Math.toRadians(userLatitude))); // Account for longitude scaling

        // Fetch all shops from the repository
        List<Shop> shops = shopRepository.findAll();

        // Filter shops within the bounding box
        List<Shop> nearbyShops = shops.stream()
                .filter(shop -> {
                    double shopLatitude = Double.parseDouble(shop.getLatitude()); // Convert String to double
                    double shopLongitude = Double.parseDouble(shop.getLongitude()); // Convert String to double

                    boolean latInRange = shopLatitude >= userLatitude - latDiff &&
                                         shopLatitude <= userLatitude + latDiff;
                    boolean lonInRange = shopLongitude >= userLongitude - lonDiff &&
                                         shopLongitude <= userLongitude + lonDiff;

                    return latInRange && lonInRange;
                })
                .collect(Collectors.toList());

        // Calculate the distance for each shop and sort by the nearest
        List<ShopWithDistance> shopsWithDistance = nearbyShops.stream()
                .map(shop -> {
                    double shopLatitude = Double.parseDouble(shop.getLatitude()); // Convert String to double
                    double shopLongitude = Double.parseDouble(shop.getLongitude()); // Convert String to double

                    double distance = haversineDistance(userLatitude, userLongitude, shopLatitude, shopLongitude);
                    return new ShopWithDistance(shop, distance);
                })
                .sorted(Comparator.comparingDouble(ShopWithDistance::getDistance))
                .collect(Collectors.toList());

        // Return the top 4 nearest shops
        return shopsWithDistance;
    }

   
    
    

}
