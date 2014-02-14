package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoSessionMigrationTest extends AbstractRedisSessionMigrationTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoRedisTestServer(port);
    }
}
