package hello.until.item.service;

import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void beforeEach() {
        this.itemService = new ItemService(this.itemRepository);

        this.testItem = new Item();
        this.testItem.setId(1L);
        this.testItem.setName("테스트 상품");
        this.testItem.setPrice(10_000);
        this.testItem.setCreatedAt(LocalDateTime.now());
        this.testItem.setUpdatedAt(LocalDateTime.now());
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
}