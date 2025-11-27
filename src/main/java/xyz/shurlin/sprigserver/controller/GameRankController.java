package xyz.shurlin.sprigserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.shurlin.sprigserver.dto.SpeedRankPostRequest;
import xyz.shurlin.sprigserver.dto.WallCreateRequest;
import xyz.shurlin.sprigserver.dto.WallCreateResponse;
import xyz.shurlin.sprigserver.dto.WallFetchResponse;
import xyz.shurlin.sprigserver.entity.SpeedRank;
import xyz.shurlin.sprigserver.entity.WallData;
import xyz.shurlin.sprigserver.service.ISpeedRankService;
import xyz.shurlin.sprigserver.service.IWallDataService;

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
@RequestMapping("/game")
public class GameRankController {

    @Autowired
    private ISpeedRankService speedRankService;

    public GameRankController(ISpeedRankService speedRankService) {
        this.speedRankService = speedRankService;
    }

    @PostMapping("/speed_test/post")
    public ResponseEntity<String> post(@RequestBody SpeedRankPostRequest request) {
        try{
            speedRankService.post(request);
            return ResponseEntity.ok("success");
        }catch (Exception e){
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

    @GetMapping(value= "/speed_test/get")
    public ResponseEntity<?> list() {
        try{
            List<SpeedRank> lists = speedRankService.listRank();
            return ResponseEntity.ok(lists);
        }catch (Exception e){
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

}
