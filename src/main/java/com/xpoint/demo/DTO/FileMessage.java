package com.xpoint.demo.DTO;

import com.xpoint.demo.enumstore.SenderType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMessage {
	 private String fileId;          // Unique identifier for the file upload
	    private String fileContent;     // Base64-encoded chunk of the file
	    private int chunkIndex;         // Index of the current chunk
	    private int totalChunks;        // Total number of chunks
	    private Long senderId;        // Sender ID
	    private Long conversationId;  // Conversation ID
	    private String fileType;  
	    private String fileName;
	    private SenderType senderType;// File type (extension, e.g., PDF, JPEG)
       private Long pageCount;
	    
	    // ...

}
