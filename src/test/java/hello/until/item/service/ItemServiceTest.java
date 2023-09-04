package hello.until.item.service;

import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    private ItemService itemService;
    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    private Item testItem;

    @BeforeEach
    void beforeEach() {
        this.itemService = new ItemService(this.itemRepository);

        this.testItem = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 조회하면 해당 상품을 담은 Optional 객체를 반환한다.")
    void readItem() {
        // given
        var testItemId = this.testItem.getId();
        Mockito.when(this.itemRepository.findById(testItemId))
                .thenReturn(Optional.of(this.testItem));

        // when
        var result = this.itemService.readItem(testItemId);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testItem);
    }

    @Test
    @DisplayName("존재하지 않는 상품 아이디로 조회하면 빈 Optional 객체를 반환한다.")
    void readNoItem() {
        // given
        var testItemId = this.testItem.getId();
        Mockito.when(this.itemRepository.findById(testItemId))
                .thenReturn(Optional.empty());

        // when
        var result = this.itemService.readItem(testItemId);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("상품명, 상품 가격으로 상품을 등록한다.")
    void createItem() {
        // given
        String name = this.testItem.getName();
        Integer price = this.testItem.getPrice();

        Mockito.when(this.itemRepository.save(any(Item.class))).thenReturn(this.testItem);

        // when
        Item item = itemService.createItem(name, price);
        verify(itemRepository).save(itemCaptor.capture());
        Item saveItem = itemCaptor.getValue();

        // then
        assertThat(item).isNotNull();
        assertThat(name.equals(item.getName())).isTrue();
        assertThat(price.equals(item.getPrice())).isTrue();
        assertThat(name.equals(saveItem.getName())).isTrue();
        assertThat(price.equals(saveItem.getPrice())).isTrue();
    }
}