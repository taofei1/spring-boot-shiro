package com.neo.DTO;

import com.neo.pojo.Directory;
import com.neo.pojo.FileList;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class FileDTO {
    //directory id
    private Long id;
    //当前页面的目录和文件

    private List<Directory> directories;
    private List<FileList> fileLists;
}
