package hpPrice.search;

import lombok.Data;

@Data
public class SearchCond {

    // POST (title, nickname, userId)

    private String searchType;

    private String keyword;

    private SearchCond() {
    }
}
