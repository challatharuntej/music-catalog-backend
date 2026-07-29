package com.musiccatalog.backend.repository;

import com.musiccatalog.backend.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    List<LibraryItem> findByUserId(Long userId);
}