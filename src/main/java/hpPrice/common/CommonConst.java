package hpPrice.common;

import java.util.regex.Pattern;

public class CommonConst {
    private CommonConst() {}

    public static final String DC_GALL_URL = "https://gall.dcinside.com/mgallery/board/lists/?id=sff";
    public static final String DC_TAB_PAGE_QUERY = "&search_head=40&page=";

    public static final String DC_POST_URL = "https://gall.dcinside.com/mgallery/board/view/?id=sff";
    public static final String DC_POST_NUM_QUERY = "&no=";

    public static final String DC_IMG_HOST_BEFORE = "dcimg8"; // dcimg6 / dcimg8 / dcimg9 // TODO 여기 있는 호스트를 전부 아래 호스트로 변경하기
    public static final String DC_IMG_HOST_AFTER = "dcimg3"; // dcimg1 / dcimg2 / dcimg3 / dcimg4 / dcimg7

    public static final String DC_CON_HOST_BEFORE = "dcimg5";
    public static final String DC_CON_HOST_AFTER = "dcimg1"; // 디시콘은 dcimg1만 가능



    public static final String NV_LOGIN_URL = "https://nid.naver.com/nidlogin.login?url=https%3A%2F%2Fsection.cafe.naver.com%2Fca-fe%2Fhome"; // 여기로 해야 치지직 관련 쿠키 안 생김
    public static final String NV_POST_LIST_URL = "https://cafe.naver.com/ArticleList.nhn?search.clubid=11196414&search.menuid=";
    public static final String NV_PAGE_QUERY = "&search.page=";

    public static final String NV_CAFE_URL = "https://cafe.naver.com/drhp/";
    public static final String NV_POST_URL = "https://cafe.naver.com/ca-fe/cafes/11196414/articles/";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    public static final int MAX_RETRY_COUNT = 10;
    public static final int TIME_OUT = 60000;
    public static final int SLEEP_TIME = 500;
    public static final int LOGIN_SLEEP_TIME = 3000;


    public static final Pattern REMOVE_PATTERN = Pattern.compile("번호 :|연락처 :|번호:|연락처:|010-" +
            "|@ 영상통화|★사기|☆ 신입회원|★ 신입회원|☆ 구매글은|☆ 모든 장터|☆ 해외구매|@ 택배거래는" +
        "|영상통화 & 카카오톡 영상통화 이용하여,|반드시, 제품 확인하시고 거래하세요.|@ 중고장터 사기 예방|https://cafe.naver.com/drhp/2141514" +
            "|@ 가격정보 지우는|@ 시세조작|## 렌탈 제품|@ 거래중일 경우|## 불가능시|@ 아래 양식");


    public static final Pattern PRICE_PATTERN = Pattern.compile("가격 :|가격:|금액:|금액 :");
}
