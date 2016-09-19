package rastro.controller;

import java.io.InputStream;
import java.io.OutputStream;

public interface ICommCommand {
    boolean sendData(OutputStream os);
    boolean receiveData(InputStream is);
    int getTimeout();
    final int DEFAULT_TIMEOUT = 200;
}
