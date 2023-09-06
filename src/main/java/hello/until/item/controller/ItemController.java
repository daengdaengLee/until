package hello.until.item.controller;

import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.response.CreateItemResponse;
import hello.until.item.dto.response.ReadAllItemResponse;
import hello.until.item.dto.response.ReadItemResponse;
import hello.until.item.service.ItemService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ReadItemResponse readItem(@PathVariable long id) {
        return this.itemService.readItem(id)
                .map(ReadItemResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ReadAllItemResponse readAllItem(@RequestParam @Min(value = 0, message = "페이지는 0 이상이여야 합니다.") Integer page,
                                           @RequestParam @Min(value = 1, message = "사이즈는 1 이상이여야 합니다.") Integer size) {
        return new ReadAllItemResponse(this.itemService.readAllItems(page, size));
    }

    @PostMapping
    public CreateItemResponse createItem(@RequestBody @Validated CreateItemRequest createItemRequest) {
        String name = createItemRequest.name();
        Integer price = createItemRequest.price();

        return new CreateItemResponse(this.itemService.createItem(name, price));
    }
}
