package rastro.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class CommController {
    public enum CommResult {
        ok, error
    };
    private String portName = "";
    private int baudRate = 115200;
    private SerialPort sPort = null;
    private static Set<String> allocatedPorts = new HashSet<String>();
    private InputStream in;
    private OutputStream out;
    private final int INP_BUFF_SIZE = 2048;

    public CommController() {
    }

    public CommController(String name, int rate) {
        setPortName(name);
        baudRate = rate;
    }

    public CommResult openPort() {
        closePort();
        CommPort port;
        try {
            port = CommPortIdentifier.getPortIdentifier(portName).open(this.getClass().getName(), 2000);
        } catch (NoSuchPortException e) {
            return CommResult.error;
        } catch (PortInUseException e) {
            return CommResult.error;
        }
        if (!(port instanceof SerialPort)) {
            return CommResult.error;
        }
        sPort = (SerialPort) port;
        try {
            sPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            sPort.setInputBufferSize(INP_BUFF_SIZE);
            in = sPort.getInputStream();
            out = sPort.getOutputStream();            
        } catch (UnsupportedCommOperationException | IOException e) {
            return CommResult.error;
        }
        return CommResult.ok;
    }

    public void closePort() {
        if (sPort != null) {
            sPort.close();
            sPort = null;
            allocatedPorts.remove(portName);
        }
    }

    public static String[] getPortList() {
        @SuppressWarnings("unchecked")
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portList = new ArrayList<String>();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portId = portEnum.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                portList.add(portId.getName());
            }
        }
        return portList.toArray(new String[] {});
    }

    public static boolean isAllocated(String name) {
        return allocatedPorts.contains(name);
    }

    public static boolean isBusy(String name) {
        try {
            return CommPortIdentifier.getPortIdentifier(name).isCurrentlyOwned();
        } catch (NoSuchPortException e) {
            return true;
        }
    }

    public void setPortName(String name) {
        portName = name;
        allocatedPorts.add(name);
    }

    public void setBaudRate(int rate) {
        baudRate = rate;
    }

    public synchronized CommResult sendCommand(ICommCommand cmd) {
        try {
            sPort.enableReceiveTimeout(cmd.getTimeout());
            if (!cmd.sendData(out)) {
                return CommResult.error;
            }
            if( !cmd.receiveData(in)) {
                return CommResult.error;
            }
        } catch (UnsupportedCommOperationException | IOException e) {
            return CommResult.error;
        }
        return CommResult.ok;
    }
}
