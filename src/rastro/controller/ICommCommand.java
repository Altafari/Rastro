package rastro.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ICommCommand {
    boolean sendData(OutputStream os) throws IOException;
    boolean receiveData(InputStream is) throws IOException;
    int getTimeout();
    final int DEFAULT_TIMEOUT = 200;
}
