package store.bookscamp.front.auth.dto;

import lombok.Getter;

@Getter
public class LoginAuthDetails {
    private final String rawAccessToken;
    private final String rtCookieString;
    private final String name;

    public LoginAuthDetails(String rawAccessToken, String rtCookieString,String name){
        this.rawAccessToken = rawAccessToken;
        this.rtCookieString = rtCookieString;
        this.name = name;
    }
}
