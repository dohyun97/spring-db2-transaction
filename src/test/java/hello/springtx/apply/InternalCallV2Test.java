package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//AOP problem - Solution: separate class
@SpringBootTest
@Slf4j
public class InternalCallV2Test {
    @Autowired CallService service;

    @Test
    void printProxy(){
        log.info("callService class = {}",service.getClass());
    }

    @Test
    void internalCall(){
        service.internalService.internal();
    }

    @Test
    void externalCall(){
        service.external();
    }

    @TestConfiguration
    static class InternalCallV2Config{

        @Bean
        CallService callService(){
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService(){
            return new InternalService();
        }
    }

    @RequiredArgsConstructor
    static class CallService{
        private final InternalService internalService;
        public void external(){
            log.info("call external");
            printTxInfo();
            internalService.internal();    //call internal method
        }

        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}",txActive);
        }
    }

    static class InternalService{
        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }
        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}",txActive);
        }
    }
}
