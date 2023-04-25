package com.gdou.common.domain;

import com.gdou.common.constant.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {

    private  String message;
    private  Integer code;
    private  Object data;
    private  boolean flag;

    public static R success(String message,Object o){
        return new R(message, HttpStatus.SUCCESS,o,true);
    }

    public static R success(String message,Object o,boolean flag){
        return new R(message, HttpStatus.SUCCESS,o,flag);
    }

    public static R success(String message,Integer code){
        return new R(message,code,null,true);
    }

    public static R success(Object data){
        return new R("成功", HttpStatus.SUCCESS,data,true);
    }

    public static R success(String message){
        return new R(message, HttpStatus.SUCCESS,null,true);
    }


    public static R success(String message,boolean flag){
        return new R(message, HttpStatus.SUCCESS,null,flag);
    }

    public static R error(String message){
        return new R(message,HttpStatus.BAD_REQUEST,null,true);
    }

    public static R error(String message,Integer code){
        return new R(message,code,null,false);
    }


}
