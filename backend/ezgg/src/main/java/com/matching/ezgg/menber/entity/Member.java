package com.matching.ezgg.menber.entity;

import com.matching.ezgg.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long memberNo;

    @Column(unique = true ,nullable = false)
    private String memberId;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 4~20자의 영문 대소문자와 숫자만 사용 가능합니다.")
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String riotUsername;

    @Column(unique = true, nullable = false)
    private String riotTag;

    @Builder
    public Member(Long memberNo, String memberId, String password, String email, String riotUsername, String riotTag) {
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.password = password;
        this.email = email;
        this.riotUsername = riotUsername;
        this.riotTag = riotTag;
    }
}
