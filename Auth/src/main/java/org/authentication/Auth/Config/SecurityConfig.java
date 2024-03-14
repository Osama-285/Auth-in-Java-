package org.authentication.Auth.Config;

import org.authentication.Auth.Security.Jwks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private RSAKey rsaKey;

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(AbstractHttpConfigurer::disable);

        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity
                .authorizeHttpRequests(
                        (authorize) -> authorize.requestMatchers("/api/signup").permitAll()
                                .requestMatchers("/api/login").permitAll()
                                .requestMatchers("/api/token").permitAll().requestMatchers("/api/user")
                                .hasAuthority("SCOPE_USER").requestMatchers("/api/users/{user_id}/update")
                                .hasAuthority("SCOPE_USER").requestMatchers("/api/users/updatePassword")
                                .authenticated().requestMatchers("/api/profile").authenticated()
                                .requestMatchers("/api/delete").authenticated());
        httpSecurity.sessionManagement(
                sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.oauth2ResourceServer((rs) -> rs.jwt((jwt) -> {
            try {
                jwt.decoder(jwtDecoder());
            } catch (JOSEException e) {

                e.printStackTrace();
            }
        }));
        return httpSecurity.build();
    }
}
