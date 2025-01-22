package hpPrice.common;

public class CommonConst {
    public static final String DC_GALL_NAME = "sff";
    public static final String DC_GALL_URL = "https://gall.dcinside.com/mgallery/board/lists/?id=" + DC_GALL_NAME;
    public static final String DC_TAB_PAGE_QUERY = "&search_head=40&page=";

    public static final String DC_POST_URL = "https://gall.dcinside.com/mgallery/board/view/?id=" + DC_GALL_NAME;
    public static final String DC_POST_NUM_QUERY = "&no=";

    public static final String DC_IMG_HOST_BEFORE = "dcimg8"; // dcimg6 / dcimg8 / dcimg9 // TODO 여기 있는 호스트를 전부 아래 호스트로 변경하기
    public static final String DC_IMG_HOST_AFTER = "dcimg3"; // dcimg1 / dcimg2 / dcimg3 / dcimg4 / dcimg7

    public static final String DC_CON_HOST_BEFORE = "dcimg5";
    public static final String DC_CON_HOST_AFTER = "dcimg1"; // 디시콘은 dcimg1만 가능


    public static final String NV_POST_LIST_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=11196414";
    public static final String NV_TAB_QUERY = "&search.menuid=";
    public static final String NV_PAGE_QUERY = "&search.page=";

    public static final String NV_TAB_EARPHONE = "&search.menuid=102";
    public static final String NV_TAB_DAC_AMP = "&search.menuid=192";
    public static final String NV_TAB_CABLE = "&search.menuid=42";

    public static final String NV_POST_URL = "https://cafe.naver.com/drhp/";




    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    public static final int MAX_RETRY_COUNT = 15; // TODO naver cafe 크롤링은 절대 죽으면 안 됨.
    public static final int TIME_OUT = 60000;
    public static final int SLEEP_TIME = 500;
    public static final int LOGIN_SLEEP_TIME = 1111;

    private CommonConst() {}
}
