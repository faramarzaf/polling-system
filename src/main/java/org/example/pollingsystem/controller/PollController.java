package org.example.pollingsystem.controller;

import org.example.pollingsystem.entity.Poll;
import org.example.pollingsystem.entity.User;
import org.example.pollingsystem.entity.Vote;
import org.example.pollingsystem.repo.PollRepository;
import org.example.pollingsystem.repo.UserRepository;
import org.example.pollingsystem.repo.VoteRepository;
import org.example.pollingsystem.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Controller
public class PollController {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;

    public PollController(PollRepository pollRepository, VoteRepository voteRepository, UserService userService) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userService = userService;
    }

    @GetMapping("/admin/createPoll")
    public String showPollCreationForm(Model model) {
        return "createPoll";
    }

    @PostMapping("/admin/createPoll")
    public String createPoll(Poll poll, Long durationInMinutes, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);
        poll.setCreatedBy(currentUser);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setDurationInMinutes(durationInMinutes);
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(durationInMinutes);
        poll.setExpiresAt(expirationDate);

        pollRepository.save(poll);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String showUserDashboard(Model model) {
        List<Poll> polls = pollRepository.findAll();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);

        for (Poll poll : polls) {
            boolean hasVoted = voteRepository.existsByPollAndUser(poll, currentUser);
            poll.setHasVoted(hasVoted);

            if (hasVoted) {
                Vote vote = voteRepository.findByPollAndUser(poll, currentUser)
                        .orElseThrow(() -> new IllegalArgumentException("Vote not found"));
                poll.setSelectedOption(vote.getVoteOption());
            }

            boolean isExpired = false;
            if (poll.getExpiresAt() != null) {
                long expiresAtTimestamp = poll.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                isExpired = expiresAtTimestamp < System.currentTimeMillis();
            }
            poll.setExpired(isExpired);
        }

        model.addAttribute("polls", polls);
        return "user-dashboard";
    }

    @PostMapping("/poll/vote")
    public String voteForPoll(Long pollId, String option, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userService.findByUsername(username);

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found: " + pollId));

        if (option == null || option.trim().isEmpty()) {
            throw new IllegalArgumentException("No option selected");
        }

        boolean hasVoted = voteRepository.existsByPollAndUser(poll, currentUser);

        if (hasVoted) {
            model.addAttribute("error", "You have already voted in this poll.");
            return "user-dashboard";
        }

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(currentUser);
        vote.setVoteOption(option);
        voteRepository.save(vote);
        return "redirect:/user/dashboard";
    }
}
