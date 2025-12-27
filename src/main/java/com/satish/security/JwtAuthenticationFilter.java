package com.satish.security;

import com.satish.document.User;
import com.satish.repository.UserRepository;
import com.satish.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token=null;
        String userId=null;
        if (authHeader !=null && authHeader.startsWith("Bearer")){
           token= authHeader.substring(7);
           try{
               jwtUtils.getUserIdFromToken(token);
           } catch (Exception e) {
               log.error("Token is not Valid/Avvailable");

           }
        }
        if (userId !=null && SecurityContextHolder.getContext().getAuthentication() == null){
            try{
               if(jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token)) {
                User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User Name not Found"));
                   UsernamePasswordAuthenticationToken authToken =new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
            }catch (Exception e){
                log.error("Exception Occur While Validating token");
            }
        }
        filterChain.doFilter(request,response);

    }
}
