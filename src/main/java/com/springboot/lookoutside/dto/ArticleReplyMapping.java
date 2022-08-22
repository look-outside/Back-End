package com.springboot.lookoutside.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface ArticleReplyMapping {

	//int getArtNo();
	
	//int getUseNo();
	
	int getRepNo();
	
	String getUseNick();
	
	String getRepContents();
	
	@JsonFormat(pattern = "YY.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
	Timestamp getRepCreated();
	
}
