package xyz.shurlin.sprigserver.service.impl;

import xyz.shurlin.sprigserver.entity.Users;
import xyz.shurlin.sprigserver.mapper.UsersMapper;
import xyz.shurlin.sprigserver.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
