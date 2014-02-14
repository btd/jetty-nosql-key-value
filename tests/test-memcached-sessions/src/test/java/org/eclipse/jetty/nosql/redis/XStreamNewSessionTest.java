package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamNewSessionTest extends AbstractRedisNewSessionTest
{
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new XStreamRedisTestServer(port,max,scavenge);
    }
}
