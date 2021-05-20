package rastro.comm.posix;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;

import rastro.comm.ISerialApi;
import rastro.comm.ISerialPort;

public class PosixSerialApi implements ISerialApi {

	protected interface CommApi extends Library {
		int open(String path, int openFlags);

		BaseTSD.SSIZE_T read(int fd, byte[] pBuffer, BaseTSD.SIZE_T nBytesToRead);

		BaseTSD.SSIZE_T write(int fd, byte[] pBuffer, BaseTSD.SIZE_T nBytesToWrite);

		int close(int fd);

		int tcgetattr(int fields, Termios termios);

		int tcsetattr(int fields, int optionalActions, Termios termios);

		Pointer opendir(String path);

		Dirent readdir(Pointer ds);

		int closedir(Pointer ds);
	}

	protected static CommApi api = null;

	static {
		if (Platform.isLinux()) {
			try {
				api = Native.load("c", CommApi.class);
			} catch (Exception e) {
				;
			}
		}
	}

	@Override
	public ArrayList<String> listSerialPorts() {
		Pointer dirStream = api.opendir("/sys/class/tty");
		ArrayList<String> result = new ArrayList<String>();
		Dirent dirEntry;
		while ((dirEntry = api.readdir(dirStream)) != null) {
			byte[] buffer = dirEntry.d_name;
			int end = 0;
			while (end < buffer.length && buffer[end] != 0) {
				++end;
			}
			String deviceName = new String(dirEntry.d_name, 0, end);
			if (deviceName.subSequence(0, Math.min(end, 6)).equals("ttyUSB")) {
				result.add(deviceName);
			}
		}
		api.closedir(dirStream);
		return result;
	}

	@Override
	public ISerialPort openPort(String name, int baud) throws IOException {
		return new PosixSerialPort(name, baud, api);
	}

	public static boolean isValidPlatform() {
		return api != null;
	}
}
