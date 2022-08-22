package com.springboot.lookoutside.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Component
public class S3Component {

	private String bucket;
	private String dir;//filePath
	private String key;
	private String path;
	
	public S3Component() {
		
	}
	
	@Builder
	public S3Component(String  bucket) {
		
		this.key = key;
		this.path = path;
	}
}
