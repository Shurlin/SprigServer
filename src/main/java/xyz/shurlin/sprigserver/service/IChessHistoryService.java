package xyz.shurlin.sprigserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.shurlin.sprigserver.dto.SpeedRankPostRequest;
import xyz.shurlin.sprigserver.entity.ChessHistory;
import xyz.shurlin.sprigserver.entity.SpeedRank;

import java.util.List;

public interface IChessHistoryService extends IService<ChessHistory> {

    List<ChessHistory> listHistory(String username);
}
