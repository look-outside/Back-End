package com.springboot.lookoutside.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.lookoutside.domain.Article;
import com.springboot.lookoutside.domain.ArticleImg;
import com.springboot.lookoutside.repository.ArticleImgRepository;
import com.springboot.lookoutside.repository.ArticleRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArticleImgService {
	
	@Autowired
	private ArticleImgRepository articleImgRepository;
	
	@Transactional
	public String nullImg(int artNo) {
		
		ArticleImg articleImg = new ArticleImg();
		
		articleImg.setArtNo(artNo);
		articleImg.setImgNo(1);
		articleImgRepository.save(articleImg);
		
		return "";
	}
	
	@Transactional
	public String saveImg(int artNo, String key, String path, String OriginName) {
		
		ArticleImg articleImg = new ArticleImg();
		
		System.out.println(key);
		String imgSave =key.replace("images/", ""); 
		System.out.println("Test" + imgSave); 
		
		articleImg.setImgSave(imgSave);
		articleImg.setImgOrigin(OriginName); 
		articleImg.setImgPath(path);			
		articleImg.setArtNo(artNo);
		
		int imgNo = articleImgRepository.findImgNo(artNo);
		System.out.println(imgNo);
		articleImg.setImgNo(imgNo + 1);	
		
		articleImgRepository.save(articleImg);
		
		return "1";//게시물 이미지 첨부 완료
	}

	//게시물 수정
	public String updateImg(int artNo, MultipartFile file) {
		
		ArticleImg articleImg = new ArticleImg();
		
		String uploadPath  = "D:\\images";
		
		String imgOriName = file.getOriginalFilename(); //filename.jpg	
		
		String saveImgName = (new Date().getTime()) + "" + (file.getOriginalFilename()); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
		
		String fileExtension = imgOriName.substring(imgOriName.lastIndexOf(".") + 1); // ex) jpg
		imgOriName = imgOriName.substring(0, imgOriName.lastIndexOf(".")); // ex) 파일
		//long fileSize = file.getSize(); // 파일 사이즈
		String filePath = uploadPath + "\\" + saveImgName;
		
		
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
		
		articleImg = new ArticleImg();
		
		articleImg.setImgSave(saveImgName);
		articleImg.setImgOrigin(imgOriName);
		articleImg.setImgPath(filePath);	
		
		articleImg.setArtNo(artNo);
		
		int imgNo = articleImgRepository.findImgNo(artNo);
		System.out.println(imgNo);
		articleImg.setImgNo(imgNo + 1);	
		
		articleImgRepository.save(articleImg);
		
		return "1";//게시물 이미지 첨부 완료
	}
	
	//이미지 파일 삭제
	@Transactional
	public String deleteImgPost(int artNo) {
	
		articleImgRepository.deleteByArtNo(artNo);
	
		return "1";//이미피 파일 삭제 완료
	}
	
	@Transactional
	public String testImg(int artNo, String file) {
		
		ArticleImg articleImg = new ArticleImg() ;
		try {
			articleImg = new ObjectMapper().readValue(file, ArticleImg.class);
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		
		String imgSave = articleImg.getImgPath().replace("https://elasticbeanstalk-us-west-1-616077318706.s3.us-west-1.amazonaws.com/images/", ""); 
		String originName = articleImg.getImgOrigin();
		String path = articleImg.getImgPath();
		
		articleImg.setImgSave(imgSave);
		articleImg.setImgOrigin(originName); 
		articleImg.setImgPath(path);			
		articleImg.setArtNo(artNo);
		
		int imgNo = articleImgRepository.findImgNo(artNo);
		System.out.println(imgNo);
		articleImg.setImgNo(imgNo + 1);	
		
		articleImgRepository.save(articleImg);
		
		return "1";//게시물 이미지 첨부 완료
	}
	
}