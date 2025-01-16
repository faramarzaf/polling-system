package org.example.pollingsystem.service;

import org.example.pollingsystem.dto.OptionStatistics;
import org.example.pollingsystem.dto.PollStatistics;
import org.example.pollingsystem.entity.Poll;
import org.example.pollingsystem.entity.Vote;
import org.example.pollingsystem.repo.PollRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PollStatisticsService {

    private final PollRepository pollRepository;

    public PollStatisticsService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public Page<PollStatistics> generatePollStatistics(int page, int size) {
        Page<Poll> pollPage = pollRepository.findAll(PageRequest.of(page, size));
        List<PollStatistics> statsContent = pollPage.getContent().stream()
                .map(this::createPollStatistics)
                .collect(Collectors.toList());

        return new PageImpl<>(statsContent, pollPage.getPageable(), pollPage.getTotalElements());
    }

    public List<PollStatistics> generatePollStatistics() {
        List<Poll> polls = pollRepository.findAll();
        return polls.stream()
                .map(this::createPollStatistics)
                .collect(Collectors.toList());
    }

    private PollStatistics createPollStatistics(Poll poll) {
        List<Vote> votes = poll.getVotes();
        Map<String, Long> optionCounts = votes
                .stream()
                .collect(Collectors.groupingBy(Vote::getVoteOption, Collectors.counting()));

        long totalVotes = votes.size();

        List<OptionStatistics> optionStats = poll.getOptions().stream()
                .map(option -> {
                    long optionVotes = optionCounts.getOrDefault(option, 0L);
                    double percentage = totalVotes > 0 ? (optionVotes * 100.0) / totalVotes : 0;
                    return new OptionStatistics(option, optionVotes, Math.round(percentage * 10.0) / 10.0);
                })
                .collect(Collectors.toList());

        return new PollStatistics(
                poll.getTitle(),
                poll.getCreatedAt(),
                totalVotes,
                optionStats,
                poll.getExpiresAt() != null && poll.getExpiresAt().isBefore(LocalDateTime.now())
        );
    }


}
