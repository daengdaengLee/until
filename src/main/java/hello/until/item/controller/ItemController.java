package hello.until.item.controller;

import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.response.CreateItemResponse;
import hello.until.item.dto.response.ReadItemResponse;
import hello.until.item.entity.Item;
import hello.until.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("")
    public CreateItemResponse createItem(CreateItemRequest createItemRequest) {
        String name = createItemRequest.name();
        Integer price = createItemRequest.price();

        if (name == null || name.isEmpty() || price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new CreateItemResponse(this.itemService.createItem(name, price));
    }
}
