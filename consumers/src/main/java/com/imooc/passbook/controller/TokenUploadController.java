package com.imooc.passbook.controller;

import com.imooc.passbook.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PassTemplate Token Upload
 */
@Slf4j
@Controller
public class TokenUploadController {

    /** Redis客户端 */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Get方法，展示token上传的form页面
     * @return upload.html 返回的是MVC当中的View name
     */
    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    /**
     * Post方法，将POST过来的数据，写入文件，并调用写入文件至Redis的方法
     * @param merchantsId form的merchantsId
     * @param passTemplateId form的passTemplateId
     * @param file form中上传的token文件
     * @param redirectAttributes 重定向相关属性的设置
     * @return 重定向的route
     */
    @PostMapping("/token")
    public String tokenFileUpload(@RequestParam("merchantsId") String merchantsId,
                                  @RequestParam("passTemplateId") String passTemplateId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes
                                  ) {
        if(null == passTemplateId || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "passTemplateId is null or file is empty");
            return "redirect:/uploadStatus";
        }

        try {
            File cur = new File(Constants.TOKEN_DIR + merchantsId);
            if(!cur.exists()) {
                log.info("Create file: {}", cur.mkdir());
            }
            Path path = Paths.get(Constants.TOKEN_DIR, merchantsId, passTemplateId);
            Files.write(path, file.getBytes());

            if(!writeTokenToRedis(path, passTemplateId)) {
                redirectAttributes.addFlashAttribute("message", "write token error");
            } else {
                redirectAttributes.addFlashAttribute("message", "write token '" + file.getOriginalFilename() + "' successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return "redirect:/uploadStatus";
    }

    /**
     * Get方法，展示
     * @return
     */
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    /**
     * 将Token写入Redis
     * @param path {@link Path}
     * @param key 给token文件定义的redis的key
     * @return true/false
     */
    private boolean writeTokenToRedis(Path path, String key) {

        Set<String> tokens;

        //1. 按行读取Token文件的path，每一行都是单独的token
        try(Stream<String> stream = Files.lines(path)) {

            //1.1. 把读取到的每一行token都放进Set里，进行去重
            tokens = stream.collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(!CollectionUtils.isEmpty(tokens)) {
            //执行pipeline，将所有写入redis数据结构的tokens发送到redis中
            redisTemplate.executePipelined((RedisCallback<Object>)connection -> {
                //依次将收集好的token加入到redis的Set数据结构里面
               for(String token : tokens) {
                   connection.sAdd(key.getBytes(), token.getBytes());
               }
               return null;
            });

            return true;
        }

        return false;
    }
}
