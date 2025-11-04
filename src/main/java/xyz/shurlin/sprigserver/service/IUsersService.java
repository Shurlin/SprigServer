package xyz.shurlin.sprigserver.service;

import xyz.shurlin.sprigserver.dto.LoginRequest;
import xyz.shurlin.sprigserver.dto.LoginResponse;
import xyz.shurlin.sprigserver.dto.RegisterRequest;
import xyz.shurlin.sprigserver.dto.RegisterResponse;
import xyz.shurlin.sprigserver.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
public interface IUsersService extends IService<Users> {

    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

}
