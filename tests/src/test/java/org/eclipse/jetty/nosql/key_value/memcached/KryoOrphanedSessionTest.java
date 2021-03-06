package org.eclipse.jetty.nosql.key_value.memcached;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoOrphanedSessionTest extends AbstractMemcachedOrphanedSessionTest
{
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
       return new KryoMemcachedTestServer(port,max,scavenge);
    }
}
