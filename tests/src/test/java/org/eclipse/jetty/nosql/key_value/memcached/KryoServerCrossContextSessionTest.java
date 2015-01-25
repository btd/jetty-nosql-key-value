package org.eclipse.jetty.nosql.key_value.memcached;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoServerCrossContextSessionTest extends AbstractMemcachedServerCrossContextSessionTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoMemcachedTestServer(port);
    }
}
