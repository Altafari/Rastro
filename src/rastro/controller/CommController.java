package rastro.controller;

import java.io.IOException;
import java.util.HashMap;

import rastro.comm.ISerialApi;
import rastro.comm.ISerialPort;
import rastro.comm.SerialApiFactory;

public class CommController {
	public enum CommResult {
		ok, error
	};

	private static ISerialApi api;
	private static HashMap<String, ISerialPort> openPorts = new HashMap<String, ISerialPort>();
	private String portName = "";
	private int baudRate = 115200;
	private ISerialPort sPort = null;

	static {
		api = SerialApiFactory.getSerialApi();
	}

	public CommController() {
	}

	public CommController(String name, int rate) {
		setPortName(name);
		baudRate = rate;
	}

	public CommResult openPort() {
		closePort();
		if (openPorts.containsKey(portName)) {
			return CommResult.error;
		}
		try {
			sPort = api.openPort(portName, baudRate);
			openPorts.put(portName, sPort);
		} catch (IOException e) {
			return CommResult.error;
		}
		return CommResult.ok;
	}

	public void closePort() {
		if (sPort != null) {
			sPort.close();
			openPorts.remove(portName);
			sPort = null;
		}
	}

	public static String[] getPortList() {
		return api.listSerialPorts().toArray(new String[] {});
	}

	public void setPortName(String name) {
		closePort();
		portName = name;
	}

	public void setBaudRate(int rate) {
		baudRate = rate;
	}

	public synchronized CommResult sendCommand(ICommCommand cmd) {
		try {
			boolean success = cmd.invoke(sPort);
			if (success) {
				return CommResult.ok;
			} else {
				return CommResult.error;
			}
		} catch (IOException e) {
			return CommResult.error;
		}
	}
}
