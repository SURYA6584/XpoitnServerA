package com.xpoint.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpoint.demo.models.Attachment;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long>{

	
	
}
