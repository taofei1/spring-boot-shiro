package com.neo.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Directory {

    private Long id;
    private Long parentId;
    private String pathName;
    private Long pathIsTrash = 0L;
    private Long pathUserId; //
    private List<FileList> fileLists;
}
