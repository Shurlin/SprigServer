package xyz.shurlin.sprigserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.shurlin.sprigserver.dto.LoginRequest;
import xyz.shurlin.sprigserver.dto.LoginResponse;
import xyz.shurlin.sprigserver.dto.RegisterRequest;
import xyz.shurlin.sprigserver.dto.RegisterResponse;
import xyz.shurlin.sprigserver.entity.Users;
import xyz.shurlin.sprigserver.mapper.UsersMapper;
import xyz.shurlin.sprigserver.service.IUsersService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    private final UsersMapper usersMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expireSeconds:604800}")
    private long expireSeconds;

    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        Users user = usersMapper.selectOne(
                new QueryWrapper<Users>().eq("username", req.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }


        return new LoginResponse(generateToken(user), user.getId(), user.getUsername(), user.getDisplayName(), user.getEmail());
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        boolean usernameExists = usersMapper.exists(
                Wrappers.<Users>lambdaQuery().eq(Users::getUsername, registerRequest.getUsername())
        );
        if (usernameExists) {
            throw new RuntimeException("用户名已被使用");
        }
        boolean emailExists = usersMapper.exists(
                Wrappers.<Users>lambdaQuery().eq(Users::getEmail, registerRequest.getEmail())
        );
        if (emailExists) {
            throw new RuntimeException("邮箱已被使用");
        }

        Long latestId = usersMapper.selectLatestId();

        Users user = Users.fromReg(latestId + 1, registerRequest);
        user.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));
        usersMapper.insert(user);

        return new RegisterResponse(generateToken(user), registerRequest.getUsername());
    }

    private String generateToken(Users user) {
        // 使用 secret 生成 token（注意：jjwt 要求适当长度 secret）
        // 在 token 生成前加入：
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new RuntimeException("JWT secret not configured (jwt.secret is missing). Please set jwt.secret in application.properties or env.");
        }
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new RuntimeException("JWT secret too short: must be at least 32 bytes for HS256.");
        }
        SecretKey key;
        try {
            key = Keys.hmacShaKeyFor(secretBytes);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build JWT key: " + ex.getMessage(), ex);
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + expireSeconds * 1000L);


        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


}
