package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LogRepository {
    private final EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage){
        em.persist(logMessage);
        if(logMessage.getMessage().contains("LogException")){
            log.info("Exception in save log");
            throw new RuntimeException("Runtime Exception");
        }
    }

    public Optional<Log> find(String message){
        String jpql = "select i from Log i where i.message=:message";
        return em.createQuery(jpql,Log.class)
                 .setParameter("message",message)
                .getResultList().stream().findAny();
    }
}
