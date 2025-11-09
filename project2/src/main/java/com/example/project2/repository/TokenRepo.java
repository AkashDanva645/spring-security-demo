package com.example.project2.repository;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TokenRepo {

    private record TokenRecord(String token, String username) {};

    private List<TokenRecord> lst = new ArrayList<>();

    public String getUsername(String token) {
        TokenRecord tokenRecord = null;
        for (TokenRecord tr: lst) {
            if (tr.token.equals(token)) {
                return tr.username;
            }
        }
        return null;
    }

    public void saveToken(String token, String username) {
        TokenRecord tokenRecord = null;
        for (TokenRecord tr: lst) {
            if (tr.username.equals(username)) {
                tokenRecord = tr;
            }
        }
        if (tokenRecord != null) lst.remove(tokenRecord);
        lst.add(new TokenRecord(token, username));
    }
}
