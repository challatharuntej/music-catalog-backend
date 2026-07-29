package com.musiccatalog.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/search")
    public String search(@RequestParam String term,
                          @RequestParam(defaultValue = "album") String type) {
        String url = "https://itunes.apple.com/search?term=" + term
                    + "&entity=" + type + "&country=US";
        return restTemplate.getForObject(url, String.class);
    }
}