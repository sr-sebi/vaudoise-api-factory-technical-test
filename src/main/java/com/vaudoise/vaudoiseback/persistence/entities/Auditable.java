package com.vaudoise.vaudoiseback.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    LocalDateTime modifiedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    String createdBy = "SYS_ADMIN";

    @LastModifiedBy
    @Column(name = "modified_by", nullable = false)
    String modifiedBy= "SYS_ADMIN";
}

