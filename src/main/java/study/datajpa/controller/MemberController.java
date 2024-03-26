package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member){
        //Member member = memberRepository.findById(id).get();
        //스프링이 중간에서 컨버팅하는 과정을 끝내고 결과를 넣어주는것,
        //http 요청은 회원 ID 를 받지만 도메인 클래스 컨버터가
        // 중간에 동작해서 회원엔티티 객체를 반환해준다.

        //주의점 : 도메인 클래스 컨버터로 가져온 값을 꼭 조회용으로만 써야한다.
        return member.getUsername();
    }

    @GetMapping("/members")
    //public Page<Member> list( Pageable pageable){
    public Page<MemberDto> list(@PageableDefault(size=5, sort="username") Pageable pageable){
        //Pageable 인터페이지 페이지 파라미터 정보
        //Page 페이지 결과정보
        //바로 바인등 되게 지원해준다.
        //Pageable 이 있으면 spring이 pageRequest를 생성해서 값을 세팅해준다

        Page<Member> page = memberRepository.findAll(pageable);

        //엔티티를 DTO에 담아서 반환
        //Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(),null));
        //return map;
        return memberRepository.findAll(pageable).map(MemberDto::new);



        //http://localhost:8080/members?page=0 하면 값을 20개만 가져온다.
        //http://localhost:8080/members?page=0&size=3 사이즈를 지정해줄수도 있다.
        //http://localhost:8080/members?page=0&size=3&sort=id,desc
        

    }
    
    @PostConstruct
    public void init(){
        //memberRepository.save(new Member("userA"));
        for(int i=0; i<100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }



}
