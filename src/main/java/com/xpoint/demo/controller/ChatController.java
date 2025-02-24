package com.xpoint.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpoint.demo.DTO.FileMessage;
import com.xpoint.demo.Service.AttachmentService;
import com.xpoint.demo.Service.ChatService;
import com.xpoint.demo.enumstore.FileType;
import com.xpoint.demo.enumstore.MessageStatus;
import com.xpoint.demo.enumstore.MessageType;
import com.xpoint.demo.models.Attachment;
import com.xpoint.demo.models.Message;
import com.xpoint.demo.models.Shop;
import com.xpoint.demo.models.User;
import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

//http://localhost:5174", "http://localhost:5173
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private AttachmentService attachmentService;

    
    @Autowired
  private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/start")
    public Long startConversation(@RequestParam long userId, @RequestParam long shopId) {
        return chatService.startConversation(userId, shopId);
    }
    
 

   
    @MessageMapping("/send")
    @SendTo("/topic/chat")
    @Transactional
    public Message sendMessage(@Payload Message message) {
    	
    	
    	chatService.sendMessage(message);
    	System.out.println("Received message: " + message);
        
        // Save the message using the service
      return  message;

    }
    
  
    private MessageType determineMessageType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {  // Convert to lower case to handle case insensitivity
            case "pdf":
                return MessageType.PDF;
            case "doc":
            case "docx":
                return MessageType.DOCX;  // You can return DOC or DOCX based on your preference
            case "txt":
                return MessageType.DOCUMENT;  // You can also add specific types if needed
            case "jpg":
            case "jpeg":
            case "png":
                return MessageType.IMAGE;
            case "mp4":
            case "avi":
                return MessageType.VIDEO;
            case "xls":
            case "xlsx":
                return MessageType.XLSX;
            case "ppt":
            case "pptx":
                return MessageType.PPT;
            case "zip":
                return MessageType.ZIP;
            default:
                return MessageType.FILE;
        }
    }


