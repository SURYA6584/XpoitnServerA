package com.xpoint.demo.ServiceImp;

import com.xpoint.demo.Service.AttachmentService;
import com.xpoint.demo.enumstore.MessageType;
import com.xpoint.demo.models.Attachment;
import com.xpoint.demo.repository.AttachmentRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.pdfbox.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ContentDisposition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;


@Service
public class AttachmentServiceImp implements AttachmentService {


    @Autowired
    private AttachmentRepository attachmentRepository;

  

    @Override
    public Attachment saveFile(MessageType messageType, String originalFileName, long fileSize ,String filePath ,Long pagecount) throws IOException {
        // Create and populate the Attachment entity
        Attachment attachment = new Attachment();
        attachment.setMessageType(messageType);
        attachment.setFileUrl(filePath); // Construct the file path for retrieval
        attachment.setFileSize(fileSize);
        attachment.setUploadedAt(LocalDateTime.now());
  attachment.setFileName(originalFileName);
  attachment.setPageCount(pagecount);
        // Save the attachment metadata to the database
        return attachmentRepository.save(attachment);
    }

   
    
 public ResponseEntity<Resource> downloadFile( Long attachmentId) {
        try {
            // Retrieve the Attachment entity from the database by attachmentId
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found with ID: " + attachmentId));

            // Get the file path and name
            String filePath = attachment.getFileUrl();
            String fileName = attachment.getFileName();

            // Load the file as a resource
            Resource resource = loadFileAsResource(filePath);

         // Determine the file's MIME type using Apache Tika
            Tika tika = new Tika();
            String mimeType = tika.detect(new File(filePath));
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback MIME type
            }

            // Set the Content-Disposition header to prompt download with the correct file name
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName)
                    .build();

            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving file with attachmentId: " + attachmentId, e);
        }
    }

    private Resource loadFileAsResource(String filePath) throws MalformedURLException {
        Resource resource = new UrlResource("file:" + filePath);
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or is not readable: " + filePath);
        }
        return resource;
    }
}
