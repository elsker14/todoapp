package org.security.sample.to_do_list_app.config;

import org.security.sample.to_do_list_app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// Final Project - Step #5 - to enable Spring Security's web security support.
@EnableWebSecurity
public class SecurityConfig {

    // Final Project - Step #3 - retrieve custom user & pass defined in application.properties
    @Value("${spring.security.user.name}")
    private String propsUsername;

    @Value("${spring.security.user.password}")
    private String propsPassword;

    // Final Project - Step #5 - inject user repo to retrieve users' list from db
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /*
    // Final Project - Step #3 - custom user memory in memory, meaning they reset each time we rerun
    // old approach before step #5
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

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(user1);
        manager.createUser(user2);
        manager.createUser(user3);

        return manager;
    }
    */

    // Final Project - Step #5 - configure both in memory users and database ones to be available
    // Now, that we want to be able to use both in memory users, and users stored in a db, we must change
    // the manager builder to be one more flexible
    // InMemoryUserDetailsManager -> AuthenticationManagerBuilder
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Configure custom UserDetailsService for database users
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        // Configure in-memory users
        auth.inMemoryAuthentication()
                .withUser(propsUsername)
                .password(passwordEncoder().encode(propsPassword))
                .roles("USER");

        auth.inMemoryAuthentication()
                .withUser("guest")
                .password(passwordEncoder().encode("guest123"))
                .roles("GUEST");

        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN");
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
                                .requestMatchers("/", "/login", "/sign-up", "/webjars/**").permitAll()  // Publicly accessible
                                .requestMatchers("/h2-console/**").hasRole("ADMIN")
                                .requestMatchers("/todo-list/add").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/todo-list/update").hasRole("ADMIN")
                                .requestMatchers("/todo-list/delete").hasRole("ADMIN")
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


/*
    Why did we add the spring.main.allow-circular-references=true property to solve the bean creation issue?

    The spring.main.allow-circular-references=true property allows Spring
    to handle circular references between beans during the creation process.
    Here’s how it helped to solve your issue:

    Understanding Circular References
        A circular reference occurs
        when two or more beans depend on each other in such a way that they cannot be created without one another.
        For example, if Bean A depends on Bean B and Bean B depends on Bean A, it creates a circular reference.

        In your case, you had a circular reference involving the SecurityConfig and CustomUserDetailsService beans.
        When Spring tried to create these beans,
        it encountered a situation where it couldn't create one without the other being fully initialized first.

    How the Property Helps
        By default,
        Spring tries to resolve circular dependencies by creating bean instances and then injecting dependencies,
        but it doesn't always succeed, especially with complex configurations or specific conditions.

        The spring.main.allow-circular-references=true property explicitly tells Spring
        to allow and resolve circular references.
        This property enables a more lenient bean creation process:

        Bean Creation with Circular Dependencies:
            When Spring detects a circular reference,
            it will allow the creation of a partially initialized bean.
            This means that one of the beans will be created and its dependencies will be injected later.

        Dependency Injection Order:
            Spring will attempt to inject dependencies in a way that resolves the circular reference.
            It may use setters or other methods to inject dependencies after the initial bean creation.

        How It Applied to Your Case
            In your setup:
                SecurityConfig:
                    This configuration bean was trying to inject CustomUserDetailsService.
                CustomUserDetailsService:
                    This service might indirectly rely on other beans
                    that could be part of the security configuration.

            When Spring tried to create these beans, it ran into a circular reference problem and threw an exception.

            By setting spring.main.allow-circular-references=true; you allowed Spring to:
                Partially create the beans.
                Inject the dependencies after initial creation.
                Resolve the circular dependencies during the bean lifecycle.

        Best Practices
            While enabling circular references can be a quick fix,
            it's generally better to refactor the code to avoid such dependencies.
            Here are a few strategies:
                Decouple Beans:
                    Refactor your beans to reduce direct dependencies.
                    Use interfaces or abstract classes to break the direct circular dependency.
                Use @Lazy Annotation:
                    Annotate one of the dependent beans with
                    @Lazy to delay its initialization until it's actually needed.
                Refactor Configuration:
                    Split your configuration into multiple configuration classes
                    to isolate and manage dependencies more effectively.
     */