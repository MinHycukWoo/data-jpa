package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        System.out.println("memberRepository==" + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //Optional<Member> byId = memberRepository.findById(savedMember.getId());
        //가져온 값이 있을수도 있고 없을수도 있어서 Optional로 받아온다.

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //수정
        findMember1.setUsername("member!!!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        //한번에 바꾸기 shift + f6
        long deleteCount = memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member ("AAA" , 10);
        Member m2 = new Member ("AAA" , 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA" , 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member ("AAA" , 10);
        Member m2 = new Member ("AAA" , 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA" , 10);
        Assertions.assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member ("AAA" , 10);
        Member m2 = new Member ("AAA" , 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList =memberRepository.findUsernameList();
        for(String s : usernameList){
            System.out.println("s =" + s);
        }
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member ("AAA" , 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> usernameList =memberRepository.findMemberDto();
        for(MemberDto dto : usernameList){
            System.out.println("dto =" + dto);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member ("AAA" , 10);
        Member m2 = new Member ("AAA" , 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result =memberRepository.findByNames(Arrays.asList("AAA" , "BBB"));
        //iter 엔터
        for (Member member : result) {
            System.out.println("member = " + member) ;
        }
    }


    @Test
    public void returnType(){
        Member m1 = new Member ("AAA" , 10);
        Member m2 = new Member ("BBB" , 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        /*List<Member> aaa = memberRepository.findListByUsername("AAA");
        System.out.println("aaa" + aaa);*/
        /*Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember" + findMember);*/
        /*Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + aaa.get());*/

        List<Member> aaa = memberRepository.findListByUsername("asdasd");
        System.out.println("aaa =" + aaa)  ;

    }


    @Test
    public void paging(){
        //given
        //shift + f6
        memberRepository.save(new Member("member1" , 10));
        memberRepository.save(new Member("member2" , 10));
        memberRepository.save(new Member("member3" , 10));
        memberRepository.save(new Member("member4" , 10));
        memberRepository.save(new Member("member5" , 10));


        int age = 10;
        //페이지를 0부터 시작함
        //페이징 조건을 만들어준다,
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));



        //when
        //반환 타입이 페이지
        //List<Member> members = memberRepository.findByAge(age ,pageRequest);
        //반환 타입에 따라서 total count를 날릴지 안날릴지 결정된다.
        Page<Member> page = memberRepository.findByAge(age ,pageRequest);


        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(),null));
        //strea의 중간연산을 이용해서 MemberDto로 변환한다.

        //Slice<Member> page = memberRepository.findByAge(age ,pageRequest);
        //슬라이로 반환타입을 하면
        //예를들어 3개만 가져오고 싶다고 pageRequest에 정해둿을때
        //slice는 +1개를 더 가져온다.
        //slice는 전체카운터를 가져오지 않는다.
        //모바일 디바이스에서 많이 사용한다. 더보기 사용시 이걸 사용

        //long totalCount = memberRepository.totalCount(age);
        //토탈카운트를 받을 필요가 없다
        //반환 타입이 page면 토탈카운트 쿼리까지 날려서 값까지 가져온다.
        //then

        List<Member> content = page.getContent();
        //long totalElement = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member =" + member);
        }
        //System.out.println("totalElement =" + totalElement);

        Assertions.assertThat(content.size()).isEqualTo(3);
        //Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0); //페이지 번호도 가져올 수 있다.
        //Assertions.assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 객수
        Assertions.assertThat(page.isFirst()).isTrue(); //첫페이지 인가
        Assertions.assertThat(page.hasNext()).isTrue(); //다음 페이지가 있냐

    }


    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1" , 10));
        memberRepository.save(new Member("member2" , 19));
        memberRepository.save(new Member("member3" , 20));
        memberRepository.save(new Member("member4" , 21));
        memberRepository.save(new Member("member5" , 40));


        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5" + member5);
        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMEmberLazy(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1" , 10 , teamA);
        Member member2 = new Member("member2" , 10 , teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member =" + member.getUsername());
            System.out.println("member.team = " + member.getTeam().getName());

        }
    }

    @Test
    public void queryHint(){
        Member member1 = new Member("member1" , 10);
        memberRepository.save(member1);
        em.flush(); //영속성의 값을 DB에 동기화
        em.clear();

        //when
        //Member findMember = memberRepository.findById(member1.getId()).get();
        //findMember.setUsername("member2");
        //em.flush();//변경감지로 update문이 나간다.
        //변경감지의 치명적인 단점은 원본 객체가 있어야 한다는것.
        //만약 개발자가 변경할 의지없이 findMember를 뿌려줄 의도라고한더라고
        // JPA는 가져온 Member 와 DB에 Member를 만들어 2개를 관리하게 된다.
        
        //이렇게 조회용으로만 쓸수 있게 해주는 옵션이 하이버네이트에 있다.
        //여기서 힌트를 사용

        //읽을떄부터 읽기전용으로 가져오기 떄문에 스냅샷을 만들지 않는다.,
        //그래서 update가 되지 않는다. 변경감지 체크 자체를 하지 않는다.
        //readOnly가 성능최적화에 큰 효과는 없다.
        //정말 중요한것이나 성능이 느린것에 넣어준다. 없어도 성능은 잘 나온다.
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock(){
        Member member1 = new Member("member1" , 10);
        memberRepository.save(member1);
        em.flush(); //영속성의 값을 DB에 동기화
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");

        em.flush();
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1" , 0, teamA);
        Member m2 = new Member("m2" , 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //memberRepository.findByUsername("m1");

        //동적인 경우
        //도메인 조건 자체를 검색조건으로 만드는 경우
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher =ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(member , matcher);

        List<Member> result = memberRepository.findAll(example);
        


    }

    @Test
    public void projections(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1" , 0, teamA);
        Member m2 = new Member("m2" , 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1" , UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnlt =" + usernameOnly);
        }
    }

    @Test
    public void nativeQuery(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1" , 0, teamA);
        Member m2 = new Member("m2" , 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1,10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProejction =" + memberProjection.getUserName());
            System.out.println("memberProejction =" + memberProjection.getTeamName());
        }
        System.out.println("result" + result);
    }
}