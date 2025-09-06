package com.mockit.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.mockit.domain.model.entity.BaseEntity;

@Entity
@Table(name = "Members")
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
}
