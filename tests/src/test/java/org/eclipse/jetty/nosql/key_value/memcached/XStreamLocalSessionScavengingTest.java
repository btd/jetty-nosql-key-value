package org.eclipse.jetty.nosql.key_value.memcached;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamLocalSessionScavengingTest extends AbstractMemcachedLocalSessionScavengingTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new XStreamMemcachedTestServer(port);
    }
}
