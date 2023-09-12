package hello.until.order.repository;


import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import hello.until.order.entity.Order;
import hello.until.order.entity.OrderStatus;
import hello.until.user.constant.Role;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새 주문 생성")
    public void saveOrder() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("12345678")
                .role(Role.BUYER)
                .build();
        User dbUser = userRepository.save(user);
        Item item =  Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .build();
        Item dbItem = itemRepository.save(item);
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.RECEIVED)
                .user(dbUser)
                .item(dbItem)
                .build();
        // when
        Order dbOrder = orderRepository.save(order);
        // then
        assertEquals(dbOrder.getId(), order.getId());
        assertEquals(dbOrder.getUser(), order.getUser());
        assertEquals(dbOrder.getItem(), order.getItem());
        assertEquals(dbOrder.getStatus(), order.getStatus());

    }
}
