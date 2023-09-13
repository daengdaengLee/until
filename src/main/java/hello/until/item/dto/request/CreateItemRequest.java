package hello.until.item.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateItemRequest(
        @NotEmpty
        String name,
        @NotNull
        Integer price) {
}