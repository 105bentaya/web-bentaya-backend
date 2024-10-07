package org.scouts105bentaya.shared.specification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public abstract class SpecificationFilter {
    private String sortedBy = "id";
    private boolean asc = true;
    private int page;
    private int countPerPage;

    public Pageable getPageable() {
        return page < 0 ? Pageable.unpaged() : PageRequest.of(page, countPerPage);
    }

    public Sort getSort() {
        return asc ?
            Sort.by(getSortedBy()).and(Sort.by("id")) :
            Sort.by(getSortedBy()).descending().and(Sort.by("id"));
    }
}
