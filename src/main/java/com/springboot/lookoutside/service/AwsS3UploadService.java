package com.springboot.lookoutside.service;

import java.io.File;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springboot.lookoutside.dto.S3Component;
import com.springboot.lookoutside.repository.S3Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AwsS3UploadService implements S3Repository {

	private final AmazonS3 amazonS3;
	//AWS S3 설정 파일
	private final S3Component component;

	//@Value("${cloud.aws.s3.bucket}")
	//private String bucket;

    //@Value("${cloud.aws.s3.dir}")
    //private String dir;
	    
	@Override
	public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName, String dir) {
		dir = component.getDir();
		amazonS3.putObject(new PutObjectRequest(component.getBucket(), dir + fileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	@Override
	public String getFileUrl(String fileName, String dir) {
		dir = component.getDir();
		return amazonS3.getUrl(component.getBucket(), dir + fileName).toString();
	}

	@Override
	public void deleteFile(String fileName) {
		amazonS3.deleteObject(component.getBucket(), fileName);
		
	}


	

	
	
	


}
