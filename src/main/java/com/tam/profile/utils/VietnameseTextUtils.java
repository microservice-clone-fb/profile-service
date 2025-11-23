package com.tam.profile.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class để xử lý text tiếng Việt:
 * - Remove diacritics (bỏ dấu): "Nguyễn" -> "Nguyen"
 * - Normalize text để search không dấu
 */
public class VietnameseTextUtils {

    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Remove diacritics từ text tiếng Việt
     * Ví dụ: "Nguyễn Văn A" -> "Nguyen Van A"
     */
    public static String removeDiacritics(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Normalize NFD (Canonical Decomposition)
        String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);

        // Remove diacritical marks
        return DIACRITICS_AND_FRIENDS
                .matcher(nfdNormalizedString)
                .replaceAll("")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");
    }

    /**
     * Tạo regex pattern để search cả có dấu và không dấu
     * Ví dụ: "nguyen" -> pattern để match "Nguyễn", "Nguyen", "nguyen", etc.
     */
    public static String createSearchPattern(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "";
        }

        String normalized = removeDiacritics(keyword.trim().toLowerCase());

        // Escape special regex characters
        String escaped = normalized.replaceAll("([\\\\\\.\\[\\]\\{\\}\\(\\)\\+\\*\\?\\^\\$\\|])", "\\\\$1");

        // Create pattern that matches both with and without diacritics
        // Match each character with optional diacritics
        StringBuilder pattern = new StringBuilder();
        for (char c : escaped.toCharArray()) {
            if (Character.isLetter(c)) {
                // For each letter, create pattern that matches with/without diacritics
                pattern.append("[").append(c).append(getDiacriticVariants(c)).append("]");
            } else {
                pattern.append(c);
            }
        }

        return pattern.toString();
    }

    /**
     * Get diacritic variants của một ký tự
     * Ví dụ: 'a' -> "àáạảãâầấậẩẫăằắặẳẵ"
     */
    private static String getDiacriticVariants(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a':
                return "àáạảãâầấậẩẫăằắặẳẵ";
            case 'e':
                return "èéẹẻẽêềếệểễ";
            case 'i':
                return "ìíịỉĩ";
            case 'o':
                return "òóọỏõôồốộổỗơờớợởỡ";
            case 'u':
                return "ùúụủũưừứựửữ";
            case 'y':
                return "ỳýỵỷỹ";
            case 'd':
                return "đ";
            case 'A':
                return "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ";
            case 'E':
                return "ÈÉẸẺẼÊỀẾỆỂỄ";
            case 'I':
                return "ÌÍỊỈĨ";
            case 'O':
                return "ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ";
            case 'U':
                return "ÙÚỤỦŨƯỪỨỰỬỮ";
            case 'Y':
                return "ỲÝỴỶỸ";
            case 'D':
                return "Đ";
            default:
                return "";
        }
    }

    /**
     * Check if text contains keyword (case-insensitive, diacritics-insensitive)
     */
    public static boolean containsIgnoreDiacritics(String text, String keyword) {
        if (text == null || keyword == null) {
            return false;
        }
        String normalizedText = removeDiacritics(text.toLowerCase());
        String normalizedKeyword = removeDiacritics(keyword.toLowerCase());
        return normalizedText.contains(normalizedKeyword);
    }
}
