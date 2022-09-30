package net.uart.uart_auth.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/auth")
    public String authorize() {
        return "This should return 403";
    }

    @GetMapping("/id")
    public String getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser user = (OidcUser) authentication.getPrincipal();
        return user.getIdToken().getTokenValue();
    }

}
