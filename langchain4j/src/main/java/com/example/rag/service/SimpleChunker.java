package com.example.rag.service;

import java.util.ArrayList;
import java.util.List;

public class SimpleChunker {

    public static List<String> chunk(String text, int chunkSizeChars, int overlapChars) {
        if (text == null || text.isBlank()) return List.of();
        if (chunkSizeChars <= 0) throw new IllegalArgumentException("chunkSizeChars must be > 0");
        if (overlapChars < 0) throw new IllegalArgumentException("overlapChars must be >= 0");
        if (overlapChars >= chunkSizeChars) throw new IllegalArgumentException("overlapChars must be < chunkSizeChars");

        String t = text.trim();
        List<String> out = new ArrayList<>();

        int start = 0;
        while (start < t.length()) {
            int end = Math.min(t.length(), start + chunkSizeChars);
            String chunk = t.substring(start, end).trim();
            if (!chunk.isEmpty()) out.add(chunk);
            if (end == t.length()) break;
            start = end - overlapChars;
        }
        return out;
    }
}
