package org.example.pollingsystem.repo;

import org.example.pollingsystem.entity.Poll;
import org.example.pollingsystem.entity.User;
import org.example.pollingsystem.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByPollAndUser(Poll poll, User user);

    Optional<Vote> findByPollAndUser(Poll poll, User user);

}
