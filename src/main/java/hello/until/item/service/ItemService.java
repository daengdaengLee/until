package hello.until.item.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
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
    private final UserRepository userRepository;

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
    public Item createItem(String name, Integer price, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ExceptionCode.NO_USER_TO_CREATE_ITEM);
        }

        User userProxy = userRepository.getReferenceById(userId);

        Item item = Item.builder()
                .name(name)
                .price(price)
                .user(userProxy)
                .build();

        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(long id, String name, Integer price) {
        var item = this.itemRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_UPDATE));
        if (name != null) {
            item.setName(name);
        }
        if (price != null) {
            item.setPrice(price);
        }
        this.itemRepository.save(item);
        return item;
    }

    @Transactional
    public void deleteItem(long id) {
        this.itemRepository.deleteById(id);
    }

    // @TODO 단위 테스트
    @Transactional
    public void deleteItem(long id, User user) {
        var itemResult = this.itemRepository.findById(id);
        if (itemResult.isEmpty()) {
            return;
        }
        var item = itemResult.get();
        if (item.getUser() == null || item.getUser().isSameUser(user)) {
            // @TODO 커스텀 예외 처리
            throw new RuntimeException("상품 판매자만 상품 삭제가 가능합니다.");
        }
        this.itemRepository.deleteById(id);
    }
}
