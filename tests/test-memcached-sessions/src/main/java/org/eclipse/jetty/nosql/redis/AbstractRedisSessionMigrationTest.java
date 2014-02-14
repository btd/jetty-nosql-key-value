package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractSessionMigrationTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Test;

public abstract class AbstractRedisSessionMigrationTest extends AbstractSessionMigrationTest
{
    public AbstractTestServer createServer(int port)
    {
        return new RedisTestServer(port);
    }

    @Test
    public void testSessionMigration() throws Exception
    {
        super.testSessionMigration();
    }
}
