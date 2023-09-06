package hello.until.item.controller;

import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.request.UpdateItemRequest;
import hello.until.item.dto.response.ItemResponse;
import hello.until.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemResponse readItem(@PathVariable long id) {
        return this.itemService.readItem(id)
                .map(ItemResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ItemResponse createItem(@RequestBody @Validated CreateItemRequest createItemRequest) {
        String name = createItemRequest.name();
        Integer price = createItemRequest.price();

        return new ItemResponse(this.itemService.createItem(name, price));
    }

    @PatchMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @RequestBody UpdateItemRequest updateItemRequest) {
        updateItemRequest.validate();
        var updatedItem = this.itemService.updateItem(id, updateItemRequest.name(), updateItemRequest.price());
        return new ItemResponse(updatedItem);
    }
}
