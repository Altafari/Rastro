package rastro.model;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rastro.comm.ISerialPort;
import rastro.controller.ICommCommand;

public class GrblSettings {

	public enum GrblSetting {
		STEP_PER_MM_X, STEP_PER_MM_Y, MAX_RATE_X, MAX_RATE_Y, ACC_X, ACC_Y, MAX_TRAVEL_X, MAX_TRAVEL_Y
	};

	private final HashMap<GrblSetting, Float> settingsMap;
	private final GrblSettingParser parser;

	private final class LoadCommand implements ICommCommand {

		@Override
		public boolean invoke(ISerialPort port) throws IOException {
			port.write("$$\n".getBytes("US-ASCII"));
			return receiveConfiguration(port);
		}

		private boolean receiveConfiguration(ISerialPort port) {
			StringBuilder sb = new StringBuilder();
			while (true) {
				try {
					byte[] buffer = port.read(1);
					sb.append((char) buffer[0]);
					if (sb.indexOf("\nok") != -1) {
						return parseSettings(sb.toString());
					}
				} catch (IOException e) {
					return false;
				}
			}
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
				return;
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

	public GrblSettings() {
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

	public ICommCommand getLoadCommand() {
		return new LoadCommand();
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
