package com.system.business.file.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "/file")
@Api(tags = "File", description = "文件相关接口")
public class FileController {

    @ApiOperation("下载文件")
    @GetMapping("/download")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String fileName = "spring_test.apk";// 文件名
        if (fileName != null) {
            //设置文件路径
            File file = new File("/Users/yezhennan220/Documents/temp/" + fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                // 配置文件下载
                // TODO: 18/10/25 response返回头部信息设置原理
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Length", "" + file.length());

                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, buffer.length);
                        i = bis.read(buffer);
                    }
                    return "下载成功";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "下载失败";
    }

}
