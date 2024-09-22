package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {
    Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                      INSERT INTO users(name, email, password)
                      VALUES (:name, :email, :password)
                      """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("name", user.getName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword());
          Optional<User> userOptional = findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                int serialID = query.executeUpdate().getKey(Integer.class);
                user.setId(serialID);
            }
            return findByEmailAndPassword(user.getEmail(), user.getPassword());
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Connection connection = sql2o.open()) {
            String sql = "SELECT * FROM users WHERE email = :email";
            Query query = connection.createQuery(sql)
                    .addParameter("email", email);
            User user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (Connection connection = sql2o.open()) {
            String sql = "SELECT * FROM users WHERE email = :email AND password = :password";
            Query query = connection.createQuery(sql)
                    .addParameter("email", email)
                    .addParameter("password", password);
            User user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }

    }

    public boolean deleteById(int id) {
        try (Connection connection = sql2o.open()) {
         String sql = "DELETE FROM users WHERE id = :id";
         Query query = connection.createQuery(sql)
                 .addParameter("id", id);
         int affectedRows = query.executeUpdate().getResult();
         return affectedRows > 0;
        }
    }

    public Collection<User> findAll() {
        try (Connection connection = sql2o.open()) {
            String sql = "SELECT * FROM users";
            Query query = connection.createQuery(sql);
            return query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetch(User.class);
        }
    }

}
