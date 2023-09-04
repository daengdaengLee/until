package hello.until.item.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    public Optional<Item> readItem(long id) {
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

    @Transactional
    public Item updateItem(long id, String name, int price) {
        var item = this.itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_UPDATE));
        item.setName(name);
        item.setPrice(price);
        this.itemRepository.save(item);
        return item;
    }
}
