package top.oxff.controller;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import top.oxff.model.HTTPRequestBodyType;
import top.oxff.utils.HttpRequestHelper;

public class ByPassHttpHandler implements HttpHandler {
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        if (null == httpRequestToBeSent){
            return null;
        }
        if (!HttpRequestHelper.getBodyType(httpRequestToBeSent).equals(HTTPRequestBodyType.JSON)){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent);
        }
        JSONObject jsonObject = HttpRequestHelper.coverJSONBodyStringToJSONObject(httpRequestToBeSent);
        if (null == jsonObject){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent);
        }
        jsonObject = HttpRequestHelper.addByPassPayloadToJSONBody(jsonObject, "Q", 10);
        HttpRequest httpRequest = httpRequestToBeSent.withBody(jsonObject.toJSONString());
        return RequestToBeSentAction.continueWith(httpRequest);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }
}
