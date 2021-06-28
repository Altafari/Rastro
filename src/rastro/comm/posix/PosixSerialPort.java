package rastro.comm.posix;

import java.io.IOException;
import java.util.HashMap;

import com.sun.jna.platform.win32.BaseTSD;

import rastro.comm.AbstractSerialPort;
import rastro.comm.ISerialPort;

public class PosixSerialPort extends AbstractSerialPort implements ISerialPort {
	
	private int fd;
	private PosixSerialApi.CommApi api;
	private int baudConst;
	private Termios currentConfig;
	private Termios storedConfig;
	
	private static final int O_RDWR = 00000002;
	private static final int O_NOCTTY = 00000400;
	private static final int CLOCAL = 0004000;
	private static final int CREAD = 0000200;
	private static final int CS8 = 0000060;
	private static final int VTIME = 5;
	
	private static final HashMap<Integer, Integer> baudTable = new HashMap<Integer, Integer>()
	{
		private static final long serialVersionUID = 1L;
		{ put(50, 0000001); put(75, 0000002); put(110, 0000003); put(134, 0000004);
		  put(150, 0000005); put(200, 0000006); put(300, 0000007); put(600, 0000010);
		  put(1200, 0000011); put(1800, 0000012); put(2400, 0000013); put(4800, 0000014);
		  put(9600, 0000015); put(19200, 0000016); put(38400, 0000017); put(57600, 0010001);
		  put(115200, 0010002); put(230400, 0010003); put(460800, 0010004); put(500000, 0010005);
		  put(576000, 0010006); put(921600, 0010007); put(1000000, 0010010); put(1152000, 0010011);
		  put(1500000, 0010012); put(2000000, 0010013); put(2500000, 0010014); put(3000000, 0010015);
		  put(3500000, 0010016); put(4000000, 0010017);
		}
	};

	public PosixSerialPort(String devName, int devBaud, PosixSerialApi.CommApi devApi) throws IOException {
		super(devName, devBaud);
		Integer bc = baudTable.get(devBaud);
		if (bc == null) {
			throw new IOException(String.format("Unsupported baudrate: %d", devBaud));
		}
		baudConst = bc;
		api = devApi;
		currentConfig = new Termios();
		storedConfig = new Termios();
		fd = api.open("/dev/" + name, O_RDWR | O_NOCTTY);
		if (fd == -1) {
			throw new IOException("Can't open serial port");
		}
		int error = api.tcgetattr(fd, storedConfig);
		configureTermios(currentConfig);
		error |= api.tcsetattr(fd, 0, currentConfig);
		if(error != 0) {
			throw new IOException("Failed to configure device " + name);
		}
	}
	
	@Override
	public void write(byte[] data) throws IOException {
		BaseTSD.SSIZE_T count = api.write(fd, data, new BaseTSD.SIZE_T(data.length));
		if (count.intValue() != data.length) {
			throw new IOException("Failed to write data to serial port");
		}
	}

	@Override
	public byte[] read(int numBytes) throws IOException {
		byte[] res = new byte[numBytes];
		BaseTSD.SSIZE_T count = api.read(fd, res, new BaseTSD.SIZE_T(numBytes));
		if (count.intValue() != numBytes) {
			throw new IOException("Failed to read data from serial port");
		}
		return res;
	}

	@Override
	public void setTimeout(int timeout) throws IOException {
		currentConfig.c_cc[VTIME] = (byte) (timeout / 100);
		int error = api.tcsetattr(fd, 0, currentConfig);
		if(error != 0) {
			throw new IOException("Failed to set timeouts " + name);
		}
	}

	@Override
	public void close() {
		api.tcsetattr(fd, 0, storedConfig);
		api.close(fd);
	}
	
	private void configureTermios(Termios termios) {
		termios.c_iflag = 0;
		termios.c_oflag = 0;
		termios.c_cflag = CLOCAL | CREAD | CS8 | baudConst;
		termios.c_lflag = 0;
		termios.c_line  = 0;
		termios.c_ospeed = baudConst;
		termios.c_ispeed = baudConst;
		termios.c_cc[VTIME] = DEFAULT_TIMEOUT / 100;
	}
}
