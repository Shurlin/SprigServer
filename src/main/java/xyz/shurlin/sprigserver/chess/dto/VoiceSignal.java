package xyz.shurlin.sprigserver.chess.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record VoiceSignal(
        String type,      // VOICE_SDP / VOICE_ICE
        long gameId,
        String from,
        JsonNode payload
) {}