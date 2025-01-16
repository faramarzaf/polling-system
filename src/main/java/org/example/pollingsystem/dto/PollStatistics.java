package org.example.pollingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PollStatistics(String title, LocalDateTime createdAt, long totalVotes, List<OptionStatistics> optionStats,
                             boolean expired) {

}
