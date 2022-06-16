package com.zionjr.policeticket.utils;

import android.util.Log;

public class PlateNumberExtractionUtils {

    public static String parsePlateNumber(String[] words) {

        String plateNumber = null;

        for (int x = 0; x < words.length; x++) {

            Log.d("detected_text", "word untouched: " + words[x]);

            words[x] = words[x].replace(" ", "");
            words[x] = words[x].replace("-", "");
            words[x] = words[x].replace(".", "");

            Log.d("detected_text", "word modified: " + words[x]);

            if (words[x].length() >= 5) {

                Log.d("detected_text", "potential plate: " + words[x]);

                if (!isDigit(words[x].charAt(0))) {

                    Log.d("detected_text", "1st char is letter: " + words[x].charAt(0));

                    if (isDigit(words[x].charAt(1))) {

                        Log.d("detected_text", "2nd char is digit: " + words[x].charAt(1));

                        plateNumber = words[x];
                        break;

                    } else if (!isDigit(words[x].charAt(1))) {

                        Log.d("detected_text", "2nd char is letter: " + words[x].charAt(1));

                        if (!isDigit(words[x].charAt(2))) {

                            if (words[x].charAt(2) == 'G') {

                                StringBuilder replacedWord = new StringBuilder(words[x]);
                                replacedWord.setCharAt(2, '0');

                                words[x] = replacedWord.toString();
                            }
                        }

                        if (isDigit(words[x].charAt(2))) {

                            Log.d("detected_text", "3rd char is digit: " + words[x].charAt(2));

                            plateNumber = words[x];
                            break;
                        }
                    }
                }
            }
        }

        return plateNumber;
    }

    private static boolean isDigit(char c) {

        String digits = "0123456789";

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
