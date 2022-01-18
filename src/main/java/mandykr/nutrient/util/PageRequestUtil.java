package mandykr.nutrient.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@Getter
@Setter
public class PageRequestUtil {
    private int page;
    private int size;

    public PageRequestUtil() {
        page = 1;
        size = 10;
    }

    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
