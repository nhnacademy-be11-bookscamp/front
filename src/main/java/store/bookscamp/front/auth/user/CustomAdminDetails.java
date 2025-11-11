package store.bookscamp.front.auth.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAdminDetails implements UserDetails, TokenDetails {

    private final String role;
    private final String rawJwtToken;

    public CustomAdminDetails(String role, String rawJwtToken) {
        this.role = role;
        this.rawJwtToken = rawJwtToken;
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
        return null;
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