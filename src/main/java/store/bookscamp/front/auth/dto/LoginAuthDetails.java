package store.bookscamp.front.auth.dto;

import lombok.Getter;

@Getter
public class LoginAuthDetails {
    private final String rawAccessToken;
    private final String rtCookieString;

    public LoginAuthDetails(String rawAccessToken, String rtCookieString){
        this.rawAccessToken = rawAccessToken;
        this.rtCookieString = rtCookieString;
    }
}
