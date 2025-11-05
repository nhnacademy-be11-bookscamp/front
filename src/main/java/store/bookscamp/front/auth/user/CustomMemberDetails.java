package store.bookscamp.front.auth.user;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

@Getter
public class CustomMemberDetails extends User {
    private final Long memberId;
    private final String rawJwtToken;

    public CustomMemberDetails(Long memberId, String username, String role, String rawJwtToken) {
        super(username, "", Collections.singletonList(new SimpleGrantedAuthority(role)));
        this.memberId = memberId;
        this.rawJwtToken = rawJwtToken;
    }
}