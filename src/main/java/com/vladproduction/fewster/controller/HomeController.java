package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Public home page and demo URL shortening (no authentication required).
 *
 * Changes from original:
 *   - Validation errors are surfaced directly on the form (stays on the page)
 *     rather than redirecting — preserves user input.
 *   - Error message from the service is passed through to the template.
 */
@Slf4j
@Controller
public class HomeController {

    private final UrlService urlService;

    public HomeController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("urlDTO", new UrlDTO());
        return "index";
    }

    @PostMapping("/demo-create")
    public String createDemoUrl(@Valid @ModelAttribute("urlDTO") UrlDTO urlDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "index";
        }

        try {
            UrlDTO created = urlService.create(urlDTO.getOriginalUrl(), true);
            redirectAttributes.addFlashAttribute("success",
                    "Demo short URL created: " + created.getShortUrl());
            redirectAttributes.addFlashAttribute("demoUrl", created);
            return "redirect:/";

        } catch (Exception e) {
            log.error("Demo URL creation failed for: {}", urlDTO.getOriginalUrl(), e);
            model.addAttribute("error", "Could not shorten that URL: " + e.getMessage());
            return "index";
        }
    }
}
