package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member){
        log.info("Save member");
        em.persist(member);
    }

    public Optional<Member> find(String username){
        String jpql= "select m from Member m where m.username=:username";
        return em.createQuery(jpql,Member.class)
                .setParameter("username",username)
                .getResultList().stream().findAny();

    }
}
