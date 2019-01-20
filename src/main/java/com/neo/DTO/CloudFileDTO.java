package com.neo.DTO;

import com.neo.pojo.CloudFile;

import java.util.List;

public class CloudFileDTO extends CloudFile{
    private List<CloudFileDTO> children;

    public List<CloudFileDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CloudFileDTO> children) {
        this.children = children;
    }
}
