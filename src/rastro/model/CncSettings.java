package rastro.model;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rastro.controller.ICommCommand;

public class CncSettings {
    
    public enum GrblSetting {STEP_PER_MM_X, STEP_PER_MM_Y, MAX_RATE_X,
            MAX_RATE_Y, ACC_X, ACC_Y, MAX_TRAVEL_X, MAX_TRAVEL_Y};

    private final HashMap<GrblSetting, Float> settingsMap;
    private final GrblSettingParser parser;
    
    public final class Load implements ICommCommand {
        
        private static final int RESP_BUFF_SIZE = 2048;
        private final byte[] responseBuffer = new byte[RESP_BUFF_SIZE];
        
        @Override
        public byte[] getRequest() {
            return "$$\n".getBytes(StandardCharsets.US_ASCII);
        }

        @Override
        public boolean parseResponse(int bytesRead) {        
            return parseSettings(new String(
                    Arrays.copyOfRange(responseBuffer, 0, bytesRead), StandardCharsets.US_ASCII));
        }

        @Override
        public int getTimeout() {
            return DEFAULT_TIMEOUT;
        }

        @Override
        public byte[] getResponseBufer() {
            return responseBuffer;
        }        
    }

    private class GrblSettingParser {
        private final Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        private final String prefix;
        private final GrblSetting key;
        private GrblSettingParser next;
        
        GrblSettingParser(String valPrefix, GrblSetting settingKey) {
            prefix = valPrefix;
            key = settingKey;
        }
        
        public GrblSettingParser setNext(GrblSettingParser nextSetting) {
            next = nextSetting;
            return nextSetting;
        }
        
        public void parseLine(String line) {
            if (!line.startsWith(prefix)) {                
                if (next != null) {
                    next.parseLine(line);
                }
            }
            Matcher m = pattern.matcher(line.substring(prefix.length()));
            if (m.find()) {
                String s = m.group();
                float val;
                try {
                    val = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    return;
                }
                settingsMap.put(key, val);
            }
        }
    };

    public CncSettings() {
        settingsMap = new HashMap<GrblSetting, Float>();        
        parser = new GrblSettingParser("$100=", GrblSetting.STEP_PER_MM_X);
        parser.setNext(new GrblSettingParser("$101=", GrblSetting.STEP_PER_MM_Y))
        .setNext(new GrblSettingParser("$110=", GrblSetting.MAX_RATE_X))
        .setNext(new GrblSettingParser("$111=", GrblSetting.MAX_RATE_Y))
        .setNext(new GrblSettingParser("$120=", GrblSetting.ACC_X))
        .setNext(new GrblSettingParser("$121=", GrblSetting.ACC_Y))
        .setNext(new GrblSettingParser("$130=", GrblSetting.MAX_TRAVEL_X))
        .setNext(new GrblSettingParser("$131=", GrblSetting.MAX_TRAVEL_Y));        
    }
    
    public Map<GrblSetting, Float> getSettings() {
        return Collections.unmodifiableMap(settingsMap);
    }
    
    private boolean parseSettings(String settings) {
        settingsMap.clear();
        String[] lines = settings.split("\\n");
        for (String ln : lines) {
            parser.parseLine(ln);
        }
        for (GrblSetting s : GrblSetting.values()) {
            if (!settingsMap.containsKey(s)) {
                return false;
            }
        }
        return true;
    }
}
