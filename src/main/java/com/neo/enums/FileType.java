package com.neo.enums;

import java.util.Arrays;
import java.util.List;

public enum FileType {
    IMAGE("Í¼Æ¬", Arrays.asList(new String[]{"jpg", "jpeg", "png", "bmp", "gif"})),
    VIDEO("ÊÓÆµ", Arrays.asList(new String[]{"mp4", "awm", "avi", "rmvb", "rm", "mkv"})),
    AUDIO("ÒôÆµ", Arrays.asList(new String[]{"mp3", "acm", "aif", "wav"})),
    ZIP("Ñ¹Ëõ°ü", Arrays.asList(new String[]{"zip", "rar", "7z", "tar", "taz"})),
    DOCUMENT("ÎÄµµ", Arrays.asList(new String[]{"doc", "docx", "pdf", "xls", "xlxs", "txt", "pdf", "java"}));
    private List<String> suffix;
    private String name;

    FileType(String name, List<String> strings) {
        this.name = name;
        this.suffix = strings;
    }

    public static void main(String[] args) {
        System.out.println(FileType.valueOf("IMAGE").getName());
    }
    public List<String> getSuffix() {
        return this.suffix;
    }

    public String getName() {
        return this.name;
    }
}
