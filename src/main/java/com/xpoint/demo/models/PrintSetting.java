package com.xpoint.demo.models;



import lombok.Data;

@Data
public class PrintSetting {
	
	 private String orientation;
	    private String colorMode;
	    private String sides;
	    private String printQuality;
	    private int copies;
	    private int scaling;
	    private int nUp;
	    private String pageRanges;
}
