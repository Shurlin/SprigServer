package xyz.shurlin.sprigserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.shurlin.sprigserver.dto.LoginResponse;
import xyz.shurlin.sprigserver.dto.WallCreateRequest;
import xyz.shurlin.sprigserver.dto.WallCreateResponse;
import xyz.shurlin.sprigserver.dto.WallFetchResponse;
import xyz.shurlin.sprigserver.entity.WallData;
import xyz.shurlin.sprigserver.service.IWallDataService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/wall_data")
public class WallDataController {

    @Autowired
    private IWallDataService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WallCreateRequest request, HttpServletRequest hRequest) {
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            WallCreateResponse response = service.create(request);

            Logger logger = LoggerFactory.getLogger(WallDataController.class);
            String clientIp = HealthController.getClientIpAddr(hRequest);
            Date date = new Date();
            logger.info("{} post wall at {}", clientIp, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", e.getMessage()));
        }

    }

    @GetMapping(value= "/get")
    public ResponseEntity<Map<String, Object>> list( // 没加try catch不管了
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<WallData> ipage = service.list(page, size);
        List<WallData> records = ipage.getRecords();
        // 组装兼容 Spring Data Page 的 JSON 结构（content + 分页信息），便于 Android 端最小改动
        Map<String, Object> body = new HashMap<>();
        body.put("content", records.stream().map(WallData::toResponse).collect(Collectors.toList()));
        // MyBatis-Plus 的 current 是 1-based（如果 service 已经处理 page+1，这里要对应）
        long current = ipage.getCurrent();
        // 将 current 转换为 0-based 返回给前端（保持你原来 Android 端的习惯）
        body.put("number", Math.max(0, current - 1));
        body.put("size", ipage.getSize());
        body.put("totalElements", ipage.getTotal());
        body.put("totalPages", ipage.getPages());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<WallFetchResponse> get(@PathVariable Long id) {
        WallFetchResponse response = service.fetch(id);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }


}
