package xyz.shurlin.sprigserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.shurlin.sprigserver.entity.ChessHistory;
import xyz.shurlin.sprigserver.mapper.ChessHistoryMapper;
import xyz.shurlin.sprigserver.service.IChessHistoryService;

import java.util.List;

@Service
public class ChessHistoryServiceImpl extends ServiceImpl<ChessHistoryMapper, ChessHistory> implements IChessHistoryService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ChessHistoryServiceImpl.class);

    @Autowired
    private final ChessHistoryMapper mapper;

    public ChessHistoryServiceImpl(ChessHistoryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<ChessHistory> listHistory(String username) {
        List<ChessHistory> histories =  mapper.selectList(new QueryWrapper<ChessHistory>()
                .eq("user1", username)
                .orderByDesc("time"))
                .stream()
//                .limit(20)
                .toList();
//        for (ChessHistory history: histories) {
//            logger.info("History - User1: {}, User2: {}, State: {}, Time: {}",
//                    history.getUser1(), history.getUser2(), history.getState(), history.getTime());
//        }
        logger.info("Fetched {} chess histories for user: {}", histories.size(), username);
        return histories;
    }
}
