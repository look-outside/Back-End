package com.springboot.lookoutside.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
//import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.springboot.lookoutside.domain.ArticleImg;
import com.springboot.lookoutside.dto.S3Component;
//import com.springboot.lookoutside.dto.S3Component;
import com.springboot.lookoutside.repository.ArticleImgRepository;
import com.springboot.lookoutside.repository.ArticleRepository;
import com.springboot.lookoutside.repository.S3Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FileUploadService {
	
	private final S3Repository s3Repository;
	
	private final ArticleImgRepository articleImgRepository;
	
	private final ArticleRepository articleRepository;
	
	@Value("${cloud.aws.s3.dir}")
    private String dir;
	
	//private final S3Component component;
	private final AmazonS3 amazonS3;
	
	public String nullImg(int artNo) {
		
		ArticleImg articleImg = new ArticleImg();
		
		articleImg.setArtNo(artNo);
		articleImg.setImgNo(1);
		articleImgRepository.save(articleImg);
		
		return "";
	}
	
	//게시물 등록 페이지
	public String uploadImage(int artNo, MultipartFile file, String dir) {
		
		//이미지 파일 등록
		ArticleImg articleImg = new ArticleImg();
		
		String uploadPath  = "D:\\images";
		
		String fileName = createFileName(file.getOriginalFilename());//imgOrigin
		String saveImgName = (new Date().getTime()) + "" + (file.getOriginalFilename()); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1); // ex) jpg
		fileName = fileName.substring(0, fileName.lastIndexOf(".")); // ex) 파일
		
		String filePath = uploadPath + "\\" + saveImgName;
		//String uploadPath  = System.getProperty("user.home") + "\\" + "images";
		//String filePath = uploadPath + "\\" + saveImgName;
		
		File fileSave = new File(uploadPath, saveImgName);
 		
 		if(!fileSave.exists()) { 
 			fileSave.mkdirs();
 		}
 		
 		try {
 			file.transferTo(fileSave);
 		} catch (IllegalStateException e) {
 			
 			e.printStackTrace();
 		} catch (IOException e) {
 			
 			e.printStackTrace();
 		}
		
 		articleImg.setImgSave(saveImgName);
 		articleImg.setImgOrigin(fileName);
 		articleImg.setImgPath(filePath);	
 		
 		articleImg.setArtNo(artNo);
 		
 		int imgNo = articleImgRepository.findImgNo(artNo);
 		System.out.println(imgNo);
 		articleImg.setImgNo(imgNo + 1);	
 		
		articleImgRepository.save(articleImg);
		
		//-------------------------------------------------------------------------
		
		//s3서버 등록
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		
		try (InputStream inputStream = file.getInputStream()) {
			
			s3Repository.uploadFile(inputStream, objectMetadata, fileName, dir);	
			//amazonS3.putObject(bucketName, key, input, metadata)
			
		} catch (IOException e) {
			
			throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다 (%s)", file.getOriginalFilename()));
		}
		return s3Repository.getFileUrl(fileName, dir);
	}

	private String createFileName(String originalFileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
	}

	private String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다", fileName));
		}
	}
	
	//삭제
	public String deleteImgPost(S3Component s3Component) {
		
		if(!amazonS3.doesObjectExist(s3Component.getBucket(), s3Component.getKey())) {
			throw new AmazonS3Exception("Object" + s3Component.getKey() + "does not exist!");
		}
		amazonS3.deleteObject(s3Component.getBucket(), s3Component.getKey());
	
		return "1";//이미피 파일 삭제 완료
	}

	//수정
	public String updateImage(int artNo, MultipartFile file) {
		//이미지 파일 등록
		ArticleImg articleImg = new ArticleImg();
		
		String uploadPath  = "D:\\images";
		
		String fileName = createFileName(file.getOriginalFilename());//imgOrigin
		String saveImgName = (new Date().getTime()) + "" + (file.getOriginalFilename()); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1); // ex) jpg
		fileName = fileName.substring(0, fileName.lastIndexOf(".")); // ex) 파일
		
		String filePath = uploadPath + "\\" + saveImgName;
		//String uploadPath  = System.getProperty("user.home") + "\\" + "images";
		//String filePath = uploadPath + "\\" + saveImgName;
		
		File fileSave = new File(uploadPath, saveImgName);
 		
 		if(!fileSave.exists()) { 
 			fileSave.mkdirs();
 		}
 		
 		try {
 			file.transferTo(fileSave);
 		} catch (IllegalStateException e) {
 			
 			e.printStackTrace();
 		} catch (IOException e) {
 			
 			e.printStackTrace();
 		}
		
 		articleImg.setImgSave(saveImgName);
 		articleImg.setImgOrigin(fileName);
 		articleImg.setImgPath(filePath);	
 		
 		articleImg.setArtNo(artNo);
 		
 		int imgNo = articleImgRepository.findImgNo(artNo);
 		System.out.println(imgNo);
 		articleImg.setImgNo(imgNo + 1);	
 		
		articleImgRepository.save(articleImg);
		
		//-------------------------------------------------------------------------
		
		//s3서버 등록
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		
		try (InputStream inputStream = file.getInputStream()) {
			
			s3Repository.uploadFile(inputStream, objectMetadata, fileName, dir);	
			//amazonS3.putObject(bucketName, key, input, metadata)
			
		} catch (IOException e) {
			
			throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다 (%s)", file.getOriginalFilename()));
		}
		return s3Repository.getFileUrl(fileName, dir);
	}
	

}
