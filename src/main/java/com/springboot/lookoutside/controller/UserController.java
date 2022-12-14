package com.springboot.lookoutside.controller;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.lookoutside.common.ApiResponse;
import com.springboot.lookoutside.config.properties.AppProperties;
import com.springboot.lookoutside.domain.AuthReqModel;
import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.domain.UserRefreshToken;
import com.springboot.lookoutside.dto.ResponseDto;
import com.springboot.lookoutside.oauth.entity.RoleType;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;
import com.springboot.lookoutside.oauth.repository.UserRefreshTokenRepository;
import com.springboot.lookoutside.oauth.token.AuthToken;
import com.springboot.lookoutside.oauth.token.AuthTokenProvider;
import com.springboot.lookoutside.service.UserService;
import com.springboot.lookoutside.utils.CookieUtil;
import com.springboot.lookoutside.utils.HeaderUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user") // ?????? ?????? ????????? ??? ????????????
public class UserController {

	@Autowired 
	private UserService userService;

	private final AppProperties appProperties;
	private final AuthTokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;
	private final UserRefreshTokenRepository userRefreshTokenRepository;
	private final PasswordEncoder encoder;

	private final AuthUserRepository userRepository;

	private final static long THREE_DAYS_MSEC = 259200000;
	private final static String REFRESH_TOKEN = "refresh_token";
	
