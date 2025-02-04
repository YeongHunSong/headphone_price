package hpPrice.domain.naver;

import lombok.Data;

@Data
public class LoginCookies {
    private String cookieDesc;

    private String jsonCookies;

    private LoginCookies() {
    }

    private LoginCookies(String cookieDesc, String jsonCookies) {
        this.cookieDesc = cookieDesc;
        this.jsonCookies = jsonCookies;
    }

    public static LoginCookies newLoginCookies(String cookieDesc, String jsonCookies) {
        return new LoginCookies(cookieDesc, jsonCookies);
    }
}

