package com.neo.util;

import com.neo.mapper.CloudFileMapper;
import com.neo.pojo.CloudFile;
import com.neo.quartz.SpringUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadCalcService {

    private CloudFileMapper cloudFileMapper = SpringUtil.getBean(CloudFileMapper.class);

    private AtomicLong size;
    private AtomicInteger dirsNum;
    private AtomicInteger filesNum;
    private ExecutorService executor;

    public MultiThreadCalcService() {
        size = new AtomicLong(0L);
        dirsNum = new AtomicInteger(0);
        filesNum = new AtomicInteger(0);
        executor = Executors.newFixedThreadPool(4);
    }

    public Map<String, Object> calc(List<Long> ids) {


        for (Long id : ids) {
            CloudFile cf = cloudFileMapper.selectByFileId(id);
            if (cf.getIsDirectory() == 0) {
                size.addAndGet(cf.getSize());
                filesNum.incrementAndGet();
            } else {
                dirsNum.incrementAndGet();
                executor.execute(() -> recursion(cf.getFileId()));
            }
        }
        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("dirsNum", dirsNum.intValue());
                map.put("filesNum", filesNum.intValue());
                map.put("size", convertSize(size.longValue()));
                return map;
            }
        }
    }

    public String convertSize(Long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        double res = size;
        String unit = "B";
        if (res / 1024 > 1) {
            res = res / 1024;
            unit = "KB";
        }
        if (res / 1024 > 1) {
            res = res / 1024;
            unit = "MB";
        }
        if (res / 1024 > 1) {
            res = res / 1024;
            unit = "GB";
        }
        if (res / 1024 > 1) {
            res = res / 1024;
            unit = "TB";
        }
        return df.format(res) + unit;

    }

    public void recursion(Long id) {
        List<CloudFile> cloudFiles = cloudFileMapper.selectAllByUserIdAndParentId(ShiroUtil.getSysUser().getUid().longValue(), id, 0);
        if (cloudFiles.size() > 0) {
            for (CloudFile cloudFile : cloudFiles) {
                if (cloudFile.getIsDirectory() == 0) {
                    filesNum.incrementAndGet();
                    size.addAndGet(cloudFile.getSize());
                } else {
                    dirsNum.incrementAndGet();
                    recursion(cloudFile.getFileId());
                }
            }
        }
    }
}
