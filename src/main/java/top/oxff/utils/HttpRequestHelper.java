package top.oxff.utils;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.message.requests.HttpRequest;

public class HttpRequestHelper {
    public static boolean isRequestBodyContainBigCharsMarker(HttpRequestToBeSent httpRequestToBeSent) {
        if (null == httpRequestToBeSent) {
            return false;
        }
        String body = httpRequestToBeSent.body().toString();
        if (null == body || body.isEmpty() || body.trim().isEmpty()) {
            return false;
        }
        body = body.trim();
        return body.contains("{{__#");
    }

    public static String getBigCharsMarker(String body) {
        if (null == body || body.isEmpty() || body.trim().isEmpty()) {
            return null;
        }
        body = body.trim();
        return body.substring(body.indexOf("{{__#"), body.indexOf("#__}}") + 5);
    }

    public static int getBigCharsSize(String bigCharsMarker) {
        if (bigCharsMarker == null || bigCharsMarker.trim().isEmpty()) {
            return 0;
        }

        bigCharsMarker = bigCharsMarker.trim();
        final int START_INDEX = 5;
        final int END_OFFSET = 6;

        if (bigCharsMarker.length() < START_INDEX + END_OFFSET) {
            return 0;
        }

        String bigCharsSize = bigCharsMarker.substring(START_INDEX, bigCharsMarker.length() - END_OFFSET).trim();
        if (bigCharsSize.isEmpty()) {
            return 0;
        }
        int size;
        try {
            size = Integer.parseInt(bigCharsSize);
        } catch (NumberFormatException e) {
            return 0;
        }
        return size;
    }

    public static String getChar(String bigCharsMarker){
        if (null == bigCharsMarker || bigCharsMarker.trim().isEmpty()) {
            return null;
        }
        bigCharsMarker = bigCharsMarker.trim();
        if (bigCharsMarker.length() < 11) {
            return null;
        }
        return bigCharsMarker.substring(bigCharsMarker.length() - 6, bigCharsMarker.length()-5);
    }


public static RequestToBeSentAction insertBigCharsIntoRequestBody(HttpRequestToBeSent httpRequestToBeSent) {
    if (httpRequestToBeSent == null || httpRequestToBeSent.body() == null) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    String body = httpRequestToBeSent.body().toString().trim();
    if (body.isEmpty()) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    String bigCharMarker = getBigCharsMarker(body);
    if (bigCharMarker == null || bigCharMarker.trim().isEmpty()) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    int bigCharsSize = getBigCharsSize(bigCharMarker);
    if (bigCharsSize <= 0) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    String bigChar = getChar(bigCharMarker);
    if (bigChar == null || bigChar.trim().isEmpty()) {
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    try {
        int leftIndex = body.indexOf("{{__#");
        int rightIndex = body.indexOf("#__}}");

        if (leftIndex == -1 || rightIndex == -1 || leftIndex >= rightIndex) {
            return RequestToBeSentAction.continueWith(httpRequestToBeSent);
        }

        String left = body.substring(0, leftIndex);
        String right = body.substring(rightIndex + 5);

        String newBodyBuilder = left + bigChar.repeat(bigCharsSize) +
                right;

        HttpRequest newHttpRequest = httpRequestToBeSent.withBody(newBodyBuilder);
        return RequestToBeSentAction.continueWith(newHttpRequest);
    } catch (StringIndexOutOfBoundsException e) {
        // Log the exception or handle it as needed
        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }
}

}
