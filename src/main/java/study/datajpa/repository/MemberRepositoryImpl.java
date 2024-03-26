package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;
    /*public MemberRepositoryImpl(EntityManager em){
        //em 자동주입
        this.em = em;
    }*/

    @Override
    public List<Member> findMemberCustom() {
        //순수 jpa 사용
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
