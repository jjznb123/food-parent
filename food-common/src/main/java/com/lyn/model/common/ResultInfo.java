package com.lyn.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel("公共返回对象")
public class ResultInfo <T> implements Serializable {
    @ApiModelProperty(value = "成功标识0=失败，1=成功")
    private Integer code;
    @ApiModelProperty(value = "描述信息")
    private String message;
    @ApiModelProperty(value = "返回数据对象")
    private T data;
    @ApiModelProperty("访问路径")
    private String path;
}
