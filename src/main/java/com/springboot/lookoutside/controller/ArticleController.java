package com.springboot.lookoutside.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.lookoutside.domain.Article;
import com.springboot.lookoutside.domain.ArticleImg;
import com.springboot.lookoutside.domain.ArticleReply;
import com.springboot.lookoutside.domain.AwsS3;
import com.springboot.lookoutside.dto.ArticleDto;
import com.springboot.lookoutside.dto.ArticleMapping;
import com.springboot.lookoutside.dto.ArticleReplyMapping;
import com.springboot.lookoutside.dto.ResponseDto;
import com.springboot.lookoutside.service.ArticleImgService;
import com.springboot.lookoutside.service.ArticleReplyService;
import com.springboot.lookoutside.service.ArticleService;
import com.springboot.lookoutside.service.AwsS3Service;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/article")
@AllArgsConstructor
public class ArticleController {
	
	@Autowired
	@Resource
	private AwsS3Service awsS3Service;
	
	@Autowired
	@Resource
	private ArticleService articleService;

	@Autowired
	@Resource
	private ArticleReplyService articleReplyService;

	@Autowired
	@Resource
	private ArticleImgService articleImgService;


	//내가 쓴 게시물 목록
	@GetMapping("/list/{useNo}")
	public ResponseDto<Map<String, Object>> articleList(@PathVariable int useNo, @PageableDefault(size=5, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = articleService.articleList(useNo, pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}

	//마이페이지 - 댓글목록
	@GetMapping("/reply/{useNo}")
	public ResponseDto<Map<String, Object>> replyListMypage(@PathVariable int useNo, @PageableDefault(size=5, sort="repNo", direction = Sort.Direction.DESC) Pageable pageable){

		Map<String, Object> replyListMypage = articleReplyService.replyListMypage(useNo, pageable);

		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), replyListMypage);		
	}

