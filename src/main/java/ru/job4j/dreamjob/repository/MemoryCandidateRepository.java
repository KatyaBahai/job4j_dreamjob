package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Nicholas", LocalDateTime.now(), "description", 1, 0));
        save(new Candidate(0, "Andrew", LocalDateTime.now(), "description to be inserted", 1, 0));
        save(new Candidate(0, "Peter", LocalDateTime.now(), "description to be inserted", 1, 0));
        save(new Candidate(0, "John", LocalDateTime.now(), "description to be inserted", 1, 0));
        save(new Candidate(0, "Romeo", LocalDateTime.now(), "description to be inserted", 1, 0));
        save(new Candidate(0, "Juliet", LocalDateTime.now(), "description to be inserted", 1, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> (new Candidate(oldCandidate.getId(), candidate.getName(), oldCandidate.getCreationDate(), candidate.getDescription(), candidate.getCityId(), candidate.getFileId()))) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
