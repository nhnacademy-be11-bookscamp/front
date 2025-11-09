package store.bookscamp.front.auth.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomMemberDetails implements UserDetails, TokenDetails {

    private final Long memberId;
    private final String username;
    private final String role;
    private final String rawJwtToken;

    public CustomMemberDetails(Long memberId, String username, String role, String rawJwtToken) {
        this.memberId = memberId;
        this.username = username;
        this.role = role;
        this.rawJwtToken = rawJwtToken;
    }

    public Long getMemberId() {
        return memberId;
    }

    @Override
    public String getRawJwtToken() {
        return this.rawJwtToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}