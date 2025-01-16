package org.example.pollingsystem.dto;

public class OptionStatistics {

    private final String optionText;
    private final long voteCount;
    private final double percentage;

    public OptionStatistics(String optionText, long voteCount, double percentage) {
        this.optionText = optionText;
        this.voteCount = voteCount;
        this.percentage = percentage;
    }

    public String getOptionText() {
        return optionText;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public double getPercentage() {
        return percentage;
    }
}
