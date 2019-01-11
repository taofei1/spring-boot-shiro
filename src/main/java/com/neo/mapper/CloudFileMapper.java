package com.neo.mapper;

import com.neo.pojo.CloudFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudFileMapper {
    List<CloudFile> selectAllCurrentPage(CloudFile cloudFile);

    int selectMaxIndexCurrentPage(Long fileId, Long userId, Integer isDirectory);

    List<CloudFile> selectCateGory(Long userId, List<String> types, String fileName);

    List<CloudFile> selectSeeTrash(Long userId, String fileName);

    int insertOne(CloudFile cloudFile);

    int updateOne(CloudFile cloudFile);

    int deleteOne(Long fileId);

    int deleteBatch(List<Long> ids);

    List<CloudFile> selectAllByUserIdAndParentId(Long userId, Long parentId, Integer isTrash);

    CloudFile selectByFileId(Long fileId);

    List<CloudFile> selectAllShares();
}
