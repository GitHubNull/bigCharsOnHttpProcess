package top.oxff;

import java.util.HashMap;
import java.util.Map;

public class GlobalConst {
    public final static String INSERT_STR_FORMAT = "{{__#%d%s#__}}";
    public final static String[] CHAR_SIZE_CHOOSE = {"512", "1KB", "2KB", "4KB", "8KB", "16KB", "32KB", "64KB", "128KB",
                                                        "256KB", "512KB", "1MB","customSize"};
    public final static Map<String, Integer> CHAR_SIZE_MAP;
    static {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("512", 512);
        map.put("1KB", 1024);
        map.put("2KB", 1024*2);
        map.put("4KB", 1024*4);
        map.put("8KB", 1024*8);
        map.put("16KB", 1024*16);
        map.put("32KB", 1024*32);
        map.put("64KB", 1024*64);
        map.put("128KB", 1024*128);
        map.put("256KB", 1024*256);
        map.put("512KB", 1024*512);
        map.put("1MB", 1024*1024);
        CHAR_SIZE_MAP = map;
    }
}
