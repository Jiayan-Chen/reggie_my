package com.chenjiayan.reggie.controller;

import com.chenjiayan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.imgPath}")
    private String BasePath;

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = UUID.randomUUID()+suffix;

        //创建文件夹
        File dir = new File(BasePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //将文件转存到指定位置
        try {
            file.transferTo(new File(BasePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(filename);
        //返回文件名
        return R.success(filename);
    }

    /**
     * 图片下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            FileInputStream fileInputStream = new FileInputStream(BasePath + name);
            ServletOutputStream outputStream = response.getOutputStream();
            //设置浏览器响应回去的数据类型
            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len;
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
