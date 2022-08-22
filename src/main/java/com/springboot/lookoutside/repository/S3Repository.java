package com.springboot.lookoutside.repository;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface S3Repository{

	//Amazon S3를 사용해 파일 업로드
	void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName, String dir);

	//String getSaveUrl(String path);

	//업로드한 파일 URL 가져오는 method.
	String getFileUrl(String fileName, String dir);
	
	//삭제
	void deleteFile(String fileName);

	//void deleteFile(Component fileName);
	
	
	
}
