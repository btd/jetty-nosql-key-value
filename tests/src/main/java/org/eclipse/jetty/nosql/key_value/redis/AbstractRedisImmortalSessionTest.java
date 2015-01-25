package org.eclipse.jetty.nosql.key_value.redis;

import org.eclipse.jetty.server.session.AbstractImmortalSessionTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Test;

public abstract class AbstractRedisImmortalSessionTest extends AbstractImmortalSessionTest
{
    public AbstractTestServer createServer(int port)
    {
        return new RedisTestServer(port);
    }

    @Test
    public void testImmortalSession() throws Exception
    {
        super.testImmortalSession();
    }
}
