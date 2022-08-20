package com.springboot.lookoutside.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.springboot.lookoutside.domain.ArticleReply;
import com.springboot.lookoutside.dto.ArticleReplyMapping;
import com.springboot.lookoutside.repository.ArticleReplyRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArticleReplyService {

	@Autowired
	private ArticleReplyRepository articleReplyRepository;
	
	//댓긇 등록
	@Transactional
	public String saveReply(ArticleReply articleReply) {
		
		articleReplyRepository.save(articleReply);
		
		return "1";
	}

	//댓글 수정
	public String updateReply(int repNo, ArticleReply articleReply) {
		
		articleReply =  articleReplyRepository.findByRepNo(repNo).orElseThrow(() -> { 
			
			return new IllegalArgumentException("0");
		});
		
		articleReply.setRepContents(articleReply.getRepContents());
		
		return "1";
	}

	//댓글 삭제
	public String deleteReply(int repNo) {
		
		articleReplyRepository.findByRepNo(repNo).orElseThrow(() -> { 
			return new IllegalArgumentException("0");
		});

		articleReplyRepository.deleteById(repNo);
		
		return "1";
	}

	//댓글 목록
	public List<ArticleReply> replyList(int artNo) {
		
		List<ArticleReply> replyList = articleReplyRepository.findAllByArtNo(artNo);
		
		return replyList;
	}

	//마이페이지 - 댓글 목록
		public Map<String, Object> replyListMypage(int useNo, Pageable pageable) {
			
			Page<ArticleReplyMapping> replyListMypage = articleReplyRepository.findAll(useNo, pageable);
			
			int numberOfElements = replyListMypage.getNumberOfElements();
			long totalElements = replyListMypage.getTotalElements();
			int number = replyListMypage.getNumber();
			int totalPages = replyListMypage.getTotalPages();
			int size = replyListMypage.getSize();
			
			Map<String, Object> pageAble = new HashMap<String, Object>();
			
			pageAble.put("numberOfElements", numberOfElements);
			pageAble.put("totalElements", totalElements);
			pageAble.put("number", number);
			pageAble.put("totalPages", totalPages);
			pageAble.put("size", size);
			pageAble.put("offset", replyListMypage.getPageable().getOffset());
			
			Map<String, Object> articleReply = new HashMap<String, Object>();
			
			articleReply.put("list", replyListMypage.getContent());
			articleReply.put("pageable", pageAble);
			
			return articleReply;
		}
	
	
}
