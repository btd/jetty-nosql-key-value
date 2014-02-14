package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamSessionSavingValueTest extends AbstractRedisSessionSavingValueTest
{
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new XStreamRedisTestServer(port,max,scavenge,true);
    }
}
