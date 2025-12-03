package store.bookscamp.front.pointhistory.controller.response;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int number;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(
                Collections.emptyList(),
                0,
                0,
                0L,
                true,
                true
        );
    }
}
