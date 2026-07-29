package com.musiccatalog.backend.controller;

import com.musiccatalog.backend.entity.LibraryItem;
import com.musiccatalog.backend.repository.LibraryItemRepository;
import com.musiccatalog.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository userRepository;

    public AnalyticsController(LibraryItemRepository libraryItemRepository, UserRepository userRepository) {
        this.libraryItemRepository = libraryItemRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Map<String, Object> getAnalytics(Authentication authentication) {
        Long userId = userRepository.findByEmail(authentication.getName()).orElseThrow().getId();
        List<LibraryItem> items = libraryItemRepository.findByUserId(userId);

        Map<String, Long> byGenre = items.stream()
                .filter(i -> i.getGenre() != null)
                .collect(Collectors.groupingBy(LibraryItem::getGenre, Collectors.counting()));

        Map<Integer, Long> byYear = items.stream()
                .filter(i -> i.getReleaseDate() != null)
                .collect(Collectors.groupingBy(i -> i.getReleaseDate().getYear(), Collectors.counting()));

        Map<String, Long> byArtist = items.stream()
                .filter(i -> i.getArtistName() != null)
                .collect(Collectors.groupingBy(LibraryItem::getArtistName, Collectors.counting()));

        Map<String, Long> savedOverTime = items.stream()
                .filter(i -> i.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                    i -> i.getCreatedAt().getYear() + "-" + String.format("%02d", i.getCreatedAt().getMonthValue()),
                    Collectors.counting()
                ));

        double avgRating = items.stream()
                .filter(i -> i.getUserRating() != null)
                .mapToInt(LibraryItem::getUserRating)
                .average().orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("totalItems", items.size());
        result.put("byGenre", byGenre);
        result.put("byYear", byYear);
        result.put("byArtist", byArtist);
        result.put("savedOverTime", savedOverTime);
        result.put("averageRating", avgRating);
        return result;
    }
}