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
        ok, error, parseError, noResponse
    };
    private String portName = "";
    private int baudRate = 115200;
    private SerialPort sPort = null;
    private static Set<String> allocatedPorts = new HashSet<String>();
    private InputStream in;
    private OutputStream out;

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
        } catch (UnsupportedCommOperationException e) {
            return CommResult.error;
        }
        try {
            in = sPort.getInputStream();
            out = sPort.getOutputStream();
        } catch (IOException e) {
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

    public CommResult write(byte[] data) {
        try {
            out.write(data);
        } catch (IOException | NullPointerException e) {
            return CommResult.error;
        }
        return CommResult.ok;
    }
  
    public int read(byte[] buff, int timeout) {
        try {
        //    sPort.enableReceiveThreshold(buff.length);            
            if (timeout > 0) {
                sPort.enableReceiveTimeout(timeout);
            } else {
                sPort.disableReceiveTimeout();
            }
            int res = 0;
            while (res != -1) {
                res = in.read(buff);
            }
            System.out.println(new String(buff));
            return res;
        } catch (IOException | NullPointerException | UnsupportedCommOperationException e) {
            return -1;
        }
    }
    
    public CommResult sendCommand(ICommCommand cmd) {
        if (write(cmd.getRequest()) != CommResult.ok) {
            return CommResult.error;
        }
        int bytesRead = read(cmd.getResponseBufer(), cmd.getTimeout());
        if (bytesRead >= 0) {
            if (!cmd.parseResponse(bytesRead)) {
                return CommResult.parseError;
            } else {
                return CommResult.ok;
            }            
        } else {
            return CommResult.noResponse;
        }
    }
}
