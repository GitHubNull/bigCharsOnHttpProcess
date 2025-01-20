package top.oxff.controller;

import burp.api.montoya.http.handler.*;
import top.oxff.utils.HttpRequestHelper;

public class BigCharsOnHttpHandler implements HttpHandler {
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        if (HttpRequestHelper.isRequestBodyContainBigCharsMarker(httpRequestToBeSent)){
            return HttpRequestHelper.insertBigCharsIntoRequestBody(httpRequestToBeSent);
        }
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }
}
