package com.leoschulmann.roboquote.quoteservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {


    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String login;

    private String statement = "SELECT * FROM uzer WHERE username=?;";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection connection = DriverManager.getConnection(url, login, null)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet set = preparedStatement.executeQuery()) {
                    if (!set.next()) throw new UsernameNotFoundException("User not found " + username);
                    return User.builder()
                            .username(set.getString("username"))
                            .password(set.getString("password"))
                            .roles(set.getString("role"))
                            .build();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new UsernameNotFoundException("User not found " + username);
    }
}
