package org.example.pollingsystem.controller;

import org.example.pollingsystem.entity.Poll;
import org.example.pollingsystem.entity.User;
import org.example.pollingsystem.entity.Vote;
import org.example.pollingsystem.repo.PollRepository;
import org.example.pollingsystem.repo.VoteRepository;
import org.example.pollingsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PollController {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;
    private static final int PAGE_SIZE = 5;

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
    public String showUserDashboard(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    Model model) {
        Page<Poll> pollPage = pollRepository.findAll(PageRequest.of(page, size));
        List<Poll> polls = pollPage.getContent();

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
                isExpired = poll.getExpiresAt().isBefore(LocalDateTime.now());
            }
            poll.setExpired(isExpired);
        }

        model.addAttribute("polls", polls);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pollPage.getTotalPages());
        model.addAttribute("totalItems", pollPage.getTotalElements());
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
