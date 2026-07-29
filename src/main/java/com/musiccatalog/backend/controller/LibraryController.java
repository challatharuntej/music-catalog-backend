package com.musiccatalog.backend.controller;

import com.musiccatalog.backend.entity.LibraryItem;
import com.musiccatalog.backend.repository.LibraryItemRepository;
import com.musiccatalog.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository userRepository;

    public LibraryController(LibraryItemRepository libraryItemRepository, UserRepository userRepository) {
        this.libraryItemRepository = libraryItemRepository;
        this.userRepository = userRepository;
    }

    // Helper: get the logged-in user's ID from the JWT-authenticated email
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping
    public List<LibraryItem> getLibrary(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return libraryItemRepository.findByUserId(userId);
    }

    @PostMapping
    public LibraryItem addToLibrary(@RequestBody LibraryItem item, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        item.setUserId(userId);
        item.setId(null); // safety: always insert new, never overwrite via id from client
        return libraryItemRepository.save(item);
    }

    @PutMapping("/{id}")
    public LibraryItem updateLibraryItem(@PathVariable Long id,
                                          @RequestBody Map<String, Object> updates,
                                          Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        LibraryItem item = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to edit this item");
        }

        if (updates.containsKey("userRating")) {
            item.setUserRating((Integer) updates.get("userRating"));
        }
        if (updates.containsKey("userNotes")) {
            item.setUserNotes((String) updates.get("userNotes"));
        }

        return libraryItemRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteLibraryItem(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        LibraryItem item = libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this item");
        }

        libraryItemRepository.delete(item);
        return Map.of("message", "Deleted successfully");
    }
}