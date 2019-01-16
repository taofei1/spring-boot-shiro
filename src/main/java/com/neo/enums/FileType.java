package com.neo.enums;

import java.util.Arrays;
import java.util.List;

public enum FileType {
    IMAGE("ͼƬ", Arrays.asList(new String[]{"jpg", "jpeg", "png", "bmp", "gif"})),
    VIDEO("��Ƶ", Arrays.asList(new String[]{"mp4", "awm", "avi", "rmvb", "rm", "mkv"})),
    AUDIO("��Ƶ", Arrays.asList(new String[]{"mp3", "acm", "aif", "wav"})),
    ZIP("ѹ����", Arrays.asList(new String[]{"zip", "rar", "7z", "tar", "taz"})),
    DOCUMENT("�ĵ�", Arrays.asList(new String[]{"doc", "docx", "pdf", "xls", "xlxs", "txt", "pdf", "java"}));
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
