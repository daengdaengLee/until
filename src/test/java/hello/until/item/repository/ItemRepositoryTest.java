package hello.until.item.repository;

import hello.until.item.entity.Item;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 생성일을 지정하지 않아도 생성일이 등록된다. (테스트에서는 +1초 오차까지 허용)")
    void autoCreatedAt() {
        // given
        var item = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .build();
        var expected = LocalDateTime.now().plusSeconds(1);

        // when
        var savedItem = this.itemRepository.save(item);

        // then
        var createdAt = savedItem.getCreatedAt();
        assertThat(createdAt).isNotNull();
        assertThat(createdAt).isBeforeOrEqualTo(expected);
    }

    @Test
    @DisplayName("상품 수정일을 지정하지 않아도 수정일이 업데이트된다. (테스트에서는 +1초 오차까지 허용)")
    void autoUpdatedAt() {
        // given
        var item = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .build();
        item = this.itemRepository.save(item);
        var createdAt = item.getCreatedAt();

        var delaySeconds = 5;
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(delaySeconds))
                .until(() -> true);
        var expected1 = createdAt.plusSeconds(delaySeconds);
        var expected2 = LocalDateTime.now().plusSeconds(1);

        // when
        item.setName("수정한 테스트 상품");
        var updatedItem = this.itemRepository.saveAndFlush(item);

        // then
        var updatedAt = updatedItem.getUpdatedAt();
        assertThat(updatedAt).isNotNull();
        assertThat(updatedAt).isAfterOrEqualTo(expected1);
        assertThat(updatedAt).isBeforeOrEqualTo(expected2);
    }
}