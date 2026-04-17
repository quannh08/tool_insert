package com.example.toolinsert.model;

// `normalizedKey` is the stable key that later mapping layers can use without depending on raw file headers.
public record FileColumn(int position, String originalName, String normalizedKey) {
}
