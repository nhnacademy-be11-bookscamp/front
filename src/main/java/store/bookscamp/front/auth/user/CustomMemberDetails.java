package store.bookscamp.front.auth.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomMemberDetails implements UserDetails, TokenDetails, OAuth2User {

    private String username;
    private final String role;
    private final String rawJwtToken;
    private Map<String, Object> attributes;

    public CustomMemberDetails(String role, String rawJwtToken) {
        this.role = role;
        this.rawJwtToken = rawJwtToken;
    }


    @Override
    public String getRawJwtToken() {
        return this.rawJwtToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority( role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    @Override
    public Map<String, Object> getAttributes(){
        return this.attributes;
    }

    @Override
    public String getName(){
        if(this.attributes == null){
            return null;
        }
        return String.valueOf(this.attributes.get("idNo"));
    }

    public void setAttributes(Map<String,Object> attributes){
        this.attributes = attributes;
    }
}
