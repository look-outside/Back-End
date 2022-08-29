package com.springboot.lookoutside.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.lookoutside.domain.ArticleImg;
import com.springboot.lookoutside.domain.AwsS3;
import com.springboot.lookoutside.dto.ResponseDto;
import com.springboot.lookoutside.repository.ArticleImgRepository;
import com.springboot.lookoutside.service.ArticleImgService;
import com.springboot.lookoutside.service.ArticleService;
import com.springboot.lookoutside.service.AwsS3Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3test")
@RequiredArgsConstructor
public class AwsS3Controller {

	private final AwsS3Service awsS3Service;
	private final ArticleService articleService;
	private final ArticleImgService articleImgService;
	private final ArticleImgRepository articleImgRepository;
	
	//게시물 작성 페이지
	@GetMapping("/post")
	public String upload() {
		System.out.println("글 작성 페이지");
		return "article/upload.html";
	}
	
    @PostMapping("/post")
    public ResponseDto<Integer> upload(@RequestPart("multipartFiles") MultipartFile[] multipartFiles, String articles) throws IOException {
        
    	int artNo = articleService.savePost(articles);//이미지 파일 제외 데이터 저장
    	
    	if(multipartFiles != null) {
			
			for(int i = 0; i < multipartFiles.length; i++) {

				MultipartFile file = multipartFiles[i];

				AwsS3 S3 = awsS3Service.upload(file,"images");//이미지 파일 저장
				
				articleImgService.saveImg(artNo, S3.getKey(), S3.getPath(),  multipartFiles[i].getOriginalFilename());//이미지 파일 data 저장

				System.out.println("게시물 올리기");					
			}				
		}
		
		if(multipartFiles == null) {  
			//awsS3Service.nullImg(multipartFiles,"images");
			articleImgService.nullImg(artNo);
		}		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
    }

    @DeleteMapping("/{artNo}")
    public ResponseDto<Integer> remove(@PathVariable("artNo") int artNo) {
        
    	awsS3Service.remove(artNo);
    	articleService.deletePost(artNo); 
		articleImgService.deleteImgPost(artNo);
    	
    	System.out.println("게시물 삭제하기");
        return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
    }
    
    @PutMapping("/post/{artNo}")
    public ResponseDto<Integer> update(@PathVariable("artNo")int artNo, @RequestPart("multipartFiles") MultipartFile[] multipartFiles, String articles) throws IOException {
    	
		if(multipartFiles != null) {
			
			for(int i = 0; i < multipartFiles.length; i++) {

				MultipartFile file = multipartFiles[i];

				AwsS3 S3 = awsS3Service.upload(file,"images");//이미지 파일 저장
				
				articleImgService.saveImg(artNo, S3.getKey(), S3.getPath(),  multipartFiles[i].getOriginalFilename());//이미지 파일 data 저장

				System.out.println("게시물 올리기");					
			}				
		}
		
		articleService.updatePost(artNo,articles);
		articleImgService.deleteImgPost(artNo);//이미지 파일 삭제
		
		if(multipartFiles == null) {
			articleImgService.nullImg(artNo);
		}		
		
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
    }
   
}
