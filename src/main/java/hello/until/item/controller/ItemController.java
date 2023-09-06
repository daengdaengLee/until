package hello.until.item.controller;

import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.response.CreateItemResponse;
import hello.until.item.dto.response.ReadItemResponse;
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
    public ReadItemResponse readItem(@PathVariable long id) {
        return this.itemService.readItem(id)
                .map(ReadItemResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public CreateItemResponse createItem(@RequestBody @Validated CreateItemRequest createItemRequest) {
        String name = createItemRequest.name();
        Integer price = createItemRequest.price();

        return new CreateItemResponse(this.itemService.createItem(name, price));
    }
}
