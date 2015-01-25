package org.eclipse.jetty.nosql.key_value;

public abstract class AbstractKeyValueStoreClient implements KeyValueStoreClient {

    protected String _serverString = null;
    protected int timeout = 1000;

    public AbstractKeyValueStoreClient(String serverString) {
        setServerString(serverString);
    }

    public String getServerString() {
        return _serverString;
    }

    public void setServerString(String _serverString) {
        this._serverString = _serverString;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int _timeoutInMs) {
        this.timeout = _timeoutInMs;
    }
}
