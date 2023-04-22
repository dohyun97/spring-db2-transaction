package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;
@Slf4j
@SpringBootTest
public class BasicTxTest {
    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class config{
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("start transaction");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction commit");
        txManager.commit(status);
        log.info("Complete Transaction Commit");
    }

    @Test
    void rollback(){
        log.info("start transaction");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction rollback");
        txManager.rollback(status);
        log.info("Complete Transaction rollback");
    }

    @Test
    void double_commit(){
        log.info("start transaction");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction1 commit");
        txManager.commit(tx1);
        log.info("Complete Transaction1 Commit");

        log.info("start transaction2");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction2 commit");
        txManager.commit(tx2);
        log.info("Complete Transaction 2Commit");
    }

    @Test
    void double_commit_rollback(){
        log.info("start transaction");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction1 commit");
        txManager.commit(tx1);
        log.info("Complete Transaction1 Commit");

        log.info("start transaction2");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Start Transaction2 rollback");
        txManager.rollback(tx2);
        log.info("Complete Transaction2 rollback");
    }

    @Test
    void inner_commit(){
        log.info("Start outer transaction");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

        log.info("Start inner Transaction");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}",inner.isNewTransaction());
        log.info("Commit inner transaction");
        txManager.commit(inner);

        log.info("Commit outer transaction");
        txManager.commit(outer);
    }

    @Test
    void outer_rollback(){
        log.info("Start outer transaction");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

        log.info("Start inner Transaction");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}",inner.isNewTransaction());
        log.info("Commit inner transaction");
        txManager.commit(inner);

        log.info("Rollback outer transaction");
        txManager.rollback(outer);
    }

    @Test
    void inner_rollback(){
        log.info("Start outer transaction");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

        log.info("Start inner Transaction");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}",inner.isNewTransaction());
        log.info("Rollback inner transaction");
        txManager.rollback(inner);

        log.info("Commit outer transaction");
        Assertions.assertThatThrownBy(()->txManager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new(){
        log.info("Start outer transaction");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction = {}", outer.isNewTransaction());

        log.info("Start inner Transaction");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.isNewTransaction() = {}",inner.isNewTransaction());
        log.info("Rollback inner transaction");
        txManager.rollback(inner);

        log.info("Commit outer transaction");
        txManager.commit(outer);
    }
}
