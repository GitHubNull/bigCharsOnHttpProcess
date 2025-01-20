package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import top.oxff.GlobalConst;
import top.oxff.utils.ByteSizeHelper;

import javax.swing.*;
import java.awt.*;

import static top.oxff.GlobalConst.CHAR_SIZE_CHOOSE;
import static top.oxff.GlobalConst.INSERT_STR_FORMAT;

public class BigCharsDialog extends JDialog {

    private JPanel centerPanel;

    ButtonGroup buttonGroup;
    JRadioButton defaultRadioButton;
    JRadioButton customRadioButton;

    private final static String[] chars = {"G", "H", "J", "L", "M", "N", "Q", "R", "T", "U", "Y"};
    private final static JComboBox<String> charsComboBox = new JComboBox<>(chars);

    private final static JComboBox<String> charSizeChooseComboBox = new JComboBox<>(CHAR_SIZE_CHOOSE);
    private final static JTextField customSizeTextField = new JTextField("", 16);

    JButton okButton;
    JButton cancelButton;

    MontoyaApi api;
    Logging logger;
    MessageEditorHttpRequestResponse messageEditorHttpRequestResponse;
    int startIndexInclusive;
    int endIndexExclusive;
    HttpRequest httpRequest;
    ByteArray requestByteArray;

    public BigCharsDialog(MontoyaApi api, MessageEditorHttpRequestResponse messageEditorHttpRequestResponse,
                          int startIndexInclusive, int endIndexExclusive, HttpRequest httpRequest,
                          ByteArray requestByteArray) {
        this.api = api;
        this.logger = api.logging();
        this.messageEditorHttpRequestResponse = messageEditorHttpRequestResponse;
        this.startIndexInclusive = startIndexInclusive;
        this.endIndexExclusive = endIndexExclusive;
        this.httpRequest = httpRequest;
        this.requestByteArray = requestByteArray;

        initUI();
    }

    private void initUI() {
        setTitle("Big Chars Config");
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        buttonGroup = new ButtonGroup();

        defaultRadioButton = new JRadioButton("Default");
        defaultRadioButton.setSelected(true);
        defaultRadioButton.addActionListener(e -> {
            if (defaultRadioButton.isSelected()){
                disableCustomConfig();
            }
        });
        buttonGroup.add(defaultRadioButton);

        customRadioButton = new JRadioButton("Custom");
        customRadioButton.addActionListener(e -> {
            if (customRadioButton.isSelected()){
                enableCustomConfig();
            }
        });
        buttonGroup.add(customRadioButton);

        northPanel.add(defaultRadioButton);
        northPanel.add(customRadioButton);

        add(northPanel, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        centerPanel.add(new JLabel("Char:"));
        centerPanel.add(charsComboBox);
        
        centerPanel.add(new JLabel("Size:"));
        centerPanel.add(charSizeChooseComboBox);
        
        centerPanel.add(customSizeTextField);

        add(centerPanel, BorderLayout.CENTER);
        disableCustomConfig();

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (defaultRadioButton.isSelected()){
                processDefaultConfig();
            }else{
                processCustomConfig();
            }
            dispose();
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        southPanel.add(okButton);
        southPanel.add(cancelButton);

        add(southPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void processCustomConfig() {
        String charSizeChoose = (String) charSizeChooseComboBox.getSelectedItem();
        if ("customSize".equals(charSizeChoose)){
            String customSize = customSizeTextField.getText();
            if (customSize.isEmpty()){
                logger.logToOutput("Custom size can not be empty");
                return;
            }
            int customSizeInt;
            try {
                customSizeInt = ByteSizeHelper.getByteSize(customSize);
            }catch (Exception e){
                logger.logToOutput("Custom size is not valid");
                return;
            }
            if(-1 == ByteSizeHelper.getByteSize(customSize)){
                logger.logToOutput("Custom size is not valid");
                return;
            }
            String charStr = (String) charsComboBox.getSelectedItem();
            String insertStr = INSERT_STR_FORMAT.formatted(customSizeInt, charStr);

            ByteArray insertData = ByteArray.byteArray(insertStr);
            ByteArray topOfRequest = requestByteArray.subArray(0, startIndexInclusive);
            ByteArray bottomOfRequest = requestByteArray.subArray(endIndexExclusive, requestByteArray.length());
            ByteArray newRequest = topOfRequest.withAppended(insertData).withAppended(bottomOfRequest);
            HttpRequest newHttpRequest = HttpRequest.httpRequest(newRequest);
            messageEditorHttpRequestResponse.setRequest(newHttpRequest);
        }else {
            String charStr = (String) charsComboBox.getSelectedItem();
            int charSize = GlobalConst.CHAR_SIZE_MAP.get((String)charSizeChooseComboBox.getSelectedItem());
            if (charSize <= -1){
                logger.logToOutput("Char size is not valid");
            }
            String insertStr = INSERT_STR_FORMAT.formatted(charSize, charStr);

            ByteArray insertData = ByteArray.byteArray(insertStr);
            ByteArray topOfRequest = requestByteArray.subArray(0, startIndexInclusive);
            ByteArray bottomOfRequest = requestByteArray.subArray(endIndexExclusive, requestByteArray.length());
            ByteArray newRequest = topOfRequest.withAppended(insertData).withAppended(bottomOfRequest);
            HttpRequest newHttpRequest = HttpRequest.httpRequest(newRequest);
            messageEditorHttpRequestResponse.setRequest(newHttpRequest);
        }
    }

    private void processDefaultConfig() {
        String charStr = "R";
        int charSize = 1024 * 1024;
        String insertStr = INSERT_STR_FORMAT.formatted(charSize, charStr);

        ByteArray insertData = ByteArray.byteArray(insertStr);
        ByteArray topOfRequest = requestByteArray.subArray(0, startIndexInclusive);
        ByteArray bottomOfRequest = requestByteArray.subArray(endIndexExclusive, requestByteArray.length());
        ByteArray newRequest = topOfRequest.withAppended(insertData).withAppended(bottomOfRequest);
        HttpRequest newHttpRequest = HttpRequest.httpRequest(newRequest);
        messageEditorHttpRequestResponse.setRequest(newHttpRequest);
    }

    private void enableCustomConfig() {
        centerPanel.setEnabled(true);
        charsComboBox.setEnabled(true);
        charSizeChooseComboBox.setEnabled(true);
        customSizeTextField.setEnabled(true);
    }

    private void disableCustomConfig() {
        centerPanel.setEnabled(false);
        charsComboBox.setEnabled(false);
        charSizeChooseComboBox.setEnabled(false);
        customSizeTextField.setEnabled(false);
    }

    public void showDialog(){
        setVisible(true);
    }
}
