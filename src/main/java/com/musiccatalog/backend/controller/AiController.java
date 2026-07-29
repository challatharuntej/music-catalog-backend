package com.musiccatalog.backend.controller;

import com.musiccatalog.backend.entity.LibraryItem;
import com.musiccatalog.backend.repository.LibraryItemRepository;
import com.musiccatalog.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository userRepository;

    public AiController(LibraryItemRepository libraryItemRepository, UserRepository userRepository) {
        this.libraryItemRepository = libraryItemRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/summary")
    public Map<String, String> getSummary(Authentication authentication) {
        Long userId = userRepository.findByEmail(authentication.getName()).orElseThrow().getId();
        List<LibraryItem> items = libraryItemRepository.findByUserId(userId);

        if (items.isEmpty()) {
            return Map.of("summary", "Your library is empty. Add some albums to get a personalized summary!");
        }

        // Mocking the AI: Find the most common genre mathematically
        String topGenre = items.stream()
                .filter(i -> i.getGenre() != null)
                .collect(Collectors.groupingBy(LibraryItem::getGenre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("a variety of genres");

        String simulatedAiResponse = String.format(
                "Based on your library of %d album(s), you have a strong preference for %s music! Keep adding to your collection to refine your taste profile.", 
                items.size(), topGenre
        );

        return Map.of("summary", simulatedAiResponse);
    }
}