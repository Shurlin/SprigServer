package xyz.shurlin.sprigserver.chess.voice;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.shurlin.sprigserver.chess.GameSessionManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Component
public class UdpVoiceRelay implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UdpVoiceRelay.class);
    private final GameSessionManager sessionManager;

    @Value("${voice.udp.port:50123}")
    private int port;

    private DatagramSocket socket;
    private volatile boolean running = true;
    private static final int buffersize = 4096;

    public UdpVoiceRelay(GameSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        logger.info("Starting UDP Voice Relay on port {}", port);
        try {
            socket = new DatagramSocket(port);
            byte[] buf = new byte[buffersize];

            while (running && !socket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                handle(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 处理收到的UDP包
    private void handle(DatagramPacket packet) {
        try {
//            logger.info("data: {}", packet.getData());
            byte[] data = packet.getData();
            if (data[0] < 48){
                logger.warn("Received malformed packet");
                return;
            }
            int length = packet.getLength();
            long gameId = Long.parseLong(new String(data, 0, 4));
            String username = new String(data, 4, 12).trim();
//            logger.debug("Received UDP packet from user: {} in game: {}", username, gameId);
            if ( length <= 20 ) {
                // 16B 的加入包，记录username对应客户端地址端口
                sessionManager.addVoiceToRoom(gameId, username, packet);
                logger.info("Received voice packet from user: {} in game: {}", username, gameId);
                return;
            }
            sessionManager.broadcast(socket, gameId, data, length, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        socket.close();
    }
}
