package top.oxff;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import top.oxff.controller.BigCharsOnHttpHandler;
import top.oxff.ui.BurpContextMenu;

public class BigCharsOnHttpRequest implements BurpExtension {
    private static final String EXTENSION_NAME = "BigCharsOnHttpRequestHandler";
    private static final String EXTENSION_VERSION = "1.0";
    private static final String EXTENSION_DESCRIPTION = "This extension is used to handle HTTP requests and responses with big characters.";
    private static final String EXTENSION_AUTHOR = "oxff";
    private static final String EXTENSION_LICENSE = "MIT";

    public static MontoyaApi api;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        api = montoyaApi;
        montoyaApi.extension().setName(EXTENSION_NAME + " " + EXTENSION_VERSION);

        montoyaApi.logging().logToOutput("%s loaded".formatted(EXTENSION_NAME));
        montoyaApi.logging().logToOutput("Version: " + EXTENSION_VERSION);
        montoyaApi.logging().logToOutput("Author: " + EXTENSION_AUTHOR);
        montoyaApi.logging().logToOutput("License: " + EXTENSION_LICENSE);
        montoyaApi.logging().logToOutput("Description: " + EXTENSION_DESCRIPTION);

        api.http().registerHttpHandler(new BigCharsOnHttpHandler());
        api.userInterface().registerContextMenuItemsProvider(new BurpContextMenu(api));
    }
}
