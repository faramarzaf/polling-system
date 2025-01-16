package org.example.pollingsystem.controller;

import org.example.pollingsystem.dto.PollStatistics;
import org.example.pollingsystem.service.PollStatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final PollStatisticsService pollStatisticsService;

    public AdminController(PollStatisticsService pollStatisticsService) {
        this.pollStatisticsService = pollStatisticsService;
    }

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        List<PollStatistics> pollStats = pollStatisticsService.generatePollStatistics();
        model.addAttribute("pollStats", pollStats);
        return "statistics";
    }
}
