package org.eclipse.jetty.nosql.key_value.redis.xstream;

import org.eclipse.jetty.nosql.key_value.redis.AbstractRedisRemoveSessionTest;
import org.eclipse.jetty.nosql.key_value.redis.XStreamRedisTestServer;
import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamRemoveSessionTest extends AbstractRedisRemoveSessionTest
{ 
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new XStreamRedisTestServer(port,max,scavenge);
    }
}
