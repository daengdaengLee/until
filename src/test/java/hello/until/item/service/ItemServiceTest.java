package hello.until.item.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    private ItemService itemService;

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
        when(this.itemRepository.findById(testItemId))
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
        when(this.itemRepository.findById(testItemId))
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

        when(this.itemRepository.save(any(Item.class))).thenReturn(this.testItem);

        // when
        Item item = itemService.createItem(name, price);

        // then
        var itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(itemCaptor.capture());
        var passedItem = itemCaptor.getValue();
        assertThat(name.equals(passedItem.getName())).isTrue();
        assertThat(price.equals(passedItem.getPrice())).isTrue();

        assertThat(name.equals(item.getName())).isTrue();
        assertThat(price.equals(item.getPrice())).isTrue();
    }

    @Test
    @DisplayName("존재하는 상품의 이름과 가격을 수정할 수 있다.")
    void updateItem() {
        // given
        var id = this.testItem.getId();
        var updatedName = this.testItem.getName() + " (수정됨)";
        var updatedPrice = this.testItem.getPrice() + 10_000;
        var updatedItem = Item.builder()
                .id(id)
                .name(updatedName)
                .price(updatedPrice)
                .createdAt(this.testItem.getCreatedAt())
                .updatedAt(this.testItem.getUpdatedAt())
                .build();
        when(this.itemRepository.findById(id))
                .thenReturn(Optional.of(this.testItem));
        when(this.itemRepository.save(ArgumentMatchers.any(Item.class)))
                .thenReturn(updatedItem);

        // when
        var result = this.itemService.updateItem(id, updatedName, updatedPrice);

        // then
        var itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(this.itemRepository, times(1)).save(itemCaptor.capture());
        var passedItem = itemCaptor.getValue();
        assertThat(passedItem.getId()).isEqualTo(id);
        assertThat(passedItem.getName()).isEqualTo(updatedName);
        assertThat(passedItem.getPrice()).isEqualTo(updatedPrice);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(updatedName);
        assertThat(result.getPrice()).isEqualTo(updatedPrice);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하려고 하면 예외가 발생한다.")
    void updateNoItem() {
        // given
        var noItemId = 100L;
        when(this.itemRepository.findById(noItemId))
                .thenReturn(Optional.empty());

        // when
        var ex = catchThrowable(() -> this.itemService.updateItem(noItemId, "없는 상품", 10_000));

        // then
        assertThat(ex).isInstanceOf(CustomException.class);
        var code = ((CustomException) ex).getCode();
        assertThat(code).isEqualTo(ExceptionCode.NO_ITEM_TO_UPDATE);
    }
}