package hello.springtx.order;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class OrderServiceTest {
   @Autowired OrderService orderService;
   @Autowired OrderRepository repository;

   @Test
    void complete() throws NotEnoughMoneyException {
       //Given
       Order order = new Order();
       order.setUsername("normal");
       //when
       orderService.order(order);
       //then
       Order findOrder = repository.findById(order.getId()).get();
       assertThat(findOrder.getPayStatus()).isEqualTo("complete");
   }

   @Test
    void runtimeException(){
       //Given
       Order order = new Order();
       order.setUsername("exception");
       //when,then
       assertThatThrownBy(()->orderService.order(order)).isInstanceOf(RuntimeException.class);
       //then
       Optional<Order> findOrder = repository.findById(order.getId());
       assertThat(findOrder.isEmpty()).isTrue();
   }

   @Test
    void bizException(){
       //Given
       Order order = new Order();
       order.setUsername("noMoney");
       //when
       try {
           orderService.order(order);
           Assertions.fail("should have thrown exception");
       } catch (NotEnoughMoneyException e) {
           log.info("Please charge money");

       }
       //then
       Order findOrder = repository.findById(order.getId()).get();
       assertThat(findOrder.getPayStatus()).isEqualTo("wait");
   }
}