	@PostMapping("/sign-in")
	public ApiResponse login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthReqModel authReqModel) {

		User persistance = userRepository.findByUseId(authReqModel.getUseId());

		System.out.println(authReqModel.getUseId() +" : " + persistance.getUseRole());
		System.out.println(encoder.matches(authReqModel.getUsePw(), persistance.getUsePw()));

		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(persistance.getUseId(), authReqModel.getUsePw());
		System.out.println(authenticationToken);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		String useId = authReqModel.getUseId();

		User user = userRepository.findByUseId(useId);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		Date now = new Date();
		AuthToken accessToken = tokenProvider.createAuthToken(
				useId,
				user.getUseNo(),
				user.getUseNick(),
				user.getUseRole().getCode(),
				new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()),
				user.getSnsNick()
				);

		long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
		AuthToken refreshToken = tokenProvider.createAuthToken(
				appProperties.getAuth().getTokenSecret(),
				new Date(now.getTime() + refreshTokenExpiry)
				);

		// userId refresh token ?????? DB ??????
		UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUseId(useId);
		if (userRefreshToken == null) {
			// ?????? ?????? ?????? ??????
			userRefreshToken = new UserRefreshToken(useId, refreshToken.getToken());
			userRefreshTokenRepository.saveAndFlush(userRefreshToken);
		} else {
			// DB??? refresh ?????? ????????????
			userRefreshToken.setRefreshToken(refreshToken.getToken());
		}

		int cookieMaxAge = (int) refreshTokenExpiry / 60;
		CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
		CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
		//response.addHeader("Authorization", "Bearer " + accessToken.getToken());
		
		return ApiResponse.success("token", accessToken.getToken());

	}

	@GetMapping("/refresh")
	public ApiResponse refreshToken (HttpServletRequest request, HttpServletResponse response) {
		// access token ??????
		String accessToken = HeaderUtil.getAccessToken(request);
		AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
		if (!authToken.validate()) {
			return ApiResponse.invalidAccessToken();
		}

		// expired access token ?????? ??????
		Claims claims = authToken.getExpiredTokenClaims();
		if (claims == null) {
			return ApiResponse.notExpiredTokenYet();
		}

		String useId = claims.getSubject();
		RoleType roleType = RoleType.of(claims.get("role", String.class));

		// refresh token
		String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
				.map(Cookie::getValue)
				.orElse((null));
		AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

		if (authRefreshToken.validate()) {
			return ApiResponse.invalidRefreshToken();
		}

		// userId refresh token ?????? DB ??????
		UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUseIdAndRefreshToken(useId, refreshToken);
		if (userRefreshToken == null) {
			return ApiResponse.invalidRefreshToken();
		}
		User user = userRepository.findByUseId(useId);
		Date now = new Date();
		AuthToken newAccessToken = tokenProvider.createAuthToken(
				useId,
				user.getUseNo(),
				user.getUseNick(),
				roleType.getCode(),
				new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()),
				user.getSnsNick()
				);

		long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

		// refresh ?????? ????????? 3??? ????????? ?????? ??????, refresh ?????? ??????
		if (validTime <= THREE_DAYS_MSEC) {
			// refresh ?????? ??????
			long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

			authRefreshToken = tokenProvider.createAuthToken(
					appProperties.getAuth().getTokenSecret(),
					new Date(now.getTime() + refreshTokenExpiry)
					);

			// DB??? refresh ?????? ????????????
			userRefreshToken.setRefreshToken(authRefreshToken.getToken());

			int cookieMaxAge = (int) refreshTokenExpiry / 60;
			CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
			CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
		}

		return ApiResponse.success("token", newAccessToken.getToken());
	}
	
	//????????????
	@PostMapping("/sign-out/{useNo}")
	public ResponseDto<Integer> signOut(@PathVariable int useNo) throws Exception{ 
		userService.logout(useNo);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1); 
	}

	//????????????
	@PostMapping("/sign-up")
	public ResponseDto<Integer> signUp(@RequestBody User user) { // json ???????????? RequestBody
		System.out.println("UserController : signUp ?????? : ????????????");
		userService.signUp(user);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1); // Java ??????????????? Json?????? ??????
	}

	//????????? ????????????
	@GetMapping("/Nickname/{useNick}")
	public ResponseDto<Boolean> useNickCheck(@PathVariable String useNick) {
		System.out.println("UserController : useIdCheck ?????? " + useNick);
		return new ResponseDto<Boolean>(HttpStatus.OK.value(), userService.useNickCheck(useNick)); // false => ????????? ??????, true => ????????? ?????????
	}

	//ID ????????????
	@GetMapping("/Id/{useId}")
	public ResponseDto<Boolean> useIdCheck(@PathVariable String useId) {
		System.out.println("UserController : useIdCheck ?????? " + useId);
		return new ResponseDto<Boolean>(HttpStatus.OK.value(), userService.useIdCheck(useId)); // false => ID ??????, true => ID ?????????
	}

	//Email ??????????????????
	@GetMapping("/Email/{useEmail}")
	public ResponseDto<Boolean> useEmailCheck(@PathVariable String useEmail) {
		System.out.println("UserController : useEmailCheck ?????? " + useEmail);
		return new ResponseDto<Boolean>(HttpStatus.OK.value(), userService.useEmailCheck(useEmail)); // false => ???????????? ?????? , true => ???????????? ??????
	}

	//?????? ???????????? ?????? (??????????????? ???????????? ??????????????????)
	@GetMapping("/{useNo}")
	public ResponseDto<Optional<User>> myPageInfo(@PathVariable int useNo) {
		return new ResponseDto<Optional<User>>(HttpStatus.OK.value(), userService.myPageInfo(useNo));
	}

	//???????????? ?????????(??????)
	@PostMapping("/myPw")
	public ResponseDto<Boolean> checkMyPw(@RequestBody User user) {
		System.out.println("???????????? ??????");
		return new ResponseDto<Boolean>(HttpStatus.OK.value(), userService.checkMyPw(user));
	}

	//???????????? ??????
	@PutMapping
	public ResponseDto<Integer> update(@RequestBody User user) {
		userService.updateUser(user);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1);
	}

	//??????????????????
	@PutMapping("/NewPw/{useId}")
	public ResponseDto<Integer> update(@RequestBody User user, @PathVariable String useId) {
		userService.newPw(user,useId);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1);
	}

	//????????? ??????
	@GetMapping("/myId/{useEmail}")
	public ResponseDto<String> findMyId(@PathVariable String useEmail) {
		System.out.println(useEmail);
		String myId = userService.findMyId(useEmail);
		return new ResponseDto<String>(HttpStatus.OK.value(), myId);
	}

	//?????? ??????, ??????
	@DeleteMapping("/{useNo}")
	public ResponseDto<String> deleteUser(@PathVariable int useNo) throws Exception{

		userService.leave(useNo); // ????????? ?????? ??????
		String result = userService.deleteUser(useNo);
		
		return new ResponseDto<String>(HttpStatus.OK.value(), result);
	}
	
}
