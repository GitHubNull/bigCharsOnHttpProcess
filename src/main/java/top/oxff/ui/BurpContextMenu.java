package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse.SelectionContext;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        List<Component> components = new ArrayList<>();
        event.messageEditorRequestResponse().ifPresent(messageEditorHttpRequestResponse -> messageEditorHttpRequestResponse.selectionOffsets().ifPresent(selectionOffsets -> {
            int startIndexInclusive = selectionOffsets.startIndexInclusive();
            int endIndexExclusive = selectionOffsets.endIndexExclusive();
            if (startIndexInclusive <= 0 || endIndexExclusive <= 0 || startIndexInclusive >= endIndexExclusive) {
                return;
            }
            SelectionContext selectionContext = messageEditorHttpRequestResponse.selectionContext();
            if (selectionContext != SelectionContext.REQUEST){
                return;
            }
            HttpRequestResponse httpRequestResponse = messageEditorHttpRequestResponse.requestResponse();
            if (null == httpRequestResponse){
                return;
            }
            HttpRequest httpRequest = httpRequestResponse.request();
            if (null == httpRequest){
                return;
            }

            ByteArray body = httpRequest.body();
            if (body == null || body.length() == 0){
                return;
            }

            ByteArray requestByteArray = httpRequest.toByteArray();
            int bodyBeginIndex = httpRequest.bodyOffset();

            if (startIndexInclusive < bodyBeginIndex || endIndexExclusive > requestByteArray.length()){
                return;
            }

            JMenuItem item = new JMenuItem("BCC");
            item.addActionListener(e -> {
                // pop dialog to config big chars args
                BigCharsDialog dialog = new BigCharsDialog(api, messageEditorHttpRequestResponse, startIndexInclusive, endIndexExclusive, httpRequest, requestByteArray);
                dialog.showDialog();

            });
            components.add(item);
        }));

        return components;
    }
}