//    @MessageMapping("/uploads")
//    public Message uploadFile(@Payload FileMessage fileMessage) throws IOException {
//        String fileId = fileMessage.getFileId();
//
//        
//        
//        System.out.println("Received chunk: " + fileMessage.getChunkIndex() +
//                "/" + fileMessage.getTotalChunks() + " for File ID: " + fileMessage.getFileId());
//
//        // Get or create ByteArrayOutputStream for the file
//        ByteArrayOutputStream byteArrayOutputStream = fileChunksMap.computeIfAbsent(fileId, k -> new ByteArrayOutputStream());
//
//        // Decode and append chunk
//        byte[] fileContent = Base64.getDecoder().decode(fileMessage.getFileContent());
//        byteArrayOutputStream.write(fileContent);
//
//        // Track total chunks and received chunks
//        fileTotalChunksMap.putIfAbsent(fileId, fileMessage.getTotalChunks());
//        receivedChunkCount.put(fileId, receivedChunkCount.getOrDefault(fileId, 0) + 1);
//        Message message = new Message();
//
//        // Check if all chunks are received
//        if (receivedChunkCount.get(fileId).equals(fileTotalChunksMap.get(fileId))) {
//            byte[] completeFile = byteArrayOutputStream.toByteArray();
//
//            String fileName = fileMessage.getFileName();
//            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//
//            MessageType messageType = determineMessageType(fileExtension);
//
//            Attachment attachment = attachmentService.saveFile(completeFile, messageType, fileName);
//
//            message.setSenderId(fileMessage.getSenderId());
//            message.setConversationId(fileMessage.getConversationId());
//            message.setSenderType(fileMessage.getSenderType());
//            message.setMessageType(messageType);
//            message.setAttachment(attachment);
//            message.setSentAt(LocalDateTime.now());
//            message.setStatus(MessageStatus.SENT);
//
//            chatService.sendMessage(message);
//            messagingTemplate.convertAndSend("/topic/chat", message);
//
//            // Cleanup
//            fileChunksMap.remove(fileId);
//            fileTotalChunksMap.remove(fileId);
//            receivedChunkCount.remove(fileId);
//        }
//        return message;
//
//    }
    private String getUniqueFileName(File directory, String originalFileName) {
        String name = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        String uniqueFileName = originalFileName;
        int counter = 1;

        while (new File(directory, uniqueFileName).exists()) {
            uniqueFileName = name + "(" + counter + ")." + extension;
            counter++;
        }

        return uniqueFileName;
    }

   
    @PostMapping("/chunk/upload")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMessage") String fileMessageJson
    ) throws IOException {
        // Deserialize fileMessage from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        FileMessage fileMessage;
        try {
            fileMessage = objectMapper.readValue(fileMessageJson, FileMessage.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid fileMessage JSON");
        }

        // Save the chunk to a temporary directory
        File dir = new File("uploads_temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File chunkFile = new File(dir, fileMessage.getFileName() + ".part" + fileMessage.getChunkIndex());
        try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
            fos.write(file.getBytes());
        }

        Message message = new Message();

        // Combine chunks if all are uploaded
        if (fileMessage.getChunkIndex() + 1 == fileMessage.getTotalChunks()) {
            // Generate a unique file name if it already exists
            String uniqueFileName = getUniqueFileName(dir, fileMessage.getFileName());
            File completeFile = new File(dir, uniqueFileName);
            try (FileOutputStream fos = new FileOutputStream(completeFile, true)) {
                for (int i = 0; i < fileMessage.getTotalChunks(); i++) {
                    File partFile = new File(dir, fileMessage.getFileName() + ".part" + i);
                    fos.write(java.nio.file.Files.readAllBytes(partFile.toPath()));
                    partFile.delete();
                }

                // Determine the file type based on the extension
                String fileExtension = uniqueFileName.substring(uniqueFileName.lastIndexOf(".") + 1).toLowerCase();
                MessageType messageType = determineMessageType(fileExtension);
       System.out.println("Message Type is" +messageType);
                // Save the complete file's metadata, including the file path
                String filePath = completeFile.getAbsolutePath();
                Attachment attachment = attachmentService.saveFile(
                        messageType,
                        uniqueFileName, // Use the unique file name
                        completeFile.length(), // Pass the file size
                        filePath,
                        fileMessage.getPageCount()// Pass the file path
                );

                // Set message details
                message.setSenderId(fileMessage.getSenderId());
                message.setConversationId(fileMessage.getConversationId());
                message.setSenderType(fileMessage.getSenderType());
                message.setMessageType(messageType);
                message.setAttachment(attachment);
                message.setSentAt(LocalDateTime.now());
                message.setStatus(MessageStatus.SENT);
                 
                // Send the message
                chatService.sendMessage(message);

                // Notify progress via WebSocket
                messagingTemplate.convertAndSend("/topic/chat", message);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error merging chunks.");
            }
        }
        System.out.println("Chunk " + fileMessage.getChunkIndex() + " uploaded.");
        return ResponseEntity.ok("Chunk " + fileMessage.getChunkIndex() + " uploaded.");
    }
    
    
    
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMessage") String fileMessageJson
    ) throws IOException {
        // Deserialize fileMessage from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        FileMessage fileMessage;
        try {
            fileMessage = objectMapper.readValue(fileMessageJson, FileMessage.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid fileMessage JSON");
        }

        // Save the file to the server
        File dir = new File("uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Get the file name with extension
        String originalFileName = file.getOriginalFilename(); // Get original file name with extension
        if (originalFileName == null || originalFileName.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid file name");
        }

        // Generate a unique file name if necessary
        String uniqueFileName = originalFileName;
        File uploadedFile = new File(dir, uniqueFileName);

        // Save the file
        try (FileOutputStream fos = new FileOutputStream(uploadedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file.");
        }
        
        // Get file extension
        String fileExtension = uniqueFileName.substring(uniqueFileName.lastIndexOf(".") + 1).toLowerCase();
        MessageType messageType = determineMessageType(fileExtension); // Call method to get message type based on file extension

        long
        pageCount = 0;

     // If the file is a PDF, count the number of pages
     if ("pdf".equals(fileExtension)) {
         try (PDDocument document = PDDocument.load(uploadedFile)) {
             pageCount = document.getNumberOfPages();
         } catch (IOException e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading PDF file.");
         }
     }
        
        
        // Save file metadata (you can customize this part to save data to your database)
        String filePath = uploadedFile.getAbsolutePath();
        Attachment attachment = attachmentService.saveFile(
                messageType, // Determine file type based on extension
                uniqueFileName, // Use the unique file name with extension
                uploadedFile.length(), // File size
                filePath,
                pageCount               
               
        );

        // Create and populate the message
        Message message = new Message();
        message.setSenderId(fileMessage.getSenderId());
        message.setConversationId(fileMessage.getConversationId());
        message.setSenderType(fileMessage.getSenderType());
        message.setMessageType(messageType); // Use message type based on file extension
        message.setAttachment(attachment);
        message.setSentAt(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);


        // Send the message
        chatService.sendMessage(message);

        // Notify progress via WebSocket (optional)
        messagingTemplate.convertAndSend("/topic/chat", message);

        return ResponseEntity.ok("File uploaded successfully.");
    }


//    private Map<String, ByteArrayOutputStream> fileChunksMap = new HashMap<>();
//    private Map<String, Integer> fileTotalChunksMap = new HashMap<>();
//
//    @MessageMapping("/upload")
//    public void uploadFile(@Payload FileMessage fileMessage) throws IOException, InterruptedException {
//        String fileId = fileMessage.getFileId();
//
//        ByteArrayOutputStream byteArrayOutputStream = fileChunksMap.get(fileId);
//        if (byteArrayOutputStream == null) {
//            byteArrayOutputStream = new ByteArrayOutputStream();
//            fileChunksMap.put(fileId, byteArrayOutputStream);
//        }
//
//        byte[] fileContent = Base64.getDecoder().decode(fileMessage.getFileContent());
//        byteArrayOutputStream.write(fileContent);
//
//        if (!fileTotalChunksMap.containsKey(fileId)) {
//            fileTotalChunksMap.put(fileId, fileMessage.getTotalChunks());
//        }
//
//        if (fileMessage.getChunkIndex() == fileMessage.getTotalChunks() - 1) {
//            byte[] completeFile = byteArrayOutputStream.toByteArray();
//            String fileName = fileMessage.getFileName();
//            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//
//            MessageType messageType;
//            if (fileExtension.equals("pdf")) {
//                messageType = MessageType.DOCUMENT;
//
//                // Repair PDF
////                File tempFile = File.createTempFile("uploaded_", ".pdf");
////                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
////                    fos.write(completeFile);
////                }
////                File repairedFile = repairPdf(tempFile);
////                completeFile = Files.readAllBytes(repairedFile.toPath()); // Update the file content with the repaired version
//            } else if (fileExtension.equals("docx") || fileExtension.equals("txt")) {
//                messageType = MessageType.DOCUMENT;
//            } else if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("jpeg")) {
//                messageType = MessageType.IMAGE;
//            } else if (fileExtension.equals("mp4") || fileExtension.equals("avi")) {
//                messageType = MessageType.VIDEO;
//            } else {
//                messageType = MessageType.FILE;
//            }
//
//            Attachment attachment = attachmentService.saveFile(completeFile, messageType, fileName);
//
//            Message message = new Message();
//            message.setSenderId(fileMessage.getSenderId());
//            message.setConversationId(fileMessage.getConversationId());
//            message.setSenderType(fileMessage.getSenderType());
//            message.setMessageType(messageType);
//            message.setAttachment(attachment);
//            message.setSentAt(LocalDateTime.now());
//            message.setStatus(MessageStatus.SENT);
//
//            chatService.sendMessage(message);
//            messagingTemplate.convertAndSend("/topic/chat", message);
//
//            fileChunksMap.remove(fileId);
//            fileTotalChunksMap.remove(fileId);
//        }
//    }

    
    

    


    @PostMapping("/upload/print")
    public ResponseEntity<Map<String, Object>> uploadFiletoprint(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMessage") String fileMessageJson
    ) throws IOException {
        // Deserialize fileMessage from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        FileMessage fileMessage;
        try {
            fileMessage = objectMapper.readValue(fileMessageJson, FileMessage.class);
        } catch (JsonProcessingException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Bad request, Invalid fileMessage JSON");
            return ResponseEntity.badRequest().body(response);
        }

        // Save the file to the server
        File dir = new File("uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Get the file name with extension
        String originalFileName = file.getOriginalFilename(); // Get original file name with extension
        if (originalFileName == null || originalFileName.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Bad request, Invalid file name");
            return ResponseEntity.badRequest().body(response);
        }

        // Generate a unique file name if necessary
        String uniqueFileName = originalFileName;
        File uploadedFile = new File(dir, uniqueFileName);

        // Save the file
        try (FileOutputStream fos = new FileOutputStream(uploadedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Internal Server error");
            return ResponseEntity.status(500).body(response);
        }

        // Get file extension
        String fileExtension = uniqueFileName.substring(uniqueFileName.lastIndexOf(".") + 1).toLowerCase();
        MessageType messageType = determineMessageType(fileExtension); // Call method to get message type based on file extension

        
        long
        pageCount = 0;

     // If the file is a PDF, count the number of pages
     if ("pdf".equals(fileExtension)) {
         try (PDDocument document = PDDocument.load(uploadedFile)) {
             pageCount = document.getNumberOfPages();
         } catch (IOException e) {
        	 Map<String, Object> response = new HashMap<>();
             response.put("status", "error");
             response.put("message", "Bad request, Invalid file name");
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

         
         }
         
         
     }
        // Save file metadata (you can customize this part to save data to your database)
        String filePath = uploadedFile.getAbsolutePath();
        Attachment attachment = attachmentService.saveFile(
                messageType, // Determine file type based on extension
                uniqueFileName, // Use the unique file name with extension
                uploadedFile.length(), // File size
                filePath,
                pageCount
        );
        
        
        
        
        
        
        List<String> extractedPagesBase64 = new ArrayList<>(); // Declare before the if block
        if (messageType == MessageType.PDF) {
             extractedPagesBase64 = extractFirstTwoPagesAsImage(uploadedFile);
            if (extractedPagesBase64 == null || extractedPagesBase64.isEmpty()) {
                System.out.println("No pages extracted.");
            }
        }

        // Create and populate the message
        Message message = new Message();
        message.setSenderId(fileMessage.getSenderId());
        message.setConversationId(fileMessage.getConversationId());
        message.setSenderType(fileMessage.getSenderType());
        message.setMessageType(messageType); // Use message type based on file extension
        message.setAttachment(attachment);
        message.setSentAt(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);

        
        messagingTemplate.convertAndSend("/topic/chat", message);

        // Send the message
        chatService.sendMessage(message);

        // Create the response JSON
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "File uploaded and processed successfully");
        response.put("extractedPages", extractedPagesBase64);  // Base64 of the first two pages
        response.put("pageCount", pageCount  );  // Base64 of the first two pages
        response.put("attachment", attachment );  // Base64 of the first two pages

        System.out.println(response);
        return ResponseEntity.ok(response); // Return a successful response with the data
    }



    
    private List<String> extractFirstTwoPagesAsImage(File pdfFile) throws IOException {
        // Load the PDF document 
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Check the number of pages in the PDF
        int pageCount = document.getNumberOfPages();

        // Create a list to hold the Base64 strings for each page
        List<String> base64Images = new ArrayList<>();

        // If the document has at least one page, render the first page
        if (pageCount >= 1) {
            BufferedImage page1Image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB); // First page
            base64Images.add(encodeImageToBase64(page1Image)); // Encode first page
        }

        // If the document has at least two pages, render the second page
        if (pageCount >= 2) {
            BufferedImage page2Image = pdfRenderer.renderImageWithDPI(1, 300, ImageType.RGB); // Second page
            base64Images.add(encodeImageToBase64(page2Image)); // Encode second page
        }

        // Close the document
        document.close();

        return base64Images; // Return list of Base64 strings (one for each page)
    }

    private String encodeImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos); // Write image to ByteArrayOutputStream
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return Base64.getEncoder().encodeToString(imageBytes); // Convert to Base64 string
    }

    
    
    
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        return  attachmentService.downloadFile(attachmentId);
    }
    
    
    
    
   

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(@RequestParam Long userId, @RequestParam Long shopId) {
        List<Message> messages = chatService.getMessages(userId, shopId);

        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/latestMessage/{userId}")
    public ResponseEntity<Message> getLatestMessage(@PathVariable Long userId) {
        Message latestMessage = chatService.getLatestMessageByUser(userId);
        if (latestMessage != null) {
            return ResponseEntity.ok(latestMessage);
        } else {
            return ResponseEntity.noContent().build(); // Returns 204 if no message found
        }
    }


    @GetMapping("/users/{shopId}")
    public List<User> getUsersByShopId(@PathVariable Long shopId) {
        return chatService.getUsersByShopIdSorted(shopId);
    }
    
    @GetMapping("/shops/{userId}")
    public ResponseEntity<List<Shop>> getShopsByUserId(@PathVariable Long userId) {
        List<Shop> shops = chatService.getLatestShops(userId);
        return ResponseEntity.ok(shops);
    }
    
    @GetMapping("/latestMessage/{userId}/{shopId}")
    public ResponseEntity<?> getLatestMessage(@PathVariable Long userId, @PathVariable Long shopId) {
        Optional<Message> latestMessage = chatService.getLatestMessage(userId, shopId);
        return latestMessage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
    
}
