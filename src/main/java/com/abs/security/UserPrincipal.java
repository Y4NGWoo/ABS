package com.abs.security;

import com.abs.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long userNo;
    private final String userEmail;
    private final String userName;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userNo, String userEmail, String userName,
                         Collection<? extends GrantedAuthority> authorities) {
        this.userNo = userNo;
        this.userEmail = userEmail;
        this.userName = userName;
        this.authorities = authorities;
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getUserNo(),
                user.getUserEmail(),
                user.getUserName(),
                List.of(() -> "ROLE_USER")
        );
    }

    public Long getUserNo() { return userNo; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }

    // UserDetails 구현
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    // 패스워드는 JWT 흐름에서는 사용 안 하지만 시그니처상 필요
    @Override public String getPassword() { return ""; }

    @Override public String getUsername() { return userEmail; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
