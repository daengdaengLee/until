package hello.until.order.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.service.ItemService;
import hello.until.order.entity.Order;
import hello.until.order.entity.OrderStatus;
import hello.until.order.repository.OrderRepository;
import hello.until.user.entity.User;
import hello.until.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public Order requestOrder(OrderStatus status, Long buyerId, Long itemId){
        User buyer = userService.getUserById(buyerId).orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));
        Item item = itemService.readItem(itemId).orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_UPDATE));
        Order order = Order.builder()
                .status(status)
                .user(buyer)
                .item(item)
                .build();
        return orderRepository.save(order);
    }

    @Transactional
    public Order approveOrder(Long orderId, Long sellerId, Long itemId){
        Order order = this.getOrderById(orderId);
        User seller = userService.getUserById(sellerId).orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));
        Item item = itemService.readItem(itemId).orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_UPDATE));
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ExceptionCode.NO_ORDER));
    }
}
