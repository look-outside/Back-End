package com.springboot.lookoutside.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.lookoutside.domain.Article;
import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.dto.ArticleMapping;
import com.springboot.lookoutside.dto.ResponseDto;
import com.springboot.lookoutside.oauth.entity.RoleType;
import com.springboot.lookoutside.service.ManagerService;

@RestController
@RequestMapping("/manager")
public class ManagerController {

	@Autowired 
	private ManagerService managerService;

	//회원 목록 전체 조회
	@GetMapping("/user")
	public ResponseDto<Map<String, Object>> userList(@PageableDefault(size=5,sort="useCreated",direction = Sort.Direction.DESC ) RoleType Role, Pageable pageable) { //가입 최근순 조회 5개		
		Map<String, Object> userList = managerService.userList(Role, pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(),userList);
	}

	//회원 권한 수정
	@PutMapping("/user/{useNo}")
	public ResponseDto<String> changeRole(@PathVariable int useNo) {
		String role = managerService.changeRole(useNo);
		return new ResponseDto<String>(HttpStatus.OK.value(),role);
	}

	//회원 선택 삭제
	@DeleteMapping("/user/{useNos}")
	public ResponseDto<String> deleteCheckUser(@PathVariable int[] useNos) {
		String result = managerService.deleteCheckUser(useNos);
		return new ResponseDto<String>(HttpStatus.OK.value(), result);
	}

	//게시물 전체 목록 조회
	@GetMapping("/article")
	public ResponseDto<Map<String, Object>> articleList(@PageableDefault(size=5, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = managerService.articleList(pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}

	//게시물 카테고리별 목록 조회
	@GetMapping("/article/{artCategory}")
	public ResponseDto<Map<String, Object>> articleListCate(@PathVariable int artCategory, @PageableDefault(size=5, sort="artNo", direction = Sort.Direction.DESC) Pageable pageable){
		Map<String, Object> articleList = managerService.articleListCate(artCategory ,pageable);
		return new ResponseDto<Map<String, Object>>(HttpStatus.OK.value(), articleList);
	}

	//게시물 선택 삭제
	@DeleteMapping("/article/{artNos}")
	public ResponseDto<String> deleteCheckArticle(@PathVariable int[] artNos) {
		String result = managerService.deleteCheckArticle(artNos);
		return new ResponseDto<String>(HttpStatus.OK.value(), result);
	}
}
