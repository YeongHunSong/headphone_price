package hpPrice.common.paging;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PageDto {
    
    private int page;
    private int pageView;
    private int pageCount;
    
    public void pageValidation() {
        this.page = page <= 0 ? 1 : page;
        this.pageCount = (page - 1) * pageView;
    }

    private PageDto() {
        this.page = 1;
        this.pageCount = 0;
        this.pageView = 50; // 노출할 게시글 개수
    }
}
