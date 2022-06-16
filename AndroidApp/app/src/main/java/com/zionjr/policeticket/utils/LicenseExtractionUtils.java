package com.zionjr.policeticket.utils;

import android.util.Log;

import java.util.HashMap;

public class LicenseExtractionUtils {

    private static final String digits = "0123456789";

    public static HashMap<String, String> parseLicense(String[] words) {

        if (words.length < 3) {

            return null;
        }

        String licenseNumber = null;
        String name = null;

        boolean licenseNumberFound = false;
        boolean nameFound = false;

        for (int x = 0; x < words.length; x++) {

            if (licenseNumberFound && nameFound) {

                break;
            }

            if (!licenseNumberFound) {

                if (isLicenseNumber(words[x])) {

                    licenseNumberFound = true;
                    licenseNumber = words[x];
                    Log.d("detected_text", "license found: " + licenseNumber);
                }
            }

            if (!nameFound) {

                if (x < (words.length - 2)) {

                    if (isName(words[x])) {

                        nameFound = true;
                        name = words[(x + 1)].toUpperCase();
                        Log.d("detected_text", "name found: " + name);
                    }
                }
            }
        }

        if (!licenseNumberFound || !nameFound) {

            return null;
        }

        String licenseExpiry = getLicenseExpiry(words);

        if (licenseExpiry == null) {

            return null;
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("license_number", licenseNumber);
        data.put("license_expiry", licenseExpiry);
        data.put("name", name);

        return data;
    }

    private static String getLicenseExpiry(String[] words) {

        boolean startAdding = false;
        StringBuilder wordsString = new StringBuilder();

        for (int x = 0; x < words.length; x++) {

            if (words[x].toLowerCase().endsWith("om")) {

                x += 5;
                startAdding = true;
                continue;
            }

            if (startAdding) {

                wordsString.append(words[x]).append(" ");
            }
        }

        String[] dateWords = wordsString.toString().split(" ");

        String expiryDate = null;

        for (int x = ((dateWords.length) - 1); x >= 0; x--) {

            Log.d("detected_text", "word: " + dateWords[x]);

            if ((dateWords[x].length() >= 10)) {

                int digitCount = 0;

                for (int y = 0; y < dateWords[x].length(); y++) {

                    if (digitCount < 2) {

                        if (isDigit(dateWords[x].charAt(y))) {

                            digitCount++;
                        }
                    }

                    if (digitCount == 2) {

                        if (dateWords[x].charAt(y) == '-') {

                            expiryDate = dateWords[x];
                            break;
                        }
                    }
                }

                if (expiryDate != null) {

                    break;
                }
            }
        }

        return expiryDate;
    }

    // Format: 084-218-844-3
    private static boolean isLicenseNumber(String word) {

        Log.d("detected_text", "word under test: " + word);

        if (!(word.length() >= 13)) {

            Log.d("detected_text", "word is smaller than 13: " + word);
            return false;
        }

        int digitCount = 0;

        for (int x = 0; x < word.length(); x++) {

            Log.d("detected_text", "char under test: " + word.charAt(x));

            if (digitCount < 3) {

                if (!isDigit(word.charAt(x))) {

                    Log.d("detected_text", "char is not a digit: " + word.charAt(x));
                    return false;
                }

                digitCount++;
            }

            Log.d("detected_text", "char is a digit: " + word.charAt(x));

            if (digitCount == 3) {

                if (word.charAt(x) == ' ') {

                    if (word.charAt(x + 1) == '-') {

                        return true;
                    }
                }

                if (word.charAt(x) == '-') {

                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isName(String previousWord) {

        if (previousWord.length() == 3) {

            return previousWord.toLowerCase().endsWith("om");
        }

        return false;
    }

    private static boolean isDigit(char c) {

        boolean isDigit = false;

        for (int y = 0; y < digits.length(); y++) {

            if (String.valueOf(c).equals(String.valueOf(digits.charAt(y)))) {

                isDigit = true;
                break;
            }
        }

        return isDigit;
    }
}
