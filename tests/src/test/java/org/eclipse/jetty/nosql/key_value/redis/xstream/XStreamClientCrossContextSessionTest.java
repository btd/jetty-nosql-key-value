package org.eclipse.jetty.nosql.key_value.redis.xstream;

import org.eclipse.jetty.nosql.key_value.redis.AbstractRedisClientCrossContextSessionTest;
import org.eclipse.jetty.nosql.key_value.redis.XStreamRedisTestServer;
import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamClientCrossContextSessionTest extends AbstractRedisClientCrossContextSessionTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new XStreamRedisTestServer(port);
    }
}
