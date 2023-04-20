package com.anyi.reggie.common;

/**
 * @author 刘上忠
 * @data studying
 */
public class BaseContext {          //线程变量工具类
    private static ThreadLocal<Integer> threadLocal=new ThreadLocal<>();

    public static void setUserId(int id){
        threadLocal.set(id);
    }
    public static Integer getCurrentId(){
        return threadLocal.get();
    }
}