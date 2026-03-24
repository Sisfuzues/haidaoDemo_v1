package com.example_2.haidaodemo_v1.controller;

import com.example_2.haidaodemo_v1.common.Utils.AliyunOSSUtils;
import com.example_2.haidaodemo_v1.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/upload")
@RestController
public class FileController {
    @Autowired
    private AliyunOSSUtils aliyunOSSUtils;

    @RequestMapping("/image")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = aliyunOSSUtils.upload(file);
        return Result.success(url);
    }
}
