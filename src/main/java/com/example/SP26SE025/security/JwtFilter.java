package com.example.SP26SE025.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.SP26SE025.service.CustomUserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
                                        

        String token = null;
        Cookie[] cookies = request.getCookies();

        // 1. Lấy Token từ Cookie
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("jwt")) {
                    token = c.getValue();
                    break;
                }
            }
        }
        
        String username = null;
        boolean tokenValid = false;
        
        // 2. Xác thực Token
        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.getUsernameFromToken(token);
                    tokenValid = true;
                }
            } catch (Exception e) {
                // Token không hợp lệ (hết hạn, sai chữ ký, v.v.)
            }
        }
        
        // 3. THIẾT LẬP NGỮ CẢNH (Chỉ khi ngữ cảnh rỗng và username hợp lệ)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && tokenValid) {
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Kiểm tra tính hợp lệ lần cuối (Mặc dù đã kiểm tra ở bước 2)
            // if (jwtUtil.validateToken(token, userDetails)) { // Dùng nếu có hàm này
            
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // THIẾT LẬP NGỮ CẢNH BẢO MẬT
            SecurityContextHolder.getContext().setAuthentication(auth);
            // System.out.println("🔐 Authenticated with roles: " + userDetails.getAuthorities());
            // } 
        } 

        filterChain.doFilter(request, response);
    }
}