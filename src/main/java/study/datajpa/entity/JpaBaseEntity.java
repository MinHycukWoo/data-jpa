package study.datajpa.entity;


import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass //extends로 받을때 진짜 상속관계는 아니고 데이터만 가져다 쓸수있게 하는것,
@Getter
public class JpaBaseEntity {

    @Column(updatable = false , insertable = true)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    //업데이트 되기 전에 실행
    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
