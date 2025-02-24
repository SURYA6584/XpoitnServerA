package com.xpoint.demo.controller;


import com.xpoint.demo.Service.Print_Service;
import com.xpoint.demo.models.PrintRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"}) // Allow specific origin

@RestController
@RequestMapping("/printer")
public class PrintControllerX {

@Autowired
Print_Service print_Service;


@Autowired
private SimpMessagingTemplate messagingTemplate;

@PostMapping("/print")
public String printPDFFile(@RequestBody List<PrintRequest> printRequests) {
    // Delegate the logic to the PrintService
    return null;

   
}


//
//@MessageMapping("/printRequest")
//public void handlePrintRequest(PrintRequest printRequestData ,Long shopId) {
//    // Here, you could process the print request as needed (save to DB, etc.)
//    System.out.println("Received Print Request: " + printRequestData);
//    
//    
////    PrintConfig printConfig =new PrintConfig();
////    
////    printConfig.setCopies(printRequestData.getPrintConfig().getCopies());
////    printConfig.setPaperSize(printRequestData.getPrintConfig().getPaperSize());
////    printConfig.setColorMode(printRequestData.getPrintConfig().getColorMode());
////    printConfig.setPrintQuality(printRequestData.getPrintConfig().getPrintQuality());
////    printConfig.setLayout(printRequestData.getPrintConfig().getLayout());
////
////    printConfig = printConfigRepo.save(printConfig);
////    
////    PrintRequest printRequest = new PrintRequest();
////    printRequest.setFilePath(printRequestData.getFilePath());
////    printRequest.setSelectedPages(printRequestData.getSelectedPages());
////    printRequest.setPrintConfig(printConfig); // Set the saved PrintConfig
//
//    
//
//    // Broadcasting to the connected clients (e.g., shop systems)
//    messagingTemplate.convertAndSend("/topic/printJob/"+shopId, printRequestData);
//}



}
