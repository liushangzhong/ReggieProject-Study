package com.anyi.reggie.config;

/**
 * @author
 * @versio
 */

import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * 自定义元数据处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]....");
        log.info("start insert fill ....");
        log.info(metaObject.toString());
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
        this.setFieldValByName("createUser",UserContext.getUserId(),metaObject);
        this.setFieldValByName("updateUser",UserContext.getUserId(),metaObject);
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long id = Thread.currentThread().getId();
        log.info("当前线程id为：{}",id);
        log.info("公共字段自动填充[update]....");
        log.info("start update fill ....");
        log.info(metaObject.toString());
        this.setFieldValByName("updateTime",new Date(),metaObject);
        this.setFieldValByName("updateUser", UserContext.getUserId(),metaObject );
    }
}