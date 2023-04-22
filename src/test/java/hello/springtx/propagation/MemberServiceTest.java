package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    LogRepository logRepository;

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     * 각각 트렌젝션을 갖고 커밋
     */
    @Test
    void outerTxOff_success(){
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     * 각각 트렌젝션을 갖고 LogRepository 롤백
     */
    @Test
    void outerTxOff_fail(){
        String username = "LogException_outerTxOff_fail";

       assertThatThrownBy(()->memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //완전히 롤백되지 않고, member 데이터가 남아서 저장된다. try catch로 예외 잡았으니깐
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository    @Transactional:OFF
     * 하나의 트렌젝션을 갖고 커밋 - 회원서비스에만 트랜젝션 사용
     */
    @Test
    void singleTx(){
        String username = "singleTx";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository    @Transactional:OFF
     * 하나의 트렌젝션을 갖고 LogRepository 롤백 - 회원서비스에만 트랜젝션 사용
     */
    @Test
    void singleTx_fail(){
        String username = "LogException_singleTx_fail";

        assertThatThrownBy(()->memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //완전히 모든게 다 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

}