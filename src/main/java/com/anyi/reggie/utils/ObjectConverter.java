package com.anyi.reggie.utils;

/**
 * @author 刘上忠
 * @data studying
 */
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 头发又黑又长
 * @Date 2022/7/26 22:41
 * @email zwb15083976291@163.com
 */
public class ObjectConverter<T> {

    /**
     * 对象拷贝
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T BeanConverter(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }


    /**
     * 集合 对象拷贝
     *
     * @param sourceList 源数据列表
     * @param tClass 目标类
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> collectionBeanConverter(List<?> sourceList, Class<T> tClass) throws Exception {
        ArrayList<T> ts = new ArrayList<>();
        Constructor<T> constructor = tClass.getConstructor();
        for (Object o : sourceList) {
            T t = constructor.newInstance();
            BeanUtils.copyProperties(o, t);
            ts.add(t);
        }
        return ts;
    }


}
