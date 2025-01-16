package org.example.pollingsystem.controller;

import org.example.pollingsystem.dto.PollStatistics;
import org.example.pollingsystem.service.PollStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final PollStatisticsService pollStatisticsService;

    public AdminController(PollStatisticsService pollStatisticsService) {
        this.pollStatisticsService = pollStatisticsService;
    }

    @GetMapping("/admin/statistics")
    public String showStatistics(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Page<PollStatistics> pollStats = pollStatisticsService.generatePollStatistics(page, size);
        model.addAttribute("pollStats", pollStats.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pollStats.getTotalPages());
        model.addAttribute("totalItems", pollStats.getTotalElements());
        return "statistics";
    }
}
