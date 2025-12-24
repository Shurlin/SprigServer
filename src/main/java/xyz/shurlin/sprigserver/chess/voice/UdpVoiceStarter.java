package xyz.shurlin.sprigserver.chess.voice;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UdpVoiceStarter implements ApplicationRunner {

    private final UdpVoiceRelay relay;

    public UdpVoiceStarter(UdpVoiceRelay relay) {
        this.relay = relay;
    }

    @Override
    public void run(ApplicationArguments args) {
        Thread t = new Thread(relay, "udp-voice-relay");
        t.setDaemon(true);
        t.start();
    }
}
