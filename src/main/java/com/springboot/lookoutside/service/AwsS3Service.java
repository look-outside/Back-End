package com.springboot.lookoutside.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springboot.lookoutside.domain.ArticleImg;
import com.springboot.lookoutside.domain.AwsS3;
import com.springboot.lookoutside.repository.ArticleImgRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3 amazonS3;
    private final ArticleImgService articleImgService;
    private final ArticleImgRepository articleImgRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //게시물 이미지 등록
    public AwsS3 upload(MultipartFile multipartFiles, String dirName) throws IOException {
        File file = convertMultipartFileToFile(multipartFiles)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert fail"));
      		
        return upload(file, dirName);
    }

	private AwsS3 upload(File file, String dirName) {
        String key = randomFileName(file, dirName);
        String path = putS3(file, key);
        removeFile(file);

        return AwsS3
                .builder()
                .key(key)
                .path(path)
                .build();
    }
	
    //파일명 생성
    private String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }

    //이미지 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }
    

    //URL 가져오기
    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    //파일 지우기
    private void removeFile(File file) {
        file.delete();
    }

    //파일 변환
    public Optional<File> convertMultipartFileToFile(MultipartFile multipartFiles) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/" + multipartFiles.getOriginalFilename());

        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)){
                fos.write(multipartFiles.getBytes());
            }
            return Optional.of(file);
        }
        return Optional.empty();
    }

    //삭제
    public void remove(int artNo) {
    	
    	//imgNo 가져오기
    	int imgNo = articleImgRepository.findImgNo(artNo);
    	
    	//artNo로 찾아서 가져온 imgSave(key)가 있을 경우
		if(articleImgRepository.findByArtNoAndImgNo(artNo,1).getImgSave() != null) { 
			
			//imgNo의 최대 갯수만큼 반복해서 이미지 파일 삭제하기
			for(int i = 1 ; i <= imgNo; i++) {
				String imgSaveKey = articleImgRepository.findByArtNoAndImgNo(artNo,i).getImgSave();
				String key="images/" + imgSaveKey;
				
				amazonS3.deleteObject(bucket, key);
				System.out.println(key);
			}
		}
    }
    
    //수정
    public void rename(AwsS3 awsS3, String destinationKey){
    	amazonS3.copyObject(
    			bucket,
    			awsS3.getKey(),
                bucket,
                destinationKey
        );
    	amazonS3.deleteObject(bucket, awsS3.getKey());
    }
    
 	//옮기기
    public void moveTo(String sourceKey, String destinationKey){
    	CopyObjectRequest copy = new CopyObjectRequest(bucket, sourceKey, bucket, destinationKey);
    	
    	copy.withCannedAccessControlList(CannedAccessControlList.PublicRead);
    	
    	amazonS3.copyObject(copy);
    	
    	amazonS3.deleteObject(bucket, sourceKey);
    }
    

    
    /**
     * 파일명이 한글인 경우 URL encode이 필요함.
     * @param request
     * @param displayFileName
     * @return
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("unused")
	private String getEncodedFilename(HttpServletRequest request, String displayFileName) throws UnsupportedEncodingException {
        String header = request.getHeader("User-Agent");

        String encodedFilename = null;
        if (header.indexOf("MSIE") > -1) {
            encodedFilename = URLEncoder.encode(displayFileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (header.indexOf("Trident") > -1) {
            encodedFilename = URLEncoder.encode(displayFileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (header.indexOf("Chrome") > -1) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < displayFileName.length(); i++) {
                char c = displayFileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else if (header.indexOf("Opera") > -1) {
            encodedFilename = "\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (header.indexOf("Safari") > -1) {
            encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"", "UTF-8");
        } else {
            encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes("UTF-8"), "8859_1") + "\"", "UTF-8");
        }
        return encodedFilename;
    }
    
    public void delete(String key) {
        try {
            //Delete 객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, key);
            //Delete
            this.amazonS3.deleteObject(deleteObjectRequest);
            System.out.println(String.format("[%s] deletion complete", key));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

   
}