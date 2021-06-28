package rastro.comm;

import rastro.comm.posix.PosixSerialApi;
import rastro.comm.win32.Win32SerialApi;

public class SerialApiFactory {
	public static ISerialApi getSerialApi() {
		if (Win32SerialApi.isValidPlatform()) {
			return new Win32SerialApi();
		}
		if (PosixSerialApi.isValidPlatform()) {
			return new PosixSerialApi();
		}
		throw new RuntimeException("Unsupported platform");
	}
}
