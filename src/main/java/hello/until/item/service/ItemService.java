package hello.until.item.service;

import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public Optional<Item> readItem(Long id) {
        return this.itemRepository.findById(id);
    }

    @Transactional
    public Item createItem(String name, Integer price) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .build();

        return itemRepository.save(item);
    }
}
