package hpPrice.common;

import java.time.format.DateTimeFormatter;

public class CommonConst {
    public static final String DC_GALL_NAME = "sff";
    public static final String DC_GALL_URL = "https://gall.dcinside.com/mgallery/board/lists/?id=" + DC_GALL_NAME;
    public static final String DC_TAB_PAGE = "&search_head=40&page=";

    public static final String DC_POST_URL = "https://gall.dcinside.com/mgallery/board/view/?id=" + DC_GALL_NAME;
    public static final String DC_POST_NUM = "&no=";

    public static final String DC_IMG_HOST_BEFORE = "dcimg8"; // dcimg6 / dcimg8 / dcimg9 // TODO 여기 있는 호스트를 전부 아래 호스트로 변경하기
    public static final String DC_IMG_HOST_AFTER = "dcimg3"; // dcimg1 / dcimg2 / dcimg3 / dcimg4 / dcimg7

    public static final String DC_CON_HOST_BEFORE = "dcimg5";
    public static final String DC_CON_HOST_AFTER = "dcimg1"; // 디시콘은 dcimg1만 가능

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int MAX_RETRY_COUNT = 3;
    public static final int TIME_OUT = 30000;
    public static final int SLEEP_TIME = 500;

    private CommonConst() {}
}
