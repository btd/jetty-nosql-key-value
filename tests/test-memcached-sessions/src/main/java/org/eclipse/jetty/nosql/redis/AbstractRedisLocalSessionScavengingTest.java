package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractLocalSessionScavengingTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Test;

public abstract class AbstractRedisLocalSessionScavengingTest extends AbstractLocalSessionScavengingTest
{
    public AbstractTestServer createServer(int port)
    {
        return new RedisTestServer(port);
    }

    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge) {
        return new RedisTestServer(port, max, scavenge);
    }

    @Test
    public void testLocalSessionsScavenging() throws Exception
    {
        super.testLocalSessionsScavenging();
    }
}
