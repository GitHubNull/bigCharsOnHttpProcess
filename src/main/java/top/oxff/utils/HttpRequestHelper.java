package top.oxff.utils;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.message.HttpHeader;
import com.alibaba.fastjson2.JSONObject;
import top.oxff.model.HTTPRequestBodyType;

import java.util.List;

public class HttpRequestHelper {
    public static HTTPRequestBodyType getBodyType(HttpRequestToBeSent httpRequestToBeSent) {
        if (null == httpRequestToBeSent){
            return HTTPRequestBodyType.UNKNOWN;
        }
        List<HttpHeader> headers = httpRequestToBeSent.headers();
        if (null == headers || headers.isEmpty()){
            return HTTPRequestBodyType.UNKNOWN;
        }

        // get content-type value
        for (HttpHeader header : headers) {
            if (header.name().equalsIgnoreCase("Content-Type")) {
                String value = header.value();
                if (null == value || value.isEmpty() || value.trim().isEmpty()){
                    return HTTPRequestBodyType.UNKNOWN;
                }
                if (value.contains("application/json")) {
                    return HTTPRequestBodyType.JSON;
                } else if (value.contains("application/xml")) {
                    return HTTPRequestBodyType.XML;
                } else if (value.contains("application/x-www-form-urlencoded")) {
                    return HTTPRequestBodyType.URL_ENCODE_FORM;
                } else if (value.contains("multipart/form-data")) {
                    return HTTPRequestBodyType.FORM;
                } else if (value.contains("application/octet-stream")) {
                    return HTTPRequestBodyType.BINARY;
                } else {
                    return HTTPRequestBodyType.UNKNOWN;
                }
            }
        }
        return HTTPRequestBodyType.UNKNOWN;
    }

    public static JSONObject addByPassPayloadToJSONBody(JSONObject jsonObject, String payloadCharacter, int payloadSize) {
        if (null == jsonObject){
            return null;
        }
        if (payloadSize <= 0 || payloadCharacter.isEmpty()){
            return jsonObject;
        }
        if (1 != payloadCharacter.length()){
            return jsonObject;
        }
        // gen payload
        StringBuilder payload = new StringBuilder();
        for (int i = 0; i < payloadSize; i++) {
            payload.append(payloadCharacter);
        }
        jsonObject.put("byPassPayload", payload.toString());
        return jsonObject;
    }

    public static JSONObject coverJSONBodyStringToJSONObject(HttpRequestToBeSent httpRequestToBeSent) {
        if (null == httpRequestToBeSent){
            return null;
        }
        String body = httpRequestToBeSent.body().toString();
        if (null == body || body.isEmpty() || body.trim().isEmpty()){
            return null;
        }
        body = body.trim();
        if (!body.startsWith("{") || !body.endsWith("}")){
            return null;
        }
        return JSONObject.parseObject(body);
    }
}
