package xyz.shurlin.sprigserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/update")
public class UpdateController {
    public static final String VERSION = "0.3.4";
    @Autowired
    private ResourceLoader resourceLoader;
    private final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok(VERSION);
    }

    // 提供下载安装包的api
    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadApk() {
        String name = "cdcpp_v" + VERSION + "_release.apk";
        ClassPathResource resource = new ClassPathResource("apk/" + name);
        try {
            InputStream inputStream = resource.getInputStream();
            logger.info("APK {} downloaded.", VERSION);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + name + "\"")
                    .body(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
