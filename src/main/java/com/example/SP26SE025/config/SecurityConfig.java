package com.example.SP26SE025.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.SP26SE025.repository.UserRepository;
import com.example.SP26SE025.security.CustomOAuth2UserService;
import com.example.SP26SE025.security.JwtFilter;
import com.example.SP26SE025.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean
    // public CustomOAuth2UserService customOAuth2UserService() {
    //     return new CustomOAuth2UserService(userRepository, new BCryptPasswordEncoder());
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login", "/register",
                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                "/authenticate", "/oauth2/**", "/login/oauth2/**",
                                "/api/reports/**") // Allow AI-Service API without auth
                        .permitAll()
                        // Phân quyền truy cập các đường dẫn
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/clinic/**").hasRole("CLINIC")
                        .requestMatchers("/doctor/**").hasRole("DOCTOR") // Thêm quyền cho Doctor
                        .requestMatchers("/staff/**").hasRole("STAFF")
                        .requestMatchers("/customer/**", "/profile", "/test-services/**", "/menstrual_cycle/**")
                        .hasRole("CUSTOMER")
                        .requestMatchers("/authenticateAdmin", "/loginAdmin").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")

                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            String redirectUrl = "/login?error=true"; // Mặc định nếu lỗi

                            for (var authority : authorities) {
                                String roleName = authority.getAuthority();

                                if (roleName.equals("ROLE_CLINIC")) {
                                    redirectUrl = "/clinic/home";
                                    break;
                                } else if (roleName.equals("ROLE_DOCTOR")) {
                                    redirectUrl = "/doctor/home";
                                    break;
                                } else if (roleName.equals("ROLE_CUSTOMER")) {
                                    redirectUrl = "/customer/home";
                                    break;
                                } else if (roleName.equals("ROLE_ADMIN")) {
                                    redirectUrl = "/admin/users";
                                    break;
                                }
                            }
                            response.sendRedirect(redirectUrl);
                        })

                        .failureUrl("/login?error=true")
                        .permitAll())

                /////////

                // .oauth2Login(oauth2 -> oauth2
                //         .loginPage("/login")
                //         .userInfoEndpoint(userInfo -> userInfo
                //                 .userService(customOAuth2UserService()))
                //         .successHandler((request, response, authentication) -> {
                //             response.sendRedirect("/customer/home");
                //         })
                //         .failureHandler((request, response, exception) -> {
                //             response.sendRedirect("/login?oauth2_error=true");
                //         }))

                //////////

                /////////
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}