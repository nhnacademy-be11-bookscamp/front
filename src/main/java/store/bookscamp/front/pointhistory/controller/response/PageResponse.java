package store.bookscamp.front.pointhistory.controller.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {
    private List<T> content;
    private int number;         // 현재 페이지 번호
    private int totalPages;     // 총 페이지 수
    private long totalElements; // 전체 건수
    private boolean first;
    private boolean last;
}
