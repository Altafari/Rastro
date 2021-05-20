package rastro.controller;

import java.io.IOException;

import rastro.comm.ISerialPort;

public interface ICommCommand {
	boolean invoke(ISerialPort port) throws IOException;
}
