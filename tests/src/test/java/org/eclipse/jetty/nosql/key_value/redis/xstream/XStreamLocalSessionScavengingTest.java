package org.eclipse.jetty.nosql.key_value.redis.xstream;

import org.eclipse.jetty.nosql.key_value.redis.AbstractRedisLocalSessionScavengingTest;
import org.eclipse.jetty.nosql.key_value.redis.XStreamRedisTestServer;
import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamLocalSessionScavengingTest extends AbstractRedisLocalSessionScavengingTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new XStreamRedisTestServer(port);
    }
}
