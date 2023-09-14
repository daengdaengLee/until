package hello.until.order.entity;

import hello.until.item.entity.Item;
import hello.until.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false, updatable = false)
    private Item item;

    public void approve(){
        this.status = OrderStatus.APPROVE;
    }
}
