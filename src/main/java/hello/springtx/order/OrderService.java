package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository repository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("Call Order");
        repository.save(order);

        log.info("Pay Process");
        if(order.getUsername().equals("exception")){
            log.info("System Exception");
            throw new RuntimeException("System exception");
        } else if (order.getUsername().equals("noMoney")) {
            log.info("No Money Business exception");
            order.setPayStatus("wait");
            throw new NotEnoughMoneyException("No money");
        }else {
            log.info("Success");
            order.setPayStatus("complete");

        }
        log.info("complete Pay process");
    }
}
