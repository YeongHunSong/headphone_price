package hpPrice.search;

import lombok.Data;

@Data
public class SearchCond {

    // POST_LIST (title, nickname, userId)

    private String searchType;

    private String keyword;

    private SearchCond() {
    }
}
