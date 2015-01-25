package org.eclipse.jetty.nosql.key_value.redis;

import org.eclipse.jetty.server.session.AbstractInvalidationSessionTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Test;

public abstract class AbstractRedisInvalidationSessionTest extends AbstractInvalidationSessionTest
{
    public AbstractTestServer createServer(int port)
    {
        return new RedisTestServer(port);
    }

    public void pause()
    {
    }

    @Test
    public void testInvalidation() throws Exception
    {
        super.testInvalidation();
    }
}
