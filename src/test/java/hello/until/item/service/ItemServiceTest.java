package hello.until.item.service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.entity.Item;
import hello.until.item.repository.ItemRepository;
import hello.until.user.constant.Role;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void beforeEach() {
        this.itemService = new ItemService(this.itemRepository, this.userRepository);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("12345678");
        user.setRole(Role.SELLER);

        testItem = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
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

    @Nested
    public class ReadAll {
        private List<Item> testItems;

        @BeforeEach
        void beforeEach() {
            this.testItems = new ArrayList<>();
            for (long id = 99; id >= 0; id--) {
                this.testItems.add(Item.builder()
                        .id(id)
                        .name("테스트 상품 " + id)
                        .price(10_000)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }
        }

        @Test
        @DisplayName("page 와 size 로 조회하면 id 를 기준으로 역순 정렬해서 page 와 size 에 해당하는 상품 리스트를 반환 한다.")
        void readAllItem() {
            // given
            int page = 1;
            int size = 10;

            PageRequest pageRequest = PageRequest.of(page, size);
            int startIdx = (int) pageRequest.getOffset();
            int endIdx = Math.min(startIdx + pageRequest.getPageSize(), testItems.size());
            Page<Item> itemPage = new PageImpl<>(this.testItems.subList(startIdx, endIdx), pageRequest, this.testItems.size());

            when(itemRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))))
                    .thenReturn(itemPage);

            // when
            var result = itemService.readAllItems(page, size);

            // then
            assertThat(!result.isEmpty()).isTrue();
            assertThat(result.size()).isEqualTo(size);

            // repository 전달 값 검증
            var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(itemRepository, times(1)).findAll(pageableCaptor.capture());
            var passedPageable = pageableCaptor.getValue();
            assertThat(passedPageable.getPageNumber()).isEqualTo(page);
            assertThat(passedPageable.getPageSize()).isEqualTo(size);

            // 결과 값을 id를 통해 검증
            int id = Math.toIntExact(this.testItems.get(startIdx).getId());
            int idx = 0;
            while(idx < size) {
                Item now = result.get(idx);
                assertThat(now.getId()).isEqualTo(id);
                assertThat(now.getName()).isEqualTo("테스트 상품 " + id);
                id--;
                idx++;
            }
        }

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
        Long userId = this.testItem.getUser().getId();
        Role role = this.testItem.getUser().getRole();

        when(this.userRepository.getReferenceById(userId)).thenReturn(User.builder().id(userId).build());
        when(this.itemRepository.save(any(Item.class))).thenReturn(this.testItem);

        // when
        Item item = itemService.createItem(name, price, userId, role);

        // then
        var itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(itemCaptor.capture());
        var passedItem = itemCaptor.getValue();
        assertThat(name.equals(passedItem.getName())).isTrue();
        assertThat(price.equals(passedItem.getPrice())).isTrue();
        assertThat(userId.equals(passedItem.getUser().getId())).isTrue();

        assertThat(name.equals(item.getName())).isTrue();
        assertThat(price.equals(item.getPrice())).isTrue();
    }

    @Test
    @DisplayName("SELLER가 아닌 회원의 userId로 상품명, 상품 가격으로 상품을 등록하면 Seller가 아니라는 CustomException 이 발생한다.")
    void createItemNotSeller() {
        // given
        String name = this.testItem.getName();
        Integer price = this.testItem.getPrice();
        Long userId = this.testItem.getUser().getId();
        Role role = Role.BUYER;

        // then
        var ex = catchThrowable(() -> itemService.createItem(name, price, userId, role));

        // then
        assertThat(ex).isInstanceOf(CustomException.class);
        var code = ((CustomException) ex).getCode();
        assertThat(code).isEqualTo(ExceptionCode.NO_ROLE_TO_CREATE_ITEM);
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

    @Test
    @DisplayName("상품 아이디로 삭제하면 해당 상품 삭제를 Repository 에 요청하고 종료한다.")
    void deleteItem() {
        // given
        // testItem 사용

        // when
        this.itemService.deleteItem(this.testItem.getId());

        // then
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(this.itemRepository, times(1)).deleteById(idCaptor.capture());
        var passedId = idCaptor.getValue();
        assertThat(passedId).isEqualTo(this.testItem.getId());
    }
}