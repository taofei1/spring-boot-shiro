package com.neo.enums;

import java.util.Arrays;
import java.util.List;

public enum Suffix {
    IMAGE(Arrays.asList(new String[]{".jpg", ".jpeg", ".png", ".bmp", ".gif"})),
    VIDEO(Arrays.asList(new String[]{".mp4", ".awm", ".avi", ".rmvb", ".rm", "mkv"})),
    AUDIO(Arrays.asList(new String[]{".mp3", ".acm", ".aif", ".wav", ".mp3",})),
    ZIP(Arrays.asList(new String[]{".zip", ".rar", ".7z", ".tar", ".taz"})),
    DOCUMENT(Arrays.asList(new String[]{".doc", ".docx", ".pdf", ".xls", ".xlxs", ".txt", ".pdf", ".java"}));
    private List<String> suffix;

    Suffix(List<String> strings) {
        this.suffix = strings;
    }

    public List<String> getSuffix() {
        return this.suffix;
    }
}
