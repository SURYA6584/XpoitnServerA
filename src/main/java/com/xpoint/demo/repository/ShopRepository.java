package com.xpoint.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpoint.demo.models.Shop;
import com.xpoint.demo.models.User;
import java.util.List;



@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>{

	
	Optional<Shop>  findByShopId(Long shopId);

    Optional<Shop> findByContactInfo(String contactInfo); // Query by contact number

	
}
