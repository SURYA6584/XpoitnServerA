package com.xpoint.demo.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.xpoint.demo.models.PrintRequest;

public interface Print_Service {

	
    public String printPDFFile(List<PrintRequest> printRequests);

}
