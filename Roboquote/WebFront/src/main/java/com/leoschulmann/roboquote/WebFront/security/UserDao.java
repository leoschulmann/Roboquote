package com.leoschulmann.roboquote.WebFront.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserDao {

    private final EntityManager em;

    public UserDao(EntityManager em) {
        this.em = em;
    }

    public Optional<User> get(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> getByUserName(String user) {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.username = :param", User.class)
                .setParameter("param", user);
        List<User> list = query.getResultList();
        return Optional.ofNullable(list.isEmpty() ? null : list.get(0));
    }

    public List<User> getAll() {
        Query query = em.createQuery("SELECT u FROM User u");
        return query.getResultList();
    }

    @Transactional
    public void save(User user) {
        em.persist(user);
    }

    @Transactional
    public void update(User user, String[] params) {
        user.setUsername(Objects.requireNonNull(params[1], "Username cannot be null"));
        user.setPassword(Objects.requireNonNull(params[0], "Password cannot be null"));
        em.persist(user);
    }

    @Transactional
    public void delete(User user) {
        em.remove(user);
    }
}
