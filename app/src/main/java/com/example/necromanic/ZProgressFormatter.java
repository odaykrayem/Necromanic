package com.example.necromanic;

import com.dinuscxj.progressbar.CircleProgressBar;

public final class ZProgressFormatter implements CircleProgressBar.ProgressFormatter {

    private static final String DEFAULT_PATTERN = "%d";

    @Override
    public CharSequence format(int progress, int max) {
        return "Z : " + String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) 1000 * 100)) + "." +(progress%10);
    }

}
