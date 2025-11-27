package xyz.shurlin.sprigserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.shurlin.sprigserver.dto.LoginRequest;
import xyz.shurlin.sprigserver.dto.LoginResponse;
import xyz.shurlin.sprigserver.dto.RegisterRequest;
import xyz.shurlin.sprigserver.dto.RegisterResponse;
import xyz.shurlin.sprigserver.service.IUsersService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private IUsersService service;

    public UsersController(IUsersService service) {
        this.service = service;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse resp = service.login(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException ex) {
            log.debug(ex.getMessage());
            return ResponseEntity.status(401).body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            RegisterResponse resp = service.register(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException ex) {
            log.debug(ex.getMessage());
            return ResponseEntity.status(401).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
