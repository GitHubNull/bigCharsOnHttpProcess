package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;

import javax.swing.*;
import java.awt.*;

public class BigCharsDialog extends JDialog {

    private JPanel northPanel;
    private JPanel centerPanel;
    private JPanel southPanel;

    ButtonGroup buttonGroup;
    JRadioButton defaultRadioButton;
    JRadioButton customRadioButton;

    private final static String[] chars = {"G", "H", "J", "L", "M", "N", "Q", "R", "T", "U", "Y"};
    private final static JComboBox<String> charsComboBox = new JComboBox<>(chars);

    private final static String[] charSizeChoose = {"512", "1KB", "2KB", "4KB", "8KB", "16KB", "32KB", "64KB", "128KB", "256KB", "512KB", "1MB","customSize"};
    private final static JComboBox<String> charSizeChooseComboBox = new JComboBox<>(charSizeChoose);
    private final static JTextField customSizeTextField = new JTextField();

    JButton okButton;
    JButton cancelButton;

    MontoyaApi api;
    Logging logger;
    HttpRequestResponse httpRequestResponse;
    int caretPosition;

    public BigCharsDialog(MontoyaApi api, HttpRequestResponse httpRequestResponse, int caretPosition) {
        this.api = api;
        this.logger = api.logging();
        this.httpRequestResponse = httpRequestResponse;
        this.caretPosition = caretPosition;

        initUI();
    }

    private void initUI() {
        setTitle("Big Chars Config");
        setLayout(new BorderLayout());

        northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

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
        centerPanel.setLayout(new FlowLayout());
        
        centerPanel.add(new JLabel("Char:"));
        centerPanel.add(charsComboBox);
        
        centerPanel.add(new JLabel("Size:"));
        centerPanel.add(charSizeChooseComboBox);
        
        centerPanel.add(customSizeTextField);

        add(centerPanel, BorderLayout.CENTER);

        southPanel = new JPanel();
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
        cancelButton.addActionListener(e -> {
            dispose();
        });

        southPanel.add(okButton);
        southPanel.add(cancelButton);

        add(southPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void processCustomConfig() {
        String charStr = charsComboBox.getSelectedItem().toString();

    }

    private void processDefaultConfig() {
        String charStr = "R";
        int charSize = 1024 * 1024;
        String insertStr = "{{__#%d%s#__}}".formatted(charSize, charStr);

        ByteArray insertData = ByteArray.byteArray(insertStr);
        HttpRequest httpRequest = httpRequestResponse.request();
        ByteArray bodyArray = httpRequest.body();
        // 计算插入位置相对于 body 的偏移量
        int insertOffset = caretPosition - httpRequest.bodyOffset();
        ByteArray bodyLeft = bodyArray.subArray(0, insertOffset);
        ByteArray bodyRight = bodyArray.subArray(insertOffset, bodyArray.length());
        ByteArray newBody = bodyLeft.withAppended(insertData).withAppended(bodyRight);
        httpRequest = httpRequest.withBody(newBody);

        // set back

    }

    private void enableCustomConfig() {
        customRadioButton.setSelected(true);
        customRadioButton.setEnabled(true);
        customSizeTextField.setEnabled(true);
        charSizeChooseComboBox.setEnabled(true);
        charsComboBox.setEnabled(true);
        customSizeTextField.setText("");
        charSizeChooseComboBox.setSelectedIndex(0);
        charsComboBox.setSelectedIndex(0);
    }

    private void disableCustomConfig() {
        customRadioButton.setSelected(false);
        customRadioButton.setEnabled(false);
        customSizeTextField.setEnabled(false);
        charSizeChooseComboBox.setEnabled(false);
        charsComboBox.setEnabled(false);
    }

    public void showDialog(){
        setVisible(true);
    }
}
