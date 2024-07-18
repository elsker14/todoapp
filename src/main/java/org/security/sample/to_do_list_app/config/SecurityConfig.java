package org.security.sample.to_do_list_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Final Project - Step #3 - retrieve custom user & pass defined in application.properties
    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    // Final Project - Step #3 - custom user memory in memory, meaning they reset each time we rerun
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user3 = User.builder()
                .username("guest")
                .password(passwordEncoder.encode("guest123"))
                .roles("GUEST")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    // Final Project - Step #3 - use a custom password encoder instead of the withDefaultPasswordEncoder() method which is deprecated
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Final Project - Step #2 - create the custom security layer
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizeHttpRequestsIt -> authorizeHttpRequestsIt
                                // Final Project - Step #4 - added /sign-up
                                .requestMatchers("/", "/login", "/h2-console", "/webjars/**", "/sign-up" ).permitAll()  // Publicly accessible
                                .anyRequest().authenticated()   // All other requests require authentication
                )
                .formLogin(
                        formLoginIt -> formLoginIt
                                // Final Project - Step #4 - added redirection to custom login page
                                .loginPage("/login")    // Redirects to custom login page
                                .defaultSuccessUrl("/todo-list", true)  // Redirect to do list page
                                .permitAll()    // Allow everyone to see the login page
                )
                .logout(
                        logoutIt -> logoutIt
                                .logoutSuccessUrl("/login") // Redirect to login page after logout
                                .permitAll()    // Allow everyone to access the logout page
                )
                .csrf(
                        csrfIt -> csrfIt.disable()  // Disable CSRF protection
                )
                .headers(
                        headersIt -> headersIt.frameOptions(
                                frameOptionsItt -> frameOptionsItt.disable() // Disable frame options for H2 console
                        )
                );


        return http.build();
    }
}

/*
    The X-Frame-Options header is a security header that helps to protect against clickjacking attacks
    by controlling whether a browser should be allowed to render a page in a <frame>, <iframe>, or <object>.

    Disabling X-Frame-Options can be necessary in some scenarios:
        H2 Console:
            The H2 database console requires displaying its content in a frame.
            Disabling frame options allows the H2 console to function correctly.
        Legacy Applications:
            Some legacy applications may require being embedded in iframes,
            requiring the disabling of frame options.
 */