package rastro.comm;

import java.io.IOException;

public interface ISerialPort {
	void write(byte[] data) throws IOException;

	byte[] read(int num_bytes) throws IOException;

	void setTimeout(int timeout) throws IOException;

	void close();
}
