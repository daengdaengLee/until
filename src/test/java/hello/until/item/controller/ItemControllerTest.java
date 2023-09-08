package hello.until.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.request.UpdateItemRequest;
import hello.until.item.entity.Item;
import hello.until.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private Item testItem;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    void beforeEach() {
        testItem = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .createdAt(LocalDateTime.now().minusDays(1L))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class ReadAll {
        private List<Item> testItems;

        @BeforeEach
        void beforeEach() {
            int itemsCount = 100;
            this.testItems = new ArrayList<>();

            for (long id = itemsCount; id > 0; id--) {
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
        @DisplayName("페이지와 사이즈를 이용해 상품 목록을 조회 한다.")
        void readAllItem() throws Exception {
            // given
            int page = 0;
            int size = 20;

            PageRequest pageRequest = PageRequest.of(page, size);
            int startIdx = (int) pageRequest.getOffset();
            int endIdx = Math.min(startIdx + pageRequest.getPageSize(), testItems.size());

            when(itemService.readAllItems(page, size))
                    .thenReturn(this.testItems.subList(startIdx, endIdx));

            // when
            ResultActions resultActions = mockMvc.perform(get("/items")
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size)));

            // then
            // httpStatus 와 contentType 검증
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            // data 검증
            int resultIdx = 0;
            int itemsIdx = startIdx;

            for (; resultIdx < size; resultIdx++, itemsIdx++) {
                String base = String.format("$.data[%d]", resultIdx);
                resultActions
                        .andExpect(jsonPath(base + ".id").value(this.testItems.get(itemsIdx).getId().toString()))
                        .andExpect(jsonPath(base + ".name").value(this.testItems.get(itemsIdx).getName()))
                        .andExpect(jsonPath(base + ".price").value(this.testItems.get(itemsIdx).getPrice()))
                        .andExpect(jsonPath(base + ".createdAt").value(this.testItems.get(itemsIdx).getCreatedAt().format(dateTimeFormatter)))
                        .andExpect(jsonPath(base + ".updatedAt").value(this.testItems.get(itemsIdx).getUpdatedAt().format(dateTimeFormatter)));
            }

            var pageCaptor = ArgumentCaptor.forClass(Integer.class);
            var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(itemService, times(1)).readAllItems(pageCaptor.capture(), sizeCaptor.capture());

            var passedPage = pageCaptor.getValue();
            var passedSize = sizeCaptor.getValue();
            assertThat(passedPage.equals(page)).isTrue();
            assertThat(passedSize.equals(size)).isTrue();
        }

        @Test
        @DisplayName("페이지와 사이즈 없이 상품 목록을 조회하면 페이지 0, 사이즈 10을 기준으로 목록을 조회하여 반환한다.")
        void readAllItemNoPageAndSize() throws Exception {
            // given
            int page = 0;
            int size = 10;

            PageRequest pageRequest = PageRequest.of(page, size);
            int startIdx = (int) pageRequest.getOffset();
            int endIdx = Math.min(startIdx + pageRequest.getPageSize(), testItems.size());

            when(itemService.readAllItems(page, size))
                    .thenReturn(this.testItems.subList(startIdx, endIdx));

            // when
            ResultActions resultActions = mockMvc.perform(get("/items"));

            // then
            // httpStatus 와 contentType 검증
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            // data 검증
            int resultIdx = 0;
            int itemsIdx = startIdx;

            for (; resultIdx < size; resultIdx++, itemsIdx++) {
                String base = String.format("$.data[%d]", resultIdx);
                resultActions
                        .andExpect(jsonPath(base + ".id").value(this.testItems.get(itemsIdx).getId().toString()))
                        .andExpect(jsonPath(base + ".name").value(this.testItems.get(itemsIdx).getName()))
                        .andExpect(jsonPath(base + ".price").value(this.testItems.get(itemsIdx).getPrice()))
                        .andExpect(jsonPath(base + ".createdAt").value(this.testItems.get(itemsIdx).getCreatedAt().format(dateTimeFormatter)))
                        .andExpect(jsonPath(base + ".updatedAt").value(this.testItems.get(itemsIdx).getUpdatedAt().format(dateTimeFormatter)));
            }

            var pageCaptor = ArgumentCaptor.forClass(Integer.class);
            var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(itemService, times(1)).readAllItems(pageCaptor.capture(), sizeCaptor.capture());

            var passedPage = pageCaptor.getValue();
            var passedSize = sizeCaptor.getValue();
            assertThat(passedPage.equals(page)).isTrue();
            assertThat(passedSize.equals(size)).isTrue();
        }

        @Test
        @DisplayName("페이지와 없이 상품 목록을 조회하면 페이지 0을 기준으로 목록을 조회하여 반환한다.")
        void readAllItemNoPage() throws Exception {
            // given
            int page = 0;
            int size = 10;

            PageRequest pageRequest = PageRequest.of(page, size);
            int startIdx = (int) pageRequest.getOffset();
            int endIdx = Math.min(startIdx + pageRequest.getPageSize(), testItems.size());

            when(itemService.readAllItems(page, size))
                    .thenReturn(this.testItems.subList(startIdx, endIdx));

            // when
            ResultActions resultActions = mockMvc.perform(get("/items")
                    .param("size", String.valueOf(size)));

            // then
            // httpStatus 와 contentType 검증
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            // data 검증
            int resultIdx = 0;
            int itemsIdx = startIdx;

            for (; resultIdx < size; resultIdx++, itemsIdx++) {
                String base = String.format("$.data[%d]", resultIdx);
                resultActions
                        .andExpect(jsonPath(base + ".id").value(this.testItems.get(itemsIdx).getId().toString()))
                        .andExpect(jsonPath(base + ".name").value(this.testItems.get(itemsIdx).getName()))
                        .andExpect(jsonPath(base + ".price").value(this.testItems.get(itemsIdx).getPrice()))
                        .andExpect(jsonPath(base + ".createdAt").value(this.testItems.get(itemsIdx).getCreatedAt().format(dateTimeFormatter)))
                        .andExpect(jsonPath(base + ".updatedAt").value(this.testItems.get(itemsIdx).getUpdatedAt().format(dateTimeFormatter)));
            }

            var pageCaptor = ArgumentCaptor.forClass(Integer.class);
            var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(itemService, times(1)).readAllItems(pageCaptor.capture(), sizeCaptor.capture());

            var passedPage = pageCaptor.getValue();
            var passedSize = sizeCaptor.getValue();
            assertThat(passedPage.equals(page)).isTrue();
            assertThat(passedSize.equals(size)).isTrue();
        }

        @Test
        @DisplayName("사이즈 없이 상품 목록을 조회하면 사이즈 10을 기준으로 목록을 조회하여 반환한다.")
        void readAllItemNoSize() throws Exception {
            // given
            int page = 0;
            int size = 10;

            PageRequest pageRequest = PageRequest.of(page, size);
            int startIdx = (int) pageRequest.getOffset();
            int endIdx = Math.min(startIdx + pageRequest.getPageSize(), testItems.size());

            when(itemService.readAllItems(page, size))
                    .thenReturn(this.testItems.subList(startIdx, endIdx));

            // when
            ResultActions resultActions = mockMvc.perform(get("/items")
                    .param("page", String.valueOf(page)));

            // then
            // httpStatus 와 contentType 검증
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            // data 검증
            int resultIdx = 0;
            int itemsIdx = startIdx;

            for (; resultIdx < size; resultIdx++, itemsIdx++) {
                String base = String.format("$.data[%d]", resultIdx);
                resultActions
                        .andExpect(jsonPath(base + ".id").value(this.testItems.get(itemsIdx).getId().toString()))
                        .andExpect(jsonPath(base + ".name").value(this.testItems.get(itemsIdx).getName()))
                        .andExpect(jsonPath(base + ".price").value(this.testItems.get(itemsIdx).getPrice()))
                        .andExpect(jsonPath(base + ".createdAt").value(this.testItems.get(itemsIdx).getCreatedAt().format(dateTimeFormatter)))
                        .andExpect(jsonPath(base + ".updatedAt").value(this.testItems.get(itemsIdx).getUpdatedAt().format(dateTimeFormatter)));
            }

            var pageCaptor = ArgumentCaptor.forClass(Integer.class);
            var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(itemService, times(1)).readAllItems(pageCaptor.capture(), sizeCaptor.capture());

            var passedPage = pageCaptor.getValue();
            var passedSize = sizeCaptor.getValue();
            assertThat(passedPage.equals(page)).isTrue();
            assertThat(passedSize.equals(size)).isTrue();
        }

        @Test
        @DisplayName("페이지를 -1 로 상품 목록을 조회하면 400과 실패 메시지를 반환한다.")
        void readAllItemPageIsInvalid() throws Exception {
            int page = -1;
            int size = 10;

            mockMvc
                    .perform(get("/items")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("readAllItems.page: 페이지는 0 이상이여야 합니다."));
        }

        @Test
        @DisplayName("사이즈를 0 으로 상품 목록을 조회하면 400과 실패 메시지를 반환한다.")
        void readAllItemSizeIsInvalid() throws Exception {
            int page = 0;
            int size = 0;

            mockMvc
                    .perform(get("/items")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("readAllItems.size: 사이즈는 1 이상이여야 합니다."));
        }
    }

    @Test
    @DisplayName("상품명, 상품 가격을 이용해 상품을 등록 한다.")
    void createItem() throws Exception {
        // given
        when(itemService.createItem(testItem.getName(), testItem.getPrice()))
                .thenReturn(testItem);

        // when & then
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(testItem.getName(), testItem.getPrice()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(testItem.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testItem.getName()))
                .andExpect(jsonPath("$.data.price").value(testItem.getPrice()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(testItem.getCreatedAt().format(this.dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value(testItem.getUpdatedAt().format(this.dateTimeFormatter)));

        // service 호출 시 name, price 가 동일하게 전달되는지 검증
        var nameCaptor = ArgumentCaptor.forClass(String.class);
        var priceCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(itemService, times(1)).createItem(nameCaptor.capture(), priceCaptor.capture());

        var passedName = nameCaptor.getValue();
        var passedPrice = priceCaptor.getValue();
        assertThat(testItem.getName().equals(passedName)).isTrue();
        assertThat(testItem.getPrice().equals(passedPrice)).isTrue();
    }

    @DisplayName("상품명 없이 상품을 등록하면 400을 반환한다.")
    @Test
    void createItemNoName() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(null, testItem.getPrice()))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품 가격 없이 상품을 등록하면 400을 반환한다.")
    @Test
    void createItemNoPrice() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(testItem.getName(), null))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품명과 상품 가격 없이 상품을 등록하면 400을 반환한다.")
    @Test
    void createItemNoNameAndPrice() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(null, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 조회하면 해당 상품 데이터를 응답한다.")
    void readExistingItem() throws Exception {
        // given
        when(this.itemService.readItem(anyLong()))
                .thenReturn(Optional.of(this.testItem));

        // when & then
        this.mockMvc
                .perform(get("/items/" + this.testItem.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(this.testItem.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(this.testItem.getName()))
                .andExpect(jsonPath("$.data.price").value(this.testItem.getPrice()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(this.testItem.getCreatedAt().format(this.dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value(this.testItem.getUpdatedAt().format(this.dateTimeFormatter)));
        this.verifyItemServiceReadItem();
    }

    @Test
    @DisplayName("존재하지 않는 상품 아이디로 조회하면 404 에러를 응답한다.")
    void readNotExistingItem() throws Exception {
        // given
        when(this.itemService.readItem(anyLong()))
                .thenReturn(Optional.empty());

        // when & then
        this.mockMvc
                .perform(get("/items/" + this.testItem.getId()))
                .andExpect(status().isNotFound());
        this.verifyItemServiceReadItem();
    }

    private void verifyItemServiceReadItem() {
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(this.itemService, times(1)).readItem(idCaptor.capture());
        var capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(this.testItem.getId());
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 상품의 이름 수정을 요청하면 수정한 상품 데이터를 응답한다.")
    void updateItemName() throws Exception {
        // given
        var updatedName = this.testItem.getName() + " (수정됨)";
        var updatedItem = this.testItem.toBuilder().name(updatedName).build();
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(updatedItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(updatedName, null))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(updatedItem.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.data.price").value(updatedItem.getPrice()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(updatedItem.getCreatedAt().format(this.dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value(updatedItem.getUpdatedAt().format(this.dateTimeFormatter)));
        this.verifyItemServiceUpdateItem(updatedName, null);
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 상품의 가격 수정을 요청하면 수정한 상품 데이터를 응답한다.")
    void updateItemPrice() throws Exception {
        // given
        var updatedPrice = this.testItem.getPrice() + 10_000;
        var updatedItem = this.testItem.toBuilder().price(updatedPrice).build();
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(updatedItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(null, updatedPrice))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(updatedItem.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.data.price").value(updatedItem.getPrice()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(updatedItem.getCreatedAt().format(this.dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value(updatedItem.getUpdatedAt().format(this.dateTimeFormatter)));
        this.verifyItemServiceUpdateItem(null, updatedPrice);
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 상품의 이름, 가격 수정을 요청하면 수정한 상품 데이터를 응답한다.")
    void updateItem() throws Exception {
        // given
        var updatedName = this.testItem.getName() + " (수정됨)";
        var updatedPrice = this.testItem.getPrice() + 10_000;
        var updatedItem = this.testItem.toBuilder().name(updatedName).price(updatedPrice).build();
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(updatedItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(updatedName, updatedPrice))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(updatedItem.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.data.price").value(updatedItem.getPrice()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(updatedItem.getCreatedAt().format(this.dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value(updatedItem.getUpdatedAt().format(this.dateTimeFormatter)));
        this.verifyItemServiceUpdateItem(updatedName, updatedPrice);
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 빈 내용으로 수정을 요청하면 400 을 응답한다.")
    void updateItemEmpty() throws Exception {
        // given
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(this.testItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(null, null))))
                .andExpect(status().isBadRequest());
        verify(this.itemService, never()).updateItem(anyLong(), nullable(String.class), nullable(Integer.class));
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 상품의 이름 수정을 빈 문자열로 요청하면 400 예외를 응답한다.")
    void updateItemNameEmpty() throws Exception {
        // given
        var updatedName = "";
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(this.testItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(updatedName, null))))
                .andExpect(status().isBadRequest());
        verify(this.itemService, never()).updateItem(anyLong(), nullable(String.class), nullable(Integer.class));
    }

    @Test
    @DisplayName("존재하는 상품 아이디로 상품의 가격 수정을 음수로 요청하면 400 예외를 응답한다.")
    void updateItemPriceNegative() throws Exception {
        // given
        var updatedPrice = -10_000;
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenReturn(this.testItem);

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(null, updatedPrice))))
                .andExpect(status().isBadRequest());
        verify(this.itemService, never()).updateItem(anyLong(), nullable(String.class), nullable(Integer.class));
    }

    @Test
    @DisplayName("존재하지 않는 상품 아이디로 수정을 요청하면 400 예외를 응답한다.")
    void updateNotExistingItem() throws Exception {
        // given
        var updatedName = this.testItem.getName() + " (수정됨)";
        var updatedPrice = this.testItem.getPrice() + 10_000;
        when(this.itemService.updateItem(anyLong(), nullable(String.class), nullable(Integer.class)))
                .thenThrow(new CustomException(ExceptionCode.NO_ITEM_TO_UPDATE));

        // when & then
        this.mockMvc
                .perform(patch("/items/" + this.testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsBytes(
                                new UpdateItemRequest(updatedName, updatedPrice))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ExceptionCode.NO_ITEM_TO_UPDATE.getMessage()));
        this.verifyItemServiceUpdateItem(updatedName, updatedPrice);
    }

    private void verifyItemServiceUpdateItem(String name, Integer price) {
        var idCaptor = ArgumentCaptor.forClass(Long.class);
        var nameCaptor = ArgumentCaptor.forClass(String.class);
        var priceCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(this.itemService, times(1))
                .updateItem(idCaptor.capture(), nameCaptor.capture(), priceCaptor.capture());

        var capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(this.testItem.getId());

        var capturedName = nameCaptor.getValue();
        assertThat(capturedName).isEqualTo(name);

        var capturedPrice = priceCaptor.getValue();
        assertThat(capturedPrice).isEqualTo(price);
    }
}