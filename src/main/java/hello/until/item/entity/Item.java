package hello.until.item.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Item {
    private Long id;
    private String name;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
