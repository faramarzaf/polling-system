package org.example.pollingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PollStatistics {

    private final String title;
    private final LocalDateTime createdAt;
    private final long totalVotes;
    private final List<OptionStatistics> optionStats;
    private final boolean expired;

    public PollStatistics(String title, LocalDateTime createdAt, long totalVotes, List<OptionStatistics> optionStats, boolean expired) {
        this.title = title;
        this.createdAt = createdAt;
        this.totalVotes = totalVotes;
        this.optionStats = optionStats;
        this.expired = expired;
    }

    public String title() {
        return title;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public long getTotalVotes() {
        return totalVotes;
    }

    public List<OptionStatistics> getOptionStats() {
        return optionStats;
    }

    public boolean isExpired() {
        return expired;
    }
}
