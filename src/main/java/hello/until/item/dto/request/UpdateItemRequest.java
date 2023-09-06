package hello.until.item.dto.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record UpdateItemRequest(String name, Integer price) {
    /**
     * @throws ResponseStatusException 검증에 실패한 경우 400 응답 코드의 예외를 던집니다.
     */
    public void validate() {
        if (this.name == null && this.price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 내용이 없습니다.");
        }

        if (this.name != null && this.name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "빈 상품 이름을 사용할 수 없습니다.");
        }

        if (this.price != null && this.price < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 가격은 음수일 수 없습니다.");
        }
    }
}
