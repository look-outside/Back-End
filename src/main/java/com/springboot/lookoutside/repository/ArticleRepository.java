package com.springboot.lookoutside.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.lookoutside.domain.Article;
import com.springboot.lookoutside.dto.ArticleDetail;
import com.springboot.lookoutside.dto.ArticleMapping;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>, JpaSpecificationExecutor<Article>{
	
	Optional<Article> findByArtNo(int artNo);
	
	@Query(value = "select a.*, u.useNick "
			+ "from lo.Article a "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "where a.artNo = ?1 ",
			countQuery ="select a.*, u.useNick "
					+ "from lo.Article a "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "where a.artNo = ?1 ",
			nativeQuery = true)
	Optional<ArticleDetail> findDetail(int artNo);

//	Page<Article> findByArtSubjectContaining(Pageable pageable,String keyword);//게시물 제목으로 검색
    Page<Article> findByArtSubjectContainingAndArtCategory(Pageable pageable,String keyword, Optional<Integer> artCategory);
	Page<Article> findByArtContentsContainingAndArtCategory(Pageable pageable,String keyword, int artCategory);
	
	//전체 게시물 목록 조회 (관리자페이지)
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where i.imgNo = 1",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where i.imgNo = 1",
			nativeQuery = true)
	Page<ArticleMapping> findList(Pageable pageable);
	
	//카테고리 별 게시물 목록 조회 (관리자페이지)
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where a.artCategory = ?1 "
			+ "and i.imgNo = 1",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where a.artCategory = ?1 "
					+ "and i.imgNo = 1",
			nativeQuery = true)
	Page<ArticleMapping> findAllByArtCategory(int artCategory, Pageable pageable);
	
	//카테고리, 지역 별 게시물 조회
	//Page<Article> findAllByArtCategoryAndRegNoStartingWith(int artCategory, String regNo, Pageable pageable);
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where a.artCategory = ?1 "
			+ "and a.regNo like ?2% "
			+ "and i.imgNo = 1",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where a.artCategory = ?1 "
					+ "and a.regNo like ?2% "
					+ "and i.imgNo = 1",
			nativeQuery = true)
	Page<ArticleMapping> findAllByArtCategoryAndRegNoStartingWith(int artCategory, String regNo, Pageable pageable);
	
	//내가 쓴 게시물 조회(마이페이지)
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where u.useNo = ?1 "
			+ "and i.imgNo = 1",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave, i.imgOrigin, i.imgPath "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where u.useNo = ?1 "
					+ "and i.imgNo = 1",
			nativeQuery = true)
	Page<ArticleMapping> findAllBy(int useNo, Pageable pageable);
	
	//테스트 검색
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where a.artCategory = :artCategory "
			+ "and a.artSubject like %:keyword%",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where a.artCategory = :artCategory "
					+ "and a.artSubject like %:keyword%",
			nativeQuery = true)
	Page<ArticleMapping> searchSubject(@Param("artCategory")int artCategory, @Param("keyword")Optional<String> keyword, Pageable pageable);
	
	//테스트 검색
	@Query(value = "select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave "
			+ "from lo.Article a "
			+ "join lo.Region r on a.regNo = r.regNo "
			+ "join lo.User u on a.useNo = u.useNo "
			+ "join lo.ArticleImg i on a.artNo = i.artNo "
			+ "where a.artCategory = :artCategory "
			+ "and a.artContents like %:keyword%",
			countQuery ="select a.*, u.useNick, r.regAddr1, r.regAddr2, i.imgSave "
					+ "from lo.Article a "
					+ "join lo.Region r on a.regNo = r.regNo "
					+ "join lo.User u on a.useNo = u.useNo "
					+ "join lo.ArticleImg i on a.artNo = i.artNo "
					+ "where a.artCategory = :artCategory "
					+ "and a.artContents like %:keyword%",
			nativeQuery = true)
	Page<ArticleMapping> searchContents(@Param("artCategory")int artCategory, @Param("keyword")Optional<String> keyword, Pageable pageable);
}
