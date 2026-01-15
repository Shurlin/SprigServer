package xyz.shurlin.sprigserver.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.model}")
    private String model;

    @Value("${qwen.api.url}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)      // 连接超时30秒
            .readTimeout(120, TimeUnit.SECONDS)        // 读取超时120秒（重要）
            .writeTimeout(30, TimeUnit.SECONDS)        // 写入超时30秒
            .callTimeout(180, TimeUnit.SECONDS)        // 整个调用超时180秒
            .build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AiService.class);

    public String chat(String userInput) throws IOException {
//        logger.info("Sending user input to Qwen API: {}", userInput);

        ObjectNode json = mapper.createObjectNode();
        json.put("model", model);
        ArrayNode messages = json.putArray("messages");

        ObjectNode msg = mapper.createObjectNode();
        msg.put("role", "user");
        msg.put("content", userInput);
        messages.add(msg);
        return generate(json);
    }

    public String generate(ObjectNode root) throws IOException {
//        logger.info("Sending request to Qwen API with model: {}", model);
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(
                        root.toString(),
                        MediaType.parse("application/json")
                ))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        logger.info("Qwen API request body: {}", root.toString());
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            logger.info("Received response from Qwen API: {}", body);
            JsonNode json = mapper.readTree(body);
            String reply = json.get("choices").get(0).get("message").get("content").asText();
            int token = json.get("usage").get("total_tokens").asInt();
            logger.info("Qwen reply: {}, tokens used: {}", reply, token);
            return reply;
        } catch (IOException e) {
            logger.info("Error communicating with Qwen API: {}", e.getMessage());
            throw e;
        }
    }

}
