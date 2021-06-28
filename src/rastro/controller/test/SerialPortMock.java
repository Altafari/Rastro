package rastro.controller.test;

import java.io.IOException;

import rastro.comm.ISerialPort;

public class SerialPortMock implements ISerialPort {

	@Override
	public void write(byte[] data) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] read(int num_bytes) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTimeout(int timeout) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
