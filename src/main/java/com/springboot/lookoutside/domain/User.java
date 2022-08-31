package com.springboot.lookoutside.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.lookoutside.oauth.entity.ProviderType;
import com.springboot.lookoutside.oauth.entity.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder 
@Entity 
@DynamicInsert 
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int useNo; 
	
	@Column(unique = true)
	private String useNick;
	
	@Column(nullable = false , unique = true)
	private String useId;
	
	@Column(nullable = false)
	private String usePw;
	
	@Column
	private String useName;
	
	@Column(nullable = false)
	private Integer useGender;
	
	@ColumnDefault("'USER'")
	@Enumerated(EnumType.STRING)
	private RoleType useRole; 
	
	@Column(nullable = false)
	private String useEmail;
	
	@JsonFormat(pattern = "YY.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
	@CreationTimestamp 
	private Timestamp useCreated;
	
	@Column
	@ColumnDefault("'LOCAL'")
    @Enumerated(EnumType.STRING)
	private ProviderType providerType;
	
	@ColumnDefault("1")
	private int snsNick;
	
	public User(
            String useId,
            String useName,
            String useNick,
            String useEmail,
            int useGender,
            ProviderType providerType,
            RoleType useRole,
            Timestamp useCreated,
            int snsNick
    ) {
        this.useId = useId;
        this.useName = useName;
        this.useNick = useNick != null ? useNick : "Annonymous";
        this.usePw = "NO_PASS";
        this.useEmail = useEmail != null ? useEmail : "NO_EMAIL";
        this.useGender = 0;
        this.providerType = providerType;
        this.useRole = useRole;
        this.useCreated = useCreated;
        this.snsNick = snsNick;
    }

}
