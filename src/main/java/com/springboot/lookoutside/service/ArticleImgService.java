package com.springboot.lookoutside.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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

	//이미지 파일 삭제
	@Transactional
	public String deleteImgPost(int artNo) {

		articleImgRepository.deleteByArtNo(artNo);

		return "1";//이미피 파일 삭제 완료
	}

	@Transactional
	public String saveImg(int artNo, String imgSave, String path) {

		ArticleImg articleImg = new ArticleImg() ;

		articleImg.setImgSave(imgSave);
		//articleImg.setImgOrigin(originName); 
		articleImg.setImgPath(path);			
		articleImg.setArtNo(artNo);

		int imgNo = articleImgRepository.findImgNo(artNo);
		System.out.println(imgNo);
		articleImg.setImgNo(imgNo + 1);	

		articleImgRepository.save(articleImg);

		return "1";//게시물 이미지 첨부 완료
	}

	//이미지 파일 key 찾기
	@Transactional
	public List<ArticleImg> searchKey(int artNo) {
		
		return articleImgRepository.findAllByArtNo(artNo);
		
	}
	
}