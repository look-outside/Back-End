package com.springboot.lookoutside.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface ArticleDetail {

	int getArtNo();
	
	int getUseNo();
	
	String getUseNick();
	
	int getArtCategory();
	
	int getArtWselect();
	
	String getRegNo();
	
	String getArtSubject();
	
	String getArtContents();
	
	
	@JsonFormat(pattern = "YY.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
	Timestamp getArtCreated();
	
}
