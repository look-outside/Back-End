# 👀 밖에 봐봐 LOOK-OUTSIDE

 "오늘 옷 뭐 입지..?" 고민하신 적 있으신가요? 날씨가 어떤 지에 따라 가디건을 입어야 할지, 반팔만 입어야 할지... 기상청의 온도 숫자는 와닿지가 않단말이죠..!  타지에 놀러 가야 하는데 그곳의 날씨를 알 수 없어 고민이 들기도 하구요.

이러한 니즈에 따라, 매일 날씨에 맞는 옷차림을 서로 공유하며 자유롭게 대화하는 커뮤니티, 🌈[밖에 봐봐](https://look-outside.netlify.app/)🌈 서비스를 개발했습니다! 각 지역의 사람들은 “오늘 가디건만 입기 딱 좋은 날씨네요”와 같이 글을 올리며 니즈를 해결하고 커뮤니티를 활성화할 수 있습니다.

## 🔨 기술 스택

**Front-end**

* TypeScript
* React, Zustand
* HTML, CSS, styled-components
* Netlify

**Back-end**

- Java 8
- SpringBoot 2.5.2, Gradle, Spring Security, OAuth2.0 + JWT
- MySQL 8.0.28(RDB), JPA & QueryDSL (ORM)
- AWS - EC2, RDS, S3, Route53
- Nginx (Reverse Proxy Server)

**협업 Tool**

- Git, Notion, Zoom, Discord

## 📆 개발 기간
2022년 8월 1일 ~ 2022년 9월 4일

## 📜 기획 및 설계

### **Diagram**

<p align="center">
    <img src="https://user-images.githubusercontent.com/97022695/188375741-79edbb1d-d82a-4883-8081-22e4354f61ac.png" width="60%" />
</p>

### **API**
<a href="https://www.notion.so/look-outiside/API-e388c9ea0b624e8abf3f1981ef4e00f7">밖에 봐봐 API 설계 (notion)</a>

### **ERD**

<p align="center">
    <img src="https://user-images.githubusercontent.com/97022695/188367117-73b6f0fc-cec0-470a-b6fb-a2c3887485d6.png" width="80%" />
</p>

## 💻 페이지 기능

### - 메인페이지

- 지도의 지역 아이콘 선택 시 "오늘 뭐 입지?" 게시글이 지역에 따라 바뀝니다.
- 제공하는 모든 카테고리의 최신 게시글을 확인할 수 있습니다.

<img src="https://user-images.githubusercontent.com/97022695/188783716-66700622-68a8-4b82-a641-be8806fa7f0e.gif"/> | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img src="https://user-images.githubusercontent.com/97022695/188448496-f791ee7e-5173-472c-995f-f38db13402db.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 :--: | :--: 

### - 로그인, 회원가입

- 유저 입력값의 유효성 검사를 통해 회원 가입을 할 수 있습니다.
- 소셜 로그인 (구글, 카카오)이 가능하며, 해당 유저들은 닉네임 정보만 사용합니다.

회원가입 | 로그인
:--: | :--: 
<img src="https://user-images.githubusercontent.com/97022695/188448903-2e5e20d0-6443-472c-b228-97b772bd8c78.gif"/> | <img src="https://user-images.githubusercontent.com/97022695/188448652-2190a771-8f2b-46bc-927e-941d7b3909b9.gif"/> 

### - 오늘의 옷

- "데일리룩" 과 "오늘 뭐 입지?"를 담고 있는 큰 카테고리입니다.
- **데일리룩** : 그날그날 날씨에 맞는 자신의 룩을 보여주는 카테고리
- **오늘 뭐 입지?** : 가디건? 자켓? 지역별 날씨에 따라 어떤 옷을 입을 지 서로 공유하는 카테고리

오늘의 옷 👭 | 데일리룩 | 오늘 뭐 입지?
:--: | :--: | :--: 
![clothes](https://user-images.githubusercontent.com/97022695/188601313-3160157b-d578-4d2c-813c-6e665b335e39.png) | ![daily](https://user-images.githubusercontent.com/97022695/188601418-1a74f207-1305-4ded-92ee-150cccdcb64e.png) | ![what](https://user-images.githubusercontent.com/97022695/188601401-c66fbada-6ab7-483a-894a-f1f10ee0fcf9.png)

### - 오늘의 하늘, 오늘의 모임

- **오늘의 하늘** : 현재 하늘 모습을 공유하는 카테고리
- **오늘의 모임** : 지역 기반으로 모임을 주최하고 참여하는 카테고리

오늘의 하늘 ⛅ | 오늘의 모임 👨‍👨‍👧‍🤼‍♂️
:--: | :--:
<img src="https://user-images.githubusercontent.com/97022695/188597534-11b01970-3572-4d85-b2e4-2f6995339c63.gif"/> | ![진현주lookmeet](https://user-images.githubusercontent.com/97022695/188599050-4d2ba608-931b-412e-b236-df1c72e3fd2e.png)

### - 글 작성, 상세페이지

- **게시글 작성** : 지역과 날씨를 자율적으로 선택하여 업로드할 수 있습니다.
- **글 상세페이지** : 사용자가 올린 내용과 댓글을 확인할 수 있습니다.

게시글 작성 | 글 상세페이지
:--: | :--:
![작성](https://user-images.githubusercontent.com/97022695/188617539-d4d3e345-4b77-42d5-849c-66031f2ee02a.gif) | ![상세](https://user-images.githubusercontent.com/97022695/188617588-a280a206-8c99-4bad-bab6-5cea69a9862c.gif)

### - 마이페이지, 관리자페이지

- **마이페이지** : 로그인 or 소셜로그인 여부에 따른 회원정보 수정, 작성글/댓글 목록, 회원 탈퇴 기능
- **관리자페이지** : 삭제 및 관리자 기능을 부여할 수 있는 회원/관리자 목록, 전체 게시글 목록

마이페이지 | 관리자페이지
:--: | :--:
![lookmy](https://user-images.githubusercontent.com/97022695/188602349-362e203e-b9d5-4a19-98f5-53d88b8fc567.gif) | ![lookadmin](https://user-images.githubusercontent.com/97022695/188600390-75cb678b-8720-4baf-a8d1-158612016127.gif)

## 👨‍👨‍👧‍👧 팀원

 역할 | 팀원 | 구현 기능
 :--: | :--: | :-- 
 Front | <a href="https://github.com/realzu">진현주</a> | - 프로젝트 총괄 및 아이디어 기획<br>- 마이페이지 (정보수정, 작성글/댓글목록, 탈퇴)<br>- 관리자페이지 (회원목록, 관리자임명/해임 기능)<br>- 지도 (open weather API 데이터 및 UI 구현)<br>- 페이지네이션 작업<br>- 오늘의 옷, 데일리룩, 오늘의 하늘 카테고리 개발
 Front | <a href="https://github.com/SH-Lee2">이시형</a> | - 메인페이지(헤더, 풋터)<br>- 로그인, 회원가입<br>- 상세페이지 작업<br>- 게시물 작성 기능 구현<br>- 오늘 뭐 입지?, 오늘의 모임 카테고리 개발
 Back | <a href="https://github.com/Lee-Han-Sol">이한솔</a> | - 로그인 및 소셜로그인(GOOGLE, KAKAO) 관리<br>- 회원가입 CRUD 작업<br>- 게시물 페이지 관련 CRUD 작업<br>- 관리자 페이지 관련 CRUD 작업<br>- 메인 페이지 기능 구현<br>- AWS(RDS, S3, EC2, Route53) 인프라 연동<br>- API 작성
 Back | <a href="https://github.com/nlee013">이나영</a> | - AWS(S3) 인프라 연동<br>- 게시물 페이지 관련 CRUD 작업<br>- 댓글 관련 CRUD 작업<br>- API 작성

