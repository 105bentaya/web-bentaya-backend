package org.scouts105bentaya.shared.specification;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@ToString
public class SpecificationFilter {
    private String sortedBy = "id";
    private boolean asc = true;
    private int page;
    private int countPerPage;

    public void setUnpaged() {
        this.page = -1;
    }

    public Pageable getPageable() {
        return page < 0 || countPerPage < 1 ? Pageable.unpaged(getSort()) : PageRequest.of(page, countPerPage, getSort());
    }

    private Sort getSort() {
        return asc ?
            Sort.by(getSortedBy()).and(Sort.by("id")) :
            Sort.by(getSortedBy()).descending().and(Sort.by("id"));
    }
}
