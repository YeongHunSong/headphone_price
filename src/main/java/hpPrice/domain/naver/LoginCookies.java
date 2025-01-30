package hpPrice.domain.naver;

import lombok.Data;

@Data
public class LoginCookies {
    private String desc;

    private String jsonCookies;

    private LoginCookies() {
    }

    private LoginCookies(String desc, String jsonCookies) {
        this.desc = desc;
        this.jsonCookies = jsonCookies;
    }

    public static LoginCookies newLoginCookies(String desc, String jsonCookies) {
        return new LoginCookies(desc, jsonCookies);
    }
}

