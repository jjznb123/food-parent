package com.lyn.service;

import com.lyn.model.common.ResultInfo;

public interface IDinnerService {
    public ResultInfo login(String username,String password,String path);
}
