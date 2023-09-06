package hello.until.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.until.item.dto.request.CreateItemRequest;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void beforeEach() {
        testItem = Item.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10_000)
                .createdAt(LocalDateTime.now())
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
            int size = 10;

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

            for (;resultIdx < size; resultIdx++, itemsIdx++) {
                String base = String.format("$.data[%d]", resultIdx);
                resultActions
                    .andExpect(jsonPath(base + ".id").value(this.testItems.get(itemsIdx).getId().toString()))
                    .andExpect(jsonPath(base + ".name").value(this.testItems.get(itemsIdx).getName()))
                    .andExpect(jsonPath(base + ".price").value(this.testItems.get(itemsIdx).getPrice()))
                    .andExpect(jsonPath(base + ".createdAt").value(this.testItems.get(itemsIdx).getCreatedAt().toString().replaceAll("^0+|0+$", "")))
                    .andExpect(jsonPath(base + ".updatedAt").value(this.testItems.get(itemsIdx).getUpdatedAt().toString().replaceAll("^0+|0+$", "")));
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
        @DisplayName("페이지 없이 사이즈만으로 상품 목록을 조회하면 400을 반환한다.")
        void readAllItemNoPage() throws Exception {
            mockMvc
                    .perform(get("/items")
                        .param("size", String.valueOf(10)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사이즈 없이 페이지만으로 상품 목록을 조회하면 400을 반환한다.")
        void readAllItemNoSize() throws Exception {
            mockMvc
                    .perform(get("/items")
                            .param("page", String.valueOf(0)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지와 사이즈 없이 상품 목록을 조회하면 400을 반환한다.")
        void readAllItemNoPageAndSize() throws Exception {
            mockMvc
                    .perform(get("/items"))
                    .andExpect(status().isBadRequest());
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
                    .andExpect(content().string("readAllItem.page: 페이지는 0 이상이여야 합니다."));
        }

        @Test
        @DisplayName("사이즈를 0 으로 상품 목록을 조회하면 400과 실패 메시지를 반환한다.")
        void readAllItemSizeIsInvalid() throws Exception {
            int page = -1;
            int size = 10;

            mockMvc
                    .perform(get("/items")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("readAllItem.size: 사이즈는 1 이상이여야 합니다."));
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
                .andExpect(jsonPath("$.data.createdAt").value(testItem.getCreatedAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(testItem.getUpdatedAt().toString()));

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
}