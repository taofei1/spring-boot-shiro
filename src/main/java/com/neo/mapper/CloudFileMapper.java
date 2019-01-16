package com.neo.mapper;

import com.neo.pojo.CloudFile;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudFileMapper {
    List<CloudFile> selectAllCurrentPage(CloudFile cloudFile);

    List<String> selectByNameLike(@Param("userId") Long userId, @Param("fileId") Long fileId, @Param("fileName") String fileName);

    Long selectMaxIndexCurrentPage(@Param("fileId") Long fileId, @Param("userId") Long userId, @Param("isDirectory") Integer isDirectory);

    List<CloudFile> selectCateGory(@Param("userId") Long userId, @Param("types") List<String> types, @Param("fileName") String fileName);

    List<CloudFile> selectSeeTrash(@Param("userId") Long userId, @Param("fileName") String fileName);

    int insertOne(CloudFile cloudFile);

    int updateOne(CloudFile cloudFile);

    int deleteOne(Long fileId);

    int deleteBatch(List<Long> ids);

    List<CloudFile> selectAllByUserIdAndParentId(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("isTrash") Integer isTrash);

    CloudFile selectByFileId(Long fileId);

    List<CloudFile> selectAllShares();
}
