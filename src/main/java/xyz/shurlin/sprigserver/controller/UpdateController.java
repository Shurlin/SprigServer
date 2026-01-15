package xyz.shurlin.sprigserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/update")
public class UpdateController {
    public static final String SERVER_PATH = "D:\\Server\\SpringBoot\\app";
    @Autowired
    private ResourceLoader resourceLoader;
    private final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        File app_dir = new File(SERVER_PATH);
        String latest_version = "0.0.0";
        for (File app: app_dir.listFiles()) {
            if (app.getName().startsWith("cdcpp") && app.getName().endsWith(".apk")) {
                String ver = app.getName().replace("cdcpp_v", "").replace("_release.apk", "");
                if (ver.compareTo(latest_version) > 0) {
                    latest_version = ver;
                }
            }
        }
        return ResponseEntity.ok(latest_version);
    }

    // 提供下载安装包的api
    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadApk() {
        File app_dir = new File(SERVER_PATH);
        File latest_apk = null;
        String latest_version = "0.0.0";
        for (File app: app_dir.listFiles()) {
            if (app.getName().startsWith("cdcpp") && app.getName().endsWith(".apk")) {
                String ver = app.getName().replace("cdcpp_v", "").replace("_release.apk", "");
                if (ver.compareTo(latest_version) > 0) {
                    latest_version = ver;
                    latest_apk = app;
                }
            }
        }
        try {
            FileInputStream inputStream = new FileInputStream(latest_apk);
            logger.info("APK {} downloaded.", latest_version);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + latest_apk.getName() + "\"")
                    .body(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
