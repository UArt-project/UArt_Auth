package net.uart.uart_auth.controllers;

import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthController {

    @Value("${openid.google.client-id}")
    private String CLIENT_ID;

    @Value("${openid.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${openid.google.redirect-uri}")
    private String REDIRECT_URI;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/auth")
    public RedirectView authorize() {
        String state = new BigInteger(130, new SecureRandom()).toString(32);

        UriComponents uriComponents =
                UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("accounts.google.com")
                        .path("/o/oauth2/v2/auth")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("response_type", "code")
                        .queryParam("scope", "openid")
                        .queryParam("state", state)
                        .encode()
                        .build();

        return new RedirectView(uriComponents.toString());
    }

    @GetMapping("/id")
    public String getId(@RequestParam("code") String code) {
        HttpPost post = new HttpPost("https://oauth2.googleapis.com/token");
        UrlEncodedFormEntity entity = getBody(code);
        post.setEntity(entity);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String result = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        String.format("Google returened status code: %s, with message: %s", response.getStatusLine().getStatusCode(), result));
            }
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.getString("id_token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UrlEncodedFormEntity getBody(String code) {
        List<Header> body = new ArrayList<>();
        body.add(new BasicHeader("client_id", CLIENT_ID));
        body.add(new BasicHeader("client_secret", CLIENT_SECRET));
        body.add(new BasicHeader("code", code));
        body.add(new BasicHeader("grant_type", "authorization_code"));
        body.add(new BasicHeader("redirect_uri", REDIRECT_URI));
        return new UrlEncodedFormEntity(body, StandardCharsets.UTF_8);
    }

}
