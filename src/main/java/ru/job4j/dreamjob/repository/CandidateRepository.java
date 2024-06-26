package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

public interface CandidateRepository {

    Candidate save(Candidate vacancy);

    void deleteById(int id);

    boolean update(Candidate vacancy);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();

}
