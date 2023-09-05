package hello.until.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.entity.Item;
import hello.until.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("상품명, 상품 가격을 이용해 상품을 등록 한다.")
    void createItem() throws Exception {
        // given
        Mockito.when(itemService.createItem(testItem.getName(), testItem.getPrice()))
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

    @DisplayName("상품명 없이 상품을 등록 한다.")
    @Test
    void createItemNoName() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(null, testItem.getPrice()))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품 가격 없이 상품을 등록 한다.")
    @Test
    void createItemNoPrice() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(testItem.getName(), null))))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품명과 상품 가격 없이 상품을 등록 한다.")
    @Test
    void createItemNoNameAndPrice() throws Exception {
        mockMvc
                .perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CreateItemRequest(null, null))))
                .andExpect(status().isBadRequest());
    }
}