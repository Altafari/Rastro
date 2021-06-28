package rastro.comm;

import java.io.IOException;
import java.util.ArrayList;

public interface ISerialApi {
	public ArrayList<String> listSerialPorts();

	public ISerialPort openPort(String name, int baud) throws IOException;
}
