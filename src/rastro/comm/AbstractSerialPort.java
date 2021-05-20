package rastro.comm;

public class AbstractSerialPort {
	protected String name;
	protected int baud;
	protected final static int DEFAULT_TIMEOUT = 200;
	
	protected AbstractSerialPort(String devName, int devBaud) {
		name = devName;
		baud = devBaud;
	}
}
