package rastro.controller;

public interface ICommCommand {
    byte[] getRequest();
    byte[] getResponseBufer();
    boolean parseResponse(int bytesRead);
    int getTimeout();
    final int DEFAULT_TIMEOUT = 200;
}
