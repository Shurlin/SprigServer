package xyz.shurlin.sprigserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.shurlin.sprigserver.dto.WallCreateRequest;
import xyz.shurlin.sprigserver.dto.WallCreateResponse;
import xyz.shurlin.sprigserver.dto.WallFetchResponse;
import xyz.shurlin.sprigserver.entity.WallData;
import xyz.shurlin.sprigserver.mapper.WallDataMapper;
import xyz.shurlin.sprigserver.service.IWallDataService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Service
public class WallDataServiceImpl extends ServiceImpl<WallDataMapper, WallData> implements IWallDataService {
    @Autowired
    private final WallDataMapper wallDataMapper;

    public WallDataServiceImpl(WallDataMapper wallDataMapper) {
        this.wallDataMapper = wallDataMapper;
    }

    @Override
    public WallCreateResponse create(WallCreateRequest request) {
        Long id = wallDataMapper.selectLatestId() +1;
        WallData wallData = new WallData(id, request.getTitle(), request.getContent(), LocalDateTime.now(), request.getCreatedBy(), 0L);
        wallDataMapper.insert(wallData);
        return new WallCreateResponse(id);
    }

    @Override
    public IPage<WallData> list(int page, int size) {
        Page<WallData> p = new Page<>(page + 1, size);
        QueryWrapper<WallData> qw = new QueryWrapper<>();
        qw.orderByDesc("createdAt");
        return wallDataMapper.selectPage(p, qw);
    }

    @Override
    public WallFetchResponse fetch(Long id) {
        WallData wallData = wallDataMapper.selectOne(
                new QueryWrapper<WallData>().eq("id", id)
        );
        if (wallData == null) return null;
        return WallData.toResponse(wallData);
    }
}
