
package com.xpoint.demo.ServiceImp;

import com.xpoint.demo.Service.Print_Service;
import com.xpoint.demo.models.PrintRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class PrintServiceImp implements Print_Service{

	@Override
	public String printPDFFile(List<PrintRequest> printRequests) {
		// TODO Auto-generated method stub
		return null;
	}

//    public ResponseEntity<String> printPDFFile(String fileName) {
//        try (InputStream inputStream = new FileInputStream(fileName)) {
//            // Load the PDF document from InputStream
//            PDDocument document = PDDocument.load(inputStream);
//
//            // Create a PrinterJob
//            PrinterJob printerJob = PrinterJob.getPrinterJob();
//            printerJob.setPageable(new PDFPageable(document)); // Set the document to print
//
//            // Find a print service (printer) and set it
//            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
//            System.out.println("Number of available printers: " + printServices.length);
//            for(int i=0;i< printServices.length;i++){
//            	
//        
//                System.out.println("Number of available printers: " + printServices[i]);
//
//            }
//            // Set the print service if available
//            if (printServices.length > 0) {
//                printerJob.setPrintService(printServices[0]); // Use the first available printer
//            } else {
//                document.close();
//                return ResponseEntity.status(404).body("No printer found.");
//            }
//
//            // Set print attributes (optional)
//            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//            attributes.add(new Copies(1)); // Set the number of copies
//
//            // Print the document
//            printerJob.print(attributes);
//
//            // Close the document
//            document.close();
//
//            return ResponseEntity.ok("Print job completed successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error loading PDF document: " + e.getMessage());
//        } catch (PrinterException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error printing document: " + e.getMessage());
//        }
//    }
//    
//	public String printPDFFile(List<PrintRequest> printRequests) {
//		
//		
//
//
//		for (PrintRequest printRequest : printRequests) {
//		    try {
//		        // Resolve absolute file path
//		        File file = new File(System.getProperty("user.dir") + printRequest.getFilePath());
//		        if (!file.exists()) {
//		            return "Error: File not found at " + file.getAbsolutePath();
//		        }
//
//		        // Load the PDF document
//		        try (InputStream inputStream = new FileInputStream(file)) {
//		            PDDocument document = PDDocument.load(inputStream);
//		            System.out.println("Starting printPDFFile method: " + printRequest);
//		            System.out.println("Starting documents: " + document);
//
//		            // If selected pages exist, handle them
//		            if (printRequest.getSelectedPages() != null && !printRequest.getSelectedPages().isEmpty()) {
//		                PDDocument selectedDocument = new PDDocument();
//
//		                // Add selected pages to the new document
//		                for (Integer pageNumber : printRequest.getSelectedPages()) {
//		                    PDPage page = document.getPage(pageNumber - 1); // PDF pages are 0-based
//		                    selectedDocument.addPage(page);
//		                }
//
//		                // Ensure PrintConfig is not null before passing to printDocument
//		                PrintConfig printConfig = printRequest.getPrintConfig();
//		                if (printConfig == null) {
//		                    return "Error: PrintConfig is missing.";
//		                }
//
//		                // Handle the printing of the selected document
//		                printDocument(selectedDocument, printConfig);
//		                selectedDocument.close();
//		            } else {
//		                // Print the entire document if no pages are selected
//		                PrintConfig printConfig = printRequest.getPrintConfig();
//		                if (printConfig == null) {
//		                    return "Error: PrintConfig is missing.";
//		                }
//		                printDocument(document, printConfig);
//		            }
//
//		            document.close();
//		        }
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		        return "Error processing PDF at " + printRequest.getFilePath() + ": " + e.getMessage();
//		    } catch (PrinterException e) {
//		        e.printStackTrace();
//		        return "Error printing document: " + e.getMessage();
//		    }
//		}
//		return "Print job initiated.";
//
//	}
//
//	private void printDocument(PDDocument document, PrintConfig printConfig) throws PrinterException {
//		
//	    System.out.println("Starting printDocument method");
//
//	    // Create a PrinterJob instance
//	    PrinterJob printerJob = PrinterJob.getPrinterJob();
//	    printerJob.setPageable(new PDFPageable(document)); // Set the document to print
//
//	    // Find a print service and set it
//	    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
//	    if (printServices.length > 0) {
//	        printerJob.setPrintService(printServices[0]); // Use the first available printer
//	        
//	        System.out.printf("the printter are ",printServices.length);
//	        System.out.println("The available printers are: " + Arrays.toString(printServices));
//
//	    } else {
//	        throw new PrinterException("No printer found.");
//	    }
//
//	    // Set print attributes (optional)
//	    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//	    attributes.add(new Copies(1)); // Set number of copies, can be customized based on PrintConfig
//	    // Add additional attributes from PrintConfig if needed (duplex, orientation, etc.)
//	    
//	 
//	    // Execute the print job
//	    printerJob.print(attributes);
//	}


	
}
