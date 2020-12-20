package com.leoschulmann.roboquote.WebFront.security;

import com.leoschulmann.roboquote.WebFront.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Route(value = "register", layout = MainLayout.class)
public class RegisterUser extends VerticalLayout {
    public RegisterUser(UserDao userDao) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button button = new Button("Submit");

        button.addClickListener(c ->
                userDao.save(new User(username.getValue(), bcrypt.encode(password.getValue()), "user"))
        );

        //todo make more advanced

        add(new H2("Register new user"), username, password, button);
    }
}
