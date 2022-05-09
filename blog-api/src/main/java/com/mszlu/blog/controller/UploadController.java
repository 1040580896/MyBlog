package com.mszlu.blog.controller;

import com.mszlu.blog.utils.QiniuUtils;
import com.mszlu.blog.vo.Result;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @Author xiaokaixin
 * @Date 2021/8/20 08:23
 * @Version 1.0
 */
@RestController
@RequestMapping("upload")
public class UploadController {


    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){

        //原始文件名称 比如aa.png
        String originalFilename = file.getOriginalFilename();
        //唯一的文件名称
        String  fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        //上传文件  上传到哪呢 七牛云 云服务器  按量付费  速度快  把图片发送到服务器上
        //降低  我们自身应用服务器的宽带消耗

        boolean upload = qiniuUtils.upload(file, fileName);
        if(upload){
            return Result.success(QiniuUtils.url+fileName);
        }
        return Result.fail(20001,"上传文件失败");
    }
}
