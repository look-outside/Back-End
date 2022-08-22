package com.springboot.lookoutside.controller;

import java.io.File;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.lookoutside.dto.ResponseDto;
import com.springboot.lookoutside.dto.S3Component;
import com.springboot.lookoutside.service.ArticleImgService;
import com.springboot.lookoutside.service.ArticleService;
import com.springboot.lookoutside.service.FileUploadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/s3")
@RestController
public class FileUploadController {

	private final FileUploadService fileUploadService;
	private final ArticleService articleService;
	private final ArticleImgService articleImgService;
	
	//등록
	@PostMapping("/post")
	public ResponseDto<Integer> uploadImage(@RequestPart(value = "multipartFiles", required = false) MultipartFile[] multipartFiles, String articles) {
		
		int artNo = articleService.savePost(articles);//이미지 파일 제외 데이터 저장
		
		//이미지 파일 첨부
		if(multipartFiles != null) {
			
			for(int i = 0; i < multipartFiles.length; i++) {

				MultipartFile file = multipartFiles[i];

				fileUploadService.uploadImage(artNo, file, "images");//이미지 파일 저장

				System.out.println("게시물 올리기");
				
			}	
			
		}
		
		if(multipartFiles == null) {
			fileUploadService.nullImg(artNo);
		}
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}

	//수정
	@PutMapping("/post")
	public ResponseDto<Integer> updateImage(@PathVariable int artNo, @RequestPart MultipartFile[] multipartFiles, String articles) throws JsonMappingException, JsonProcessingException {
		
		articleService.updatePost(artNo,articles);
		articleImgService.deleteImgPost(artNo);//이미지 파일 삭제
		
		if(multipartFiles != null) {
			
			for(int i = 0; i < multipartFiles.length; i++) {
				
				MultipartFile file = multipartFiles[i];
				
				fileUploadService.updateImage(artNo, file);//이미지 파일 저장
				
				System.out.println("게시물 수정하기");
			}	
		}

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}
	
	//삭제
	@DeleteMapping("/{artNo}")
	public ResponseDto<Integer> delete(@PathVariable("artNo") int artNo, S3Component fileName) {
		
		articleService.deletePost(artNo);
		articleImgService.deleteImgPost(artNo);
		fileUploadService.deleteImgPost(fileName);
		
		System.out.println("게시물 삭제하기");
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	
	}
}