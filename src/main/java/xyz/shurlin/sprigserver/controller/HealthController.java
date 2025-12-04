package xyz.shurlin.sprigserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class HealthController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping(HttpServletRequest request) {
        Logger logger = LoggerFactory.getLogger(HealthController.class);
        String clientIp = getClientIpAddr(request);
        Date date = new Date();
        logger.info("{} visit at {}", clientIp, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        return ResponseEntity.ok("pong");
    }

    static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能是逗号分隔的 IP 列表：客户端, proxy1, proxy2...
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        // 其他可能的代理头
        for (String header : new String[]{
                "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        }) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        // 最后退回到 socket 层的远端地址
        return request.getRemoteAddr();
    }
}
