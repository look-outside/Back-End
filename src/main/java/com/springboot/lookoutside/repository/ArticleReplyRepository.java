package com.springboot.lookoutside.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.lookoutside.domain.ArticleReply;
import com.springboot.lookoutside.dto.ArticleReplyMapping;

@Repository
public interface ArticleReplyRepository extends JpaRepository<ArticleReply, Integer> {

	Optional<ArticleReply> findByRepNo(int repNo);
	
	//게시물에 대한 댓글 조회
	List<ArticleReply> findAllByArtNo(int artNo);
	
	//마이페이지 - 댓글 목록
		@Query(value = "select ar.repNo, ar.repContents, ar.repCreated "
				+ "from lo.ArticleReply ar "			
				+ "join lo.User u on ar.useNo = u.useNo "
				+ "join lo.Article a on ar.artNo = a.artNo "
				+ "where u.useNo = ?1",
				countQuery ="select ar.repNo, ar.repContents, ar.repCreated "
						+ "from lo.ArticleReply ar "					
						+ "join lo.User u on ar.useNo = u.useNo "
						+ "join lo.Article a on a.artNo = ar.artNo "
						+ "where u.useNo = ?1",
				nativeQuery = true)
		Page<ArticleReplyMapping> findAll(int useNo, Pageable pageable);
}
