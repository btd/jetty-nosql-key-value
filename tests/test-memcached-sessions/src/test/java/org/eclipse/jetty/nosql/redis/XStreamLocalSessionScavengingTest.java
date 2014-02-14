package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamLocalSessionScavengingTest extends AbstractRedisLocalSessionScavengingTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new XStreamRedisTestServer(port);
    }
}
