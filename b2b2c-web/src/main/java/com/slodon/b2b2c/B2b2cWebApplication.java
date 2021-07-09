package com.slodon.b2b2c;

import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.system.example.SettingExample;
import com.slodon.b2b2c.system.pojo.Setting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class B2b2cWebApplication {

    @Resource
    private SettingModel settingModel;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;//不能删除
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(B2b2cWebApplication.class, args);
    }

    /**
     * 启动同步配置信息
     */
    @PostConstruct
    public void init() {
        List<Setting> settingList = settingModel.getSettingList(new SettingExample(), null);
        if (settingList != null && settingList.size() > 0) {
            settingList.forEach(setting -> {
                stringRedisTemplate.opsForValue().set(setting.getName(), setting.getValue());
            });
        }
    }
}
