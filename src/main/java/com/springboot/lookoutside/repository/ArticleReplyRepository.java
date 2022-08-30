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
	
	@Query(value = "select ar.*, u.useNick, a.artCategory "
			+ "from lo.ArticleReply ar "			
			+ "join lo.User u on ar.useNo = u.useNo "
			+ "join lo.Article a on ar.artNo = a.artNo "
			+ "where ar.repNo = ?1",
			countQuery ="select ar.*, u.useNick, a.artCategory "
					+ "from lo.ArticleReply ar "					
					+ "join lo.User u on ar.useNo = u.useNo "
					+ "join lo.Article a on a.artNo = ar.artNo "
					+ "where ar.repNo = ?1",
			nativeQuery = true)
	ArticleReplyMapping findByRepNo2(int repNo);
	
	//게시물에 대한 댓글 조회
	@Query(value = "select ar.*, u.useNick, a.artCategory "
			+ "from lo.ArticleReply ar "			
			+ "join lo.User u on ar.useNo = u.useNo "
			+ "join lo.Article a on ar.artNo = a.artNo "
			+ "where a.artNo = ?1",
			countQuery ="select ar.*, u.useNick, a.artCategory "
					+ "from lo.ArticleReply ar "					
					+ "join lo.User u on ar.useNo = u.useNo "
					+ "join lo.Article a on a.artNo = ar.artNo "
					+ "where a.artNo = ?1",
			nativeQuery = true)
	Page<ArticleReplyMapping> findAllByArtNo(int artNo, Pageable pageable);
	
	//마이페이지 - 댓글 목록
	@Query(value = "select u.useNick, ar.*, a.artCategory "
			+ "from lo.ArticleReply ar "			
			+ "join lo.User u on ar.useNo = u.useNo "
			+ "join lo.Article a on ar.artNo = a.artNo "
			+ "where u.useNo = ?1",
			countQuery ="select u.useNick, ar.repNo, ar.repContents, ar.repCreated, a.artCategory "
					+ "from lo.ArticleReply ar "					
					+ "join lo.User u on ar.useNo = u.useNo "
					+ "join lo.Article a on a.artNo = ar.artNo "
					+ "where u.useNo = ?1",
			nativeQuery = true)
	Page<ArticleReplyMapping> findAll(int useNo, Pageable pageable);
		
	
}
