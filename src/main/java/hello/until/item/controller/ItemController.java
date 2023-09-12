package hello.until.item.controller;

import hello.until.auth.PrincipalDetails;
import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.item.dto.request.CreateItemRequest;
import hello.until.item.dto.request.UpdateItemRequest;
import hello.until.item.dto.response.ItemResponse;
import hello.until.item.dto.response.ReadAllItemResponse;
import hello.until.item.service.ItemService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ItemResponse readItem(@PathVariable long id) {
        return this.itemService.readItem(id)
                .map(ItemResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ReadAllItemResponse readAllItems(
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "페이지는 0 이상이여야 합니다.") Integer page,
            @RequestParam(required = false, defaultValue = "10")
            @Min(value = 1, message = "사이즈는 1 이상이여야 합니다.") Integer size) {
        return new ReadAllItemResponse(this.itemService.readAllItems(page, size));
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

    @DeleteMapping("/{id}")
    public void deleteItem(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id) {
        if (principalDetails == null) {
            throw new CustomException(ExceptionCode.NOT_AUTHENTICATED);
        }
        this.itemService.deleteItem(id);
    }
}
