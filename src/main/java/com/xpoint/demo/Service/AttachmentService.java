package com.xpoint.demo.Service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.xpoint.demo.enumstore.FileType;
import com.xpoint.demo.enumstore.MessageType;
import com.xpoint.demo.models.Attachment;

public interface AttachmentService {


    public Attachment saveFile(MessageType messageType, String originalFileName, long fileSize ,String filePath ,Long pagecount) throws IOException ;
    public ResponseEntity<Resource> downloadFile(Long attachemntId);


}
