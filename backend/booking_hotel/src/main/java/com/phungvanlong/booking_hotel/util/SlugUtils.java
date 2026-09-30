package com.phungvanlong.booking_hotel.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s+]");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private SlugUtils() {
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        // Convert Vietnamese đ/Đ explicitly
        String normalized = input.trim()
                .replace("đ", "d")
                .replace("Đ", "d");

        // Remove diacritical marks
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Replace whitespace with hyphens
        String noWhiteSpace = WHITESPACE.matcher(normalized).replaceAll("-");

        // Remove non-latin and non-alphanumeric characters except hyphen
        String slug = NONLATIN.matcher(noWhiteSpace).replaceAll("");

        // Collapse multiple hyphens into one and trim leading/trailing hyphens
        slug = MULTI_HYPHEN.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase(Locale.ROOT);
    }
}
