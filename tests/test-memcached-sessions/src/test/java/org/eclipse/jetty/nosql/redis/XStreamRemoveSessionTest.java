package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamRemoveSessionTest extends AbstractRedisRemoveSessionTest
{ 
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new XStreamRedisTestServer(port,max,scavenge);
    }
}
