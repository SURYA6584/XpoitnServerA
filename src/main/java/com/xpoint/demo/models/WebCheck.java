package com.xpoint.demo.models;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WebCheck {
	
	private String nickName;
	private String content;
	private Date timestamp;
	
	
	
	
	

}
