package hello.until.item.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    public Optional<Item> readItem(long id) {
        return this.itemRepository.findById(id);
    }

    public List<Item> readAllItems(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Item> items = itemRepository.findAll(pageRequest);
        return items.stream().toList();
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

    @Transactional
    public void deleteItem(long id) {
        this.itemRepository.deleteById(id);
    }
}
