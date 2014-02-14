package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoRemoveSessionTest extends AbstractRedisRemoveSessionTest
{ 
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new KryoRedisTestServer(port,max,scavenge);
    }
}
