package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;


import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom ,JpaSpecificationExecutor{
    //인터페이스 끼리 상속받을딴 Implement 가아니라 extends 다.

    List<Member> findByUsernameAndAgeGreaterThan(String username , int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username , @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id , m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    //반환타입이 page ,
    //매개변수 pageable은 지금이 몇페이지인지 조건이 들어간다.
    //인터페이스 만 만들어주면 된다.
    @Query(value = "select m from Member m left join m.team t " , countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age , Pageable pageable);
    //Slice<Member> findByAge(int age , Pageable pageable);

    //List<Member> findByAge(int age , Pageable pageable);
    //그냥 list로 바로 받을 수도 있다.
    //대신 아무기능도 없다

    @Modifying(clearAutomatically = true) // 수정할땐 이값을 꼭 넣어야 한다.
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //fetchJoin할 필드명을 넣어주면 도니다.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //@EntityGraph(attributePaths = ("team"))
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly" , value="true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //jpa가 제공
    List<Member> findLockByUsername(String username);

    //구현하고 싶은 기능을 사용할순간 위 모든 메서드를 구현해야한다.
    //커스텀한 기능 하나만 쓰고싶을땐 사용자 정의 인터페이스를 만든다.

    //반환타입을 인터페이스로 남아주면 해당 필드만 넘어 오게 된다,
    <T>List<T> findProjectionsByUsername(@Param("username") String username , Class<T> type);

    @Query(value = "select * from member where username = ?" , nativeQuery = true)
    Member findByNativeQuery(String username);


    @Query(value="select m.member_id as id, m.username , t.name as teamName" +
            " from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
