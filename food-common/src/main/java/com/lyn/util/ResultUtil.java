package com.lyn.util;

import com.lyn.constant.ApiConstant;
import com.lyn.model.common.ResultInfo;

/**
 * 公共返回对象工具类
 */
public class ResultUtil {

    public static <T> ResultInfo<T> buildError(String path){
        ResultInfo<T> resultInfo = build(ApiConstant.ERROR_CODE,ApiConstant.ERROR_MESSAGE,path,null);
        return resultInfo;
    }

    public static <T>ResultInfo<T> buildError(Integer code,String message,String path){
        ResultInfo<T> resultInfo = build(code,message,path,null);
        return resultInfo;
    }

    public static<T>ResultInfo<T> buildSuccess(String path){
        ResultInfo<T> resultInfo = build(null,null,path,null);
        return resultInfo;
    }

    public static<T>ResultInfo<T> buildSuccess(String path,T data){
        ResultInfo<T> resultInfo = build(null,null,path,data);
        return resultInfo;
    }
    public static <T>ResultInfo<T> build(Integer code,String message,
                                         String path,T data){
        if(code == null){
            code = ApiConstant.SUCCESS_CODE;
        }
        if(message == null){
            message = ApiConstant.SUCCESS_MESSAGE;
        }
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(code);
        resultInfo.setMessage(message);
        resultInfo.setPath(path);
        resultInfo.setData(data);
        return resultInfo;

    }

}
