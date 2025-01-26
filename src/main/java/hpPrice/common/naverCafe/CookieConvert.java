package hpPrice.common.naverCafe;

import lombok.Getter;
import lombok.ToString;
import org.openqa.selenium.Cookie;

import java.util.Date;

@Getter
@ToString
public class CookieConvert {
    private String name;
    private String value;
    private String domain;
    private String path;
    private Date expiry;
    private boolean isSecure;
    private boolean isHttpOnly;

    private CookieConvert() {}

    public CookieConvert(Cookie cookie) {
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.domain = cookie.getDomain();
        this.path = cookie.getPath();
        this.expiry = cookie.getExpiry();
        this.isSecure = cookie.isSecure();
        this.isHttpOnly = cookie.isHttpOnly();
    }

    public Cookie toSeleniumCookie() {
        return new Cookie(name, value, domain, path, expiry, isSecure, isHttpOnly);
    }
}