	//게시물 작성 + 이미지 파일 첨부
	@PostMapping("/post")
	public ResponseDto<Integer> upload(MultipartFile[] multipartFiles, String articles) throws Exception{

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
			articleImgService.nullImg(artNo);
		}

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);

	}

	//게시물 수정 페이지
	@PutMapping("/{artNo}")
	public ResponseDto<String> update(@PathVariable int artNo, MultipartFile[] multipartFiles, String articles) throws JsonMappingException, JsonProcessingException {

		String update = articleService.updatePost(artNo,articles);

		articleImgService.deleteImgPost(artNo);//이미지 파일 삭제

		if(multipartFiles != null) {

			for(int i = 0; i < multipartFiles.length; i++) {

				MultipartFile file = multipartFiles[i];

				articleImgService.updateImg(artNo,file);//이미지 파일 저장\

				System.out.println("게시물 수정하기");
			}	

		}

		return new ResponseDto<String>(HttpStatus.OK.value(), update);
	}

	//게시물 상세 조회 페이지
	@GetMapping("/{artNo}")
	public ResponseDto<Map<String, Object>> detail(@PathVariable int artNo, @PageableDefault(size=5, sort="repCreated", direction = Sort.Direction.DESC) Pageable pageable) {

		Map<String, Object> detail = articleService.detailPost(artNo, pageable);
		System.out.println("게시물 상세 페이지입니다.");

		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), detail);
	}

	//삭제
	@DeleteMapping("/{artNo}")
	public ResponseDto<String> delete(@PathVariable("artNo") int artNo) {

		String deleteArt = articleService.deletePost(artNo);
		System.out.println("게시물 삭제하기");
		return new ResponseDto<String>(HttpStatus.OK.value(), deleteArt);
	}


	//검색
	@GetMapping("/search")
	public ResponseDto<Page<Article>> search(@PageableDefault(size=3, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable, String keyword, Optional<Integer> artCategory) {
		Page<Article> articleList = articleService.searchPosts(pageable, keyword, artCategory);
		return new ResponseDto<Page<Article>>(HttpStatus.OK.value(), articleList);

	}

	//게시물 카테고리,지역별 조회
	@GetMapping("/list/{artCategory}/{regNo}")
	public ResponseDto<Map<String, Object>> articleListCate(@PathVariable int artCategory, @PathVariable String regNo, @PageableDefault(size=12, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = articleService.articleListCateRegNo(artCategory, regNo, pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}


	//댓글 등록
	@PostMapping("/reply")
	public ResponseDto<ArticleReplyMapping> saveReply(@RequestBody ArticleReply articleReply) {

		ArticleReplyMapping saveReply = articleReplyService.saveReply(articleReply);
		System.out.println("controller : 댓글 쓰기");

		return new ResponseDto<ArticleReplyMapping>(HttpStatus.OK.value(), saveReply);

	}

	//댓글 수정
	@PutMapping("/reply/{repNo}")
	public ResponseDto<ArticleReplyMapping> updateReply(@PathVariable int repNo, @RequestBody ArticleReply articleReply) {

		ArticleReplyMapping updateReply = articleReplyService.updateReply(repNo, articleReply);
		System.out.println("controller : 댓글 수정하기");

		return new ResponseDto<ArticleReplyMapping>(HttpStatus.OK.value(), updateReply);

	}

	//댓글 삭제
	@DeleteMapping("/reply/{repNo}")
	public ResponseDto<Integer> deleteReply (@PathVariable int repNo) {

		articleReplyService.deleteReply(repNo);
		System.out.println("controller : 댓글 삭제하기");

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);

	}

	//댓글 목록
	@GetMapping("/replylist/{artNo}")
	public ResponseDto<Map<String, Object>> replyList (@PathVariable int artNo, @PageableDefault(size=5, sort="repCreated", direction = Sort.Direction.DESC) Pageable pageable) {

		Map<String, Object> replyList = articleReplyService.replyList(artNo, pageable);
		System.out.println("controller : 댓글 목록 불러오기");

		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), replyList);

	}

	//오늘의 x 카테고리별 목록 조회 ( 모든지역 최근순)
	@GetMapping("/category/{artCategory}")
	public ResponseDto<Map<String, Object>> articleCategory(@PathVariable int artCategory, @PageableDefault(size=4, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = articleService.articleCategory(artCategory, pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}

	//오늘의 x 카테고리별 목록 조회 ( 모든지역 최근순)
	@GetMapping("/searchtest")
	public ResponseDto<Map<String, Object>> searchtest(int artCategory, String type, Optional<String> keyword, @PageableDefault(size=4, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = articleService.search(artCategory, type, keyword, pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}
	
	//게시물 작성 + 이미지 파일 첨부
	@PostMapping("/testpost")
	public ResponseDto<Integer> testpost(String[] multipartFiles, String articles) throws Exception{

		int artNo = articleService.savePost(articles);//이미지 파일 제외 데이터 저장

		if(multipartFiles != null) {
			
			for(int i = 0; i < multipartFiles.length; i++) {

				String file = multipartFiles[i];
				
				ArticleImg articleImg = new ArticleImg() ;
				try {
					articleImg = new ObjectMapper().readValue(file, ArticleImg.class);
				} catch (JsonMappingException e) {

					e.printStackTrace();
				} catch (JsonProcessingException e) {

					e.printStackTrace();
				}
				
				String sourceKey = articleImg.getImgPath().replace("https://elasticbeanstalk-us-west-1-616077318706.s3.us-west-1.amazonaws.com/", ""); 
				String destinationKey = sourceKey.replace("temporary", "images");
				awsS3Service.moveTo(sourceKey, destinationKey);
				
				String imgSave = destinationKey.replace("https://elasticbeanstalk-us-west-1-616077318706.s3.us-west-1.amazonaws.com/images/", ""); 
				String originName = articleImg.getImgOrigin();
				String path = destinationKey;
				
				articleImgService.testImg(artNo, imgSave, originName, path);//이미지 파일 data 저장
			
			}
			
		}

		if(multipartFiles == null || multipartFiles.equals(null)) {
			articleImgService.nullImg(artNo);
		}

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);

	}
	
	//파일만 업로드
	@PostMapping("/testupload")
    public ResponseDto<List<String>> testupload(@RequestPart("multipartFiles") MultipartFile[] multipartFiles) throws IOException {

		List<String> path = new ArrayList<String>();
		
		for(int i = 0; i < multipartFiles.length; i++) {

			MultipartFile file = multipartFiles[i];

			AwsS3 S3 = awsS3Service.upload(file,"temporary");//이미지 파일 저장
			
			path.add(S3.getPath());
			
		}
		
		return new ResponseDto<List<String>>(HttpStatus.OK.value(), path);
		
    }
	
	//파일만 업로드
	@PostMapping("/testmove")
    public ResponseDto<List<String>> testmove(@RequestPart("multipartFiles") MultipartFile[] multipartFiles) throws IOException {

		List<String> path = new ArrayList<String>();
		
		for(int i = 0; i < multipartFiles.length; i++) {

			MultipartFile file = multipartFiles[i];

			AwsS3 S3 = awsS3Service.upload(file,"temporary");//이미지 파일 저장
			
			String sourceKey = S3.getPath().replace("https://elasticbeanstalk-us-west-1-616077318706.s3.us-west-1.amazonaws.com/", ""); 
			String destinationKey = sourceKey.replace("temporary", "images");
			awsS3Service.moveTo(sourceKey, destinationKey);
			
			path.add(S3.getPath());
			
		}
		
		return new ResponseDto<List<String>>(HttpStatus.OK.value(), path);
		
    }
	
}
