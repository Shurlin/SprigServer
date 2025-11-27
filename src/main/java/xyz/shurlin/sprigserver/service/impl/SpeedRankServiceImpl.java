package xyz.shurlin.sprigserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.shurlin.sprigserver.dto.SpeedRankPostRequest;
import xyz.shurlin.sprigserver.entity.SpeedRank;
import xyz.shurlin.sprigserver.mapper.SpeedRankMapper;
import xyz.shurlin.sprigserver.mapper.WallDataMapper;
import xyz.shurlin.sprigserver.service.ISpeedRankService;

import java.util.List;

@Service
public class SpeedRankServiceImpl extends ServiceImpl<SpeedRankMapper, SpeedRank> implements ISpeedRankService {

    private final SpeedRankMapper mapper;

    public SpeedRankServiceImpl(SpeedRankMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public List<SpeedRank> listRank() {
        List<SpeedRank> ranks = mapper.selectList(new QueryWrapper<SpeedRank>().orderByDesc("score")).stream().limit(5).toList();
//        Logger logger = LoggerFactory.getLogger("test");
//        for(SpeedRank rank : ranks) {
//            logger.warn(rank.getUsername());
//        }
        return ranks;
    }

    @Override
    public void post(SpeedRankPostRequest request) {
        String username = request.getUsername();
        SpeedRank speedRank = mapper.selectOne(new QueryWrapper<SpeedRank>().eq("username", username));
        if (speedRank!=null && speedRank.getScore() < request.getScore()) {
            mapper.update(new UpdateWrapper<SpeedRank>().eq("username", username).set("score", request.getScore()));
        }else{
            Long id = mapper.selectLatestId()+1;
            mapper.insert(new SpeedRank(id, request.getUsername(), request.getScore()));
        }
    }
}
