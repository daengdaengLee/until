package hello.until.item.dto.response;

import hello.until.item.entity.Item;

import java.util.List;

public record ReadAllItemResponse(
        List<Item> data
) {
}
