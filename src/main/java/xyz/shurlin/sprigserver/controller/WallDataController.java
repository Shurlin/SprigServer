package xyz.shurlin.sprigserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import xyz.shurlin.sprigserver.service.IWallDataService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Controller
@RequestMapping("/wallData")
public class WallDataController {

    @Autowired
    private IWallDataService wallDataService;
}
