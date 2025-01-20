package top.oxff.utils;

import java.util.regex.Pattern;

public class ByteSizeHelper {
    // 定义正则表达式为静态常量，并忽略大小写
    private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("^\\d+(B|KB|MB)?$", Pattern.CASE_INSENSITIVE);
    public static int getByteSize(String byteSize) {
        if (!validByteSizeChars(byteSize)) {
            return -1;
        }

        byteSize = byteSize.toUpperCase().trim();

        // Extract the numeric part and unit separately
        String numericPart;
        String unit = "";

        if (byteSize.endsWith("B")) {
            byteSize = byteSize.substring(0, byteSize.length() - 1);
            if (byteSize.endsWith("K")) {
                unit = "KB";
                numericPart = byteSize.substring(0, byteSize.length() - 1);
            } else if (byteSize.endsWith("M")) {
                unit = "MB";
                numericPart = byteSize.substring(0, byteSize.length() - 1);
            } else {
                unit = "B";
                numericPart = byteSize;
            }
        } else {
            // Handle pure numeric input without unit
            numericPart = byteSize;
        }

        try {
            int value = Integer.parseInt(numericPart);
            return switch (unit) {
                case "KB" -> value * 1024;
                case "MB" -> value * 1024 * 1024;
                case "B" -> value;
                default -> value; // Pure numeric input
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public static boolean validByteSizeChars(String byteSize) {
        // 检查输入是否为 null 或者是空白字符串
        if (byteSize == null || byteSize.trim().isEmpty()) {
            return false;
        }

        // 去除字符串两端的空白字符
        byteSize = byteSize.trim();

        // 如果包含负号，根据业务需求决定是否允许负数
        if (byteSize.contains("-")) {
            // 如果不允许负数，直接返回 false
            return false;
            // 如果允许负数，可以在这里添加额外的处理逻辑
            // 例如：去掉负号后继续验证
            // byteSize = byteSize.replace("-", "");
        }

        // 使用预编译正则表达式进行匹配
        return BYTE_SIZE_PATTERN.matcher(byteSize).matches();
    }
}
