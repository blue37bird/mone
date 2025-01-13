package run.mone.m78.client.test;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import run.mone.m78.client.bot.BotWsClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author HawickMason@xiaomi.com
 * @date 9/11/24 11:03
 */
public class BotWsClientTest {

    @Test
    public void testBotWsClient() throws InterruptedException {
        String botId = "100265";
        String token = "ac6524cf-6053-4888-8464-40b3fb04dc92";
        String input = "你好，1+1等于几呀?";
        BotWsClient botWsClient = BotWsClient.builder()
                .url("ws://10.38.204.190:8076/ws/bot/abc")
                .token(token)
                .build();
        botWsClient.start(System.out::println);

        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("botId", botId);
        jsonReq.addProperty("token", token);
        jsonReq.addProperty("input", input);
        jsonReq.addProperty("topicId", UUID.randomUUID().toString());
        botWsClient.send(jsonReq.toString());
        TimeUnit.SECONDS.sleep(20);
    }
}
