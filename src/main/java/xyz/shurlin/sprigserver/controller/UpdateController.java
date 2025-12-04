package xyz.shurlin.sprigserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/update")
public class UpdateController {
    @Autowired
    private ResourceLoader resourceLoader;
    private final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        Resource resource = resourceLoader.getResource("classpath:apk/");
//
        try {
            File list = resource.getFile();
            if (list.isDirectory())
                for (File file : list.listFiles()) {
                    if (file.getName().endsWith(".apk")) {
                        String fileName = file.getName(); // 样例：cdcpp_v0.2.8_release.apk
                        String version = fileName.split("_")[1].substring(1);
                        return ResponseEntity.ok(version);
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(404).body("0.0");
    }

    // 提供下载安装包的api
    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadApk() {
        Resource resource = resourceLoader.getResource("classpath:apk/");
        try {
            File list = resource.getFile();
            if (list.isDirectory())
                for (File file : list.listFiles()) {
                    if (file.getName().endsWith(".apk")) {
                        File apk = resourceLoader.getResource("classpath:apk/" + file.getName()).getFile();
                        byte[] output = Files.readAllBytes(apk.toPath());
                        logger.info("APK {} downloaded.", file.getName());
                        return ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                                .body(output);
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
