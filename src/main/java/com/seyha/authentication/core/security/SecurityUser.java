package com.seyha.authentication.core.security;
import com.seyha.authentication.domain.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
    // Helper to get the underlying entity if needed (e.g. to get ID for JWT)
    private final User user;

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the Enum Role into a Spring Security Authority
        // Example: Role.ADMIN -> "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("Role_"+user.getRole().name()));
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
