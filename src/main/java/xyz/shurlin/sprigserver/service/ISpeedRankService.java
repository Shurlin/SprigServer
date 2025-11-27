package xyz.shurlin.sprigserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.shurlin.sprigserver.dto.SpeedRankPostRequest;
import xyz.shurlin.sprigserver.dto.WallCreateRequest;
import xyz.shurlin.sprigserver.dto.WallCreateResponse;
import xyz.shurlin.sprigserver.dto.WallFetchResponse;
import xyz.shurlin.sprigserver.entity.SpeedRank;
import xyz.shurlin.sprigserver.entity.WallData;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shurlin
 * @since 2025-11-27
 */
public interface ISpeedRankService extends IService<SpeedRank> {

    List<SpeedRank> listRank();

    void post(SpeedRankPostRequest request);
}
