package com.springboot.lookoutside.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.lookoutside.domain.User;

public interface AuthUserRepository extends JpaRepository<User, Integer>{
	
	User findByUseId(String userId);

}
