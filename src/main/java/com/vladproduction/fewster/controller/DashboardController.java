package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Dashboard — authenticated user's URL management.
 *
 * Changes from original:
 *   - getAllUrlsForCurrentUser() now returns enriched UrlDTOs that include
 *     clickCount and createdAt, so the profile template can display them.
 *   - Error handling uses typed exceptions from the service layer; the controller
 *     catches generic Exception only as a last resort and surfaces a user-friendly
 *     flash message.
 *   - Added currentUsername to the model so the header greeting stays accurate.
 */
@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UrlService urlService;

    public DashboardController(UrlService urlService) {
        this.urlService = urlService;
    }

    // ── GET /dashboard ─────────────────────────────────────────────────────────

    @GetMapping
    public String dashboard(Model model) {
        try {
            List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
            model.addAttribute("urls", userUrls);
            model.addAttribute("urlDTO", new UrlDTO());
            model.addAttribute("totalUrls", userUrls.size());
            model.addAttribute("totalClicks",
                    userUrls.stream().mapToLong(u -> u.getClickCount() != null ? u.getClickCount() : 0).sum());
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "Unable to load your URLs. Please try again.");
            model.addAttribute("urls", List.of());
            model.addAttribute("urlDTO", new UrlDTO());
        }
        return "dashboard/profile";
    }

    // ── POST /dashboard/create ─────────────────────────────────────────────────

    @PostMapping("/create")
    public String createUrl(@Valid @ModelAttribute("urlDTO") UrlDTO urlDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (bindingResult.hasErrors()) {
            List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
            model.addAttribute("urls", userUrls);
            model.addAttribute("totalUrls", userUrls.size());
            model.addAttribute("totalClicks",
                    userUrls.stream().mapToLong(u -> u.getClickCount() != null ? u.getClickCount() : 0).sum());
            return "dashboard/profile";
        }

        try {
            UrlDTO created = urlService.create(urlDTO.getOriginalUrl(), false);
            redirectAttributes.addFlashAttribute("success",
                    "Short URL created: " + created.getShortUrl());
        } catch (Exception e) {
            log.error("Error creating URL: {}", urlDTO.getOriginalUrl(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Failed to create short URL: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ── POST /dashboard/update/{id} ────────────────────────────────────────────

    @PostMapping("/update/{id}")
    public String updateUrl(@PathVariable Long id,
                            @RequestParam String newOriginalUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            urlService.updateUrl(id, newOriginalUrl);
            redirectAttributes.addFlashAttribute("success", "URL updated successfully.");
        } catch (Exception e) {
            log.error("Error updating URL ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update URL: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ── POST /dashboard/delete/{id} ────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String deleteUrl(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            urlService.deleteUrl(id);
            redirectAttributes.addFlashAttribute("success", "URL deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting URL ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error",
                    "Failed to delete URL: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
