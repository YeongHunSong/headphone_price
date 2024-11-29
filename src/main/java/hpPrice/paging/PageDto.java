package hpPrice.paging;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PageDto {
    
    private int page;
    private int pageView;
    private int pageCount;
    
    public void pageValidation() { // pageView 변경 기능을 위한 modelAttribute 용
        this.page = page <= 0 ? 1 : page;
        this.pageCount = (page - 1) * pageView;
    }

    private PageDto() {
        this.page = 1;
        this.pageCount = 0;
        this.pageView = 50; // 노출할 게시글 개수
    }

    PageDto(int page, int pageView) { // 기존 로직용 (modelAttribute 를 사용하지 않고)
        this.page = page;
        this.pageView = pageView;
    }

    public static PageDto create(int page, int pageView) {
        return new PageDto(page, pageView);
    }
}
