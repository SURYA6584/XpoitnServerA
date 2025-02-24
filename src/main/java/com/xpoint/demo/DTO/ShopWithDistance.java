package com.xpoint.demo.DTO;

import com.xpoint.demo.models.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShopWithDistance {
	
	
	 private Shop shop;
	    private double distance;
	
	

}
