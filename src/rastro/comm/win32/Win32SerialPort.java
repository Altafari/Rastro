package rastro.comm.win32;

import java.io.IOException;

import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import rastro.comm.AbstractSerialPort;
import rastro.comm.ISerialPort;

public class Win32SerialPort extends AbstractSerialPort implements ISerialPort {

	private Win32SerialApi.CommApi api;
	private WinNT.HANDLE handle;
	private WinBase.DCB currentConfig;
	private WinBase.DCB storedConfig;
	private WinBase.COMMTIMEOUTS currentTimeouts;
	private WinBase.COMMTIMEOUTS storedTimeouts;

	public Win32SerialPort(String devName, int devBaud, Win32SerialApi.CommApi devApi) throws IOException {
		super(devName, devBaud);
		api = devApi;
		storedConfig = new WinBase.DCB();
		storedTimeouts = new WinBase.COMMTIMEOUTS();
		handle = Win32SerialApi.openDevice(name);
		if (handle == null) {
			throw new IOException("No such device " + name);
		}
		boolean success = api.GetCommState(handle, storedConfig);
		currentConfig = configureDcb(storedConfig);
		success &= api.SetCommState(handle, currentConfig);
		currentTimeouts = configureTimeouts();
		success &= api.SetCommTimeouts(handle, currentTimeouts);
		if (!success) {
			throw new IOException("Failed to configure device " + name);
		}
	}

	@Override
	public void write(byte[] data) throws IOException {
		IntByReference nBytesWritten = new IntByReference(0);
		api.WriteFile(handle, data, data.length, nBytesWritten, null);
		if (nBytesWritten.getValue() != data.length) {
			throw new IOException("Failed to write data to serial port");
		}
	}

	@Override
	public byte[] read(int numBytes) throws IOException {
		IntByReference nBytesRead = new IntByReference(0);
		byte[] buffer = new byte[numBytes];
		api.ReadFile(handle, buffer, numBytes, nBytesRead, null);
		if (nBytesRead.getValue() != numBytes) {
			throw new IOException("Failed to read data from serial port");
		}
		return buffer;
	}

	@Override
	public void setTimeout(int timeout) throws IOException {
		currentTimeouts.ReadTotalTimeoutConstant.setValue(timeout);
		boolean success = api.SetCommTimeouts(handle, currentTimeouts);
		if (!success) {
			throw new IOException("Failed to set timeouts" + name);
		}
	}

	@Override
	public void close() {
		api.SetCommTimeouts(handle, storedTimeouts);
		api.SetCommState(handle, storedConfig);
		api.CloseHandle(handle);
	}

	private WinBase.DCB configureDcb(WinBase.DCB current) {
		WinBase.DCB result = new WinBase.DCB();
		result.clear();
		result.DCBlength.setValue(current.DCBlength.intValue());
		result.BaudRate.setValue(baud);
		WinBase.DCB.DCBControllBits controlBits = new WinBase.DCB.DCBControllBits();
		controlBits.setfBinary(true);
		result.controllBits = controlBits;
		result.ByteSize.setValue(8);
		result.Parity.setValue(0);
		result.StopBits.setValue(0);
		return result;
	}

	private WinBase.COMMTIMEOUTS configureTimeouts() {
		WinBase.COMMTIMEOUTS result = new WinBase.COMMTIMEOUTS();
		result.ReadTotalTimeoutConstant.setValue(DEFAULT_TIMEOUT);
		return result;
	}
}
