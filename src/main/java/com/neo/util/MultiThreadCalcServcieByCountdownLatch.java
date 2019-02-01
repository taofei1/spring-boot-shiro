package com.neo.util;

import com.neo.mapper.CloudFileMapper;
import com.neo.pojo.CloudFile;
import com.neo.quartz.SpringUtil;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MultiThreadCalcServcieByCountdownLatch {

    private final CloudFileMapper cloudFileMapper = SpringUtil.getBean(CloudFileMapper.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private CountDownLatch countDownLatch;
    private AtomicLong size;
    private AtomicInteger dirsNum;
    private AtomicInteger filesNum;

    public Map<String, Object> calc(List<Long> ids) {
        this.size = new AtomicLong(0L);
        this.dirsNum = new AtomicInteger(0);
        this.filesNum = new AtomicInteger(0);
        this.countDownLatch = new CountDownLatch(ids.size());
        for (Long id : ids) {
            CloudFile cf = cloudFileMapper.selectByFileId(id);
            if (cf.getIsDirectory() == 0) {
                size.addAndGet(cf.getSize());
                filesNum.incrementAndGet();
                countDownLatch.countDown();
            } else {
                dirsNum.incrementAndGet();
                executor.execute(() -> {
                    recursion(cf.getFileId());
                    countDownLatch.countDown();

                });
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();


        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("dirsNum", dirsNum.intValue());
        map.put("filesNum", filesNum.intValue());
        map.put("size", StringUtils.convertSize(size.longValue()));
        return map;
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
