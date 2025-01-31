package hpPrice.domain.common;

import lombok.Data;

@Data
public class SearchCond {

    // POST_LIST (title, nickname, userId)
    // POST (content)

    private String searchType;

    private String keyword;

    private SearchCond() {
    }
}
