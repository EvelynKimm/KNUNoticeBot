package org.chatbot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import org.json.JSONObject;

public class SimpleHandler implements RequestHandler<Map<String, Object>, String> {
    public String handleRequest(Map<String, Object> input, Context context) {
        FunctionCheck.handler();

        LambdaLogger logger = context.getLogger();
        try {
            String result = Crawling.crawling();
            logger.log(result);
            return result;

        } catch (Exception e) {
            // 예외 정보를 로깅
            context.getLogger().log("An error occurred: " + e.toString());

            // 예외를 JSON 형식으로 포맷하여 반환
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", e.getMessage());
            errorJson.put("type", e.getClass().getSimpleName());
            return errorJson.toString();
        }
    }
}
