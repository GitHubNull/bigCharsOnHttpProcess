package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.editor.HttpRequestEditor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BurpContextMenu  implements ContextMenuItemsProvider {
    private final MontoyaApi api;
    private final Logging logger;

    public BurpContextMenu(MontoyaApi api) {
        this.api = api;
        logger = api.logging();

    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        logger.logToOutput("[+] BurpContextMenu");
        // 判断是否为请求编辑器
        if (!event.isFromTool(ToolType.PROXY, ToolType.REPEATER, ToolType.INTRUDER, ToolType.EXTENSIONS)) {
            return null;
        }

        Optional<MessageEditorHttpRequestResponse> messageEditorHttpRequestResponse = event.messageEditorRequestResponse();
        if (!messageEditorHttpRequestResponse.isPresent()) {
            return null;
        }
        //messageEditorHttpRequestResponse
        //messageEditorHttpRequestResponse

        // get HttpRequestResponse from messageEditorHttpRequestResponse
        HttpRequestResponse httpRequestResponse = messageEditorHttpRequestResponse.get().requestResponse();
        if (httpRequestResponse == null) {
            return null;
        }

        HttpRequest httpRequest = httpRequestResponse.request();
        if (httpRequest == null) {
            return null;
        }
        ByteArray body = httpRequest.body();
        if (body == null || body.length() == 0) {
            return null;
        }

        // get mouse position in request
        int caretPosition = messageEditorHttpRequestResponse.get().caretPosition();
        if (caretPosition == 0) {
            return null;
        }

        // get body begin index
        int bodyBeginIndex = httpRequest.bodyOffset();
        if (0 == bodyBeginIndex || bodyBeginIndex > caretPosition){
            return null;
        }

        if (body.length() < caretPosition - bodyBeginIndex) {
            return null;
        }


        List<Component> components = new ArrayList<>();
        JMenuItem item = new JMenuItem("bigCharsInHttpRequestHandler");
        item.addActionListener(e -> {
            // pop dialog to config big chars args
            BigCharsDialog dialog = new BigCharsDialog(api, httpRequestResponse, caretPosition);
            dialog.showDialog();

        });
        components.add(item);


        return components;
    }
}
