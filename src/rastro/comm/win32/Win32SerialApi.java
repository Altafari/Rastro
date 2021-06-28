package rastro.comm.win32;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import rastro.comm.ISerialApi;
import rastro.comm.ISerialPort;

public class Win32SerialApi implements ISerialApi {

	interface CommApi extends Library {
		WinNT.HANDLE CreateFileA(String fName, int desiredAccess, int shareMode, WinBase.SECURITY_ATTRIBUTES security,
				int creationDisposition, int flagsAndAttributes, WinNT.HANDLE templateFile);

		boolean ReadFile(WinNT.HANDLE handle, byte[] pBuffer, int nBytesToRead, IntByReference nBytesRead,
				WinBase.OVERLAPPED overlapped);

		boolean WriteFile(WinNT.HANDLE handle, byte[] pBuffer, int nBytesToWrite, IntByReference nBytesWritten,
				WinBase.OVERLAPPED overlapped);

		boolean GetCommState(WinNT.HANDLE handle, WinBase.DCB dcb);

		boolean SetCommState(WinNT.HANDLE handle, WinBase.DCB dcb);

		boolean GetCommTimeouts(WinNT.HANDLE handle, WinBase.COMMTIMEOUTS timeouts);

		boolean SetCommTimeouts(WinNT.HANDLE handle, WinBase.COMMTIMEOUTS timeouts);

		boolean CloseHandle(WinNT.HANDLE handle);
	}

	protected static CommApi api = null;

	static {
		if (Platform.isWindows()) {
			try {
				api = Native.load("Kernel32", CommApi.class);
			} catch (Exception e) {
				;
			}
		}
	}

	@Override
	public ArrayList<String> listSerialPorts() {
		ArrayList<String> ports = new ArrayList<String>();
		for (int i = 1; i < 100; ++i) {
			String name = "COM" + Integer.toString(i);
			WinNT.HANDLE h = openDevice(name);
			if (h != null) {
				ports.add(name);
				api.CloseHandle(h);
			}
		}
		return ports;
	}

	@Override
	public ISerialPort openPort(String name, int baud) throws IOException {
		return new Win32SerialPort(name, baud, api);
	}

	public static boolean isValidPlatform() {
		return api != null;
	}

	static WinNT.HANDLE openDevice(String name) {
		WinNT.HANDLE h = api.CreateFileA("\\.\\" + name, 0xC0000000, 0, null, 3, 0x80, null);
		if (Pointer.nativeValue(h.getPointer()) == -1) {
			return null;
		}
		return h;
	}
}
