package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {
private static Sql2oUserRepository sql2oUserRepository;

@BeforeAll
public static void initRepositories() throws Exception {
    Properties properties = new Properties();
    try (InputStream inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
        properties.load(inputStream);
    }
    var url = properties.getProperty("datasource.url");
    var username = properties.getProperty("datasource.username");
    var password = properties.getProperty("datasource.password");

    var configuration = new DatasourceConfiguration();
    var datasource = configuration.connectionPool(url, username, password);
    var sql2o = configuration.databaseClient(datasource);

    sql2oUserRepository = new Sql2oUserRepository(sql2o);
}

    @AfterEach
    public void clearVacancies() {
        Collection<User> users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

@Test
    public void whenSaveThenSaved() {
    User user = new User(0, "Ivan", "email@mail.ru", "1234");
    sql2oUserRepository.save(user);
    assertThat(user).usingRecursiveComparison().isEqualTo(sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get());
}

@Test
    public void whenEmailAlreadyExistsThenNotSaved() {
    User user = new User(0,  "Ivan", "email@mail.ru", "1234");
    User user2 = new User(0, "Ian", "email@mail.ru", "3456");
    sql2oUserRepository.save(user);
    Optional<User> secondUser = sql2oUserRepository.save(user2);
    assertThat(secondUser).isEqualTo(empty());
}

}