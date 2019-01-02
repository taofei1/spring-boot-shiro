package com.neo;


import com.neo.entity.SyncFilePlan;
import com.neo.mapper.GenMapper;
import com.neo.model.Car;
import com.neo.service.FileInfoService;
import com.neo.service.SyncFilePlanService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCaching
@Slf4j
public class SpringBootShiroApplicationTests {
	@Autowired
	private GenMapper genMapper;

	@Autowired
	private FileInfoService fileInfoService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Resource
	private CacheManager cacheManager;
	@Autowired
	private SyncFilePlanService syncFilePlanService;
	@Test
	public void testFileInfoCache(){
		SyncFilePlan syncFilePlan=syncFilePlanService.findById(4);
		fileInfoService.findByFilePathAndSyncFilePlan("12321",syncFilePlan);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fileInfoService.findByFilePathAndSyncFilePlan("12321",syncFilePlan);


	}
	@Test
	public void testCache(){
/*		net.sf.ehcache.CacheManager manager1 = net.sf.ehcache.CacheManager.create("classpath:config/ehcache.xml");
		net.sf.ehcache.Cache cache=manager1.getCache("userInfo");
		System.out.println(cache.getSize());*/
	/*	Element e=new Element("111","222");
		Element e2=new Element("121","222");
		Element e3=new Element("131","222");
		cache.put(e);
		cache.put(e2);
		cache.put(e3);*/

	//	System.out.println("result:"+cache.getSize());

		//System.out.println(manager1.getCacheNames().length);
		//System.out.println(cacheManager.getCacheNames().size());
	//	Cache cache=cacheManager.getCache("fileInfo");


		//System.out.println(cache.get("file_/log/zookeeper-3.5.2-alpha/bin/D/program/zookeeper/log/version-2/log.1_16"));

	}
	@Test
	public void testRedis(){
		redisTemplate.opsForValue().set("user",new Car("112","22"));
	}


	@Test
	public void testFileInfo(){
		//log.info(fileInfoDao.toString());
	}
	@Test
	public void testMapper(){

		System.out.println(genMapper.selectTableByName("file_info"));
		//log.info(genMapper.selectTableByName("email").toString());
	}
	@Test
	public void create(){
		log.info(genMapper.selectCreateTable("email").toString());
	}


}
