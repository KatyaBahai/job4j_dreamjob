package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import javax.sql.DataSource;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oCandidateRepositoryTest {
    private static Sql2oCandidateRepository sql2oCandidateRepository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        Properties properties = new Properties();
        try (InputStream stream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(stream);
        }
        String url = properties.getProperty("datasource.url");
        String password = properties.getProperty("datasource.password");
        String username = properties.getProperty("datasource.username");

        DatasourceConfiguration datasourceConfiguration = new DatasourceConfiguration();
        DataSource dataSource = datasourceConfiguration.connectionPool(url, username, password);
        Sql2o sql2o = datasourceConfiguration.databaseClient(dataSource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("ffstName ", "ffstPath");
        sql2oFileRepository.save(file);
    }

    @AfterEach
    public void clearCandidates() {
        for (Candidate candidate : sql2oCandidateRepository.findAll()) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @Test
    public void whenSaveThenSaved() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        Candidate savedCandidate = sql2oCandidateRepository.save(candidate);
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralThenGetThemAll() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        Candidate candidate2 = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        Candidate candidate3 = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        sql2oCandidateRepository.save(candidate1);
        sql2oCandidateRepository.save(candidate2);
        sql2oCandidateRepository.save(candidate3);
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    public void whenNotSavedThenNothingFound() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByIdThenDeleted() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        sql2oCandidateRepository.save(candidate);
        assertThat(sql2oCandidateRepository.deleteById(candidate.getId())).isEqualTo(true);
    }

    @Test
    public void whenDeleteNonExistingCandidateThenEmptyOptional() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        assertThat(sql2oCandidateRepository.deleteById(candidate.getId())).isEqualTo(false);
    }

    @Test
    public void whenUpdateCandidateThenUpdated() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        sql2oCandidateRepository.save(candidate);
        Candidate candidateWithUpdates = new Candidate(candidate.getId(), "newName", candidate.getCreationDate(), candidate.getDescription(), 2, file.getId());
        boolean isUpdated = sql2oCandidateRepository.update(candidateWithUpdates);
        assertThat(isUpdated).isEqualTo(true);
        Candidate updatedCandidate = sql2oCandidateRepository.findById(candidateWithUpdates.getId()).get();
        assertThat(updatedCandidate).usingRecursiveComparison().isEqualTo(candidateWithUpdates);
    }

    @Test
    public void whenUpdateNonExistingCandidateThenFalse() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        boolean isUpdated = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdated).isEqualTo(false);
    }

    @Test
    public void whenFindByIdThenFound() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name", creationDate, "description1", 1, file.getId());
        sql2oCandidateRepository.save(candidate);
        assertThat(sql2oCandidateRepository.findById(candidate.getId()).get()).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenFindByWrongIdThenNotFound() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }
}