package org.eclipse.jetty.nosql.key_value.memcached;
//========================================================================
//Copyright (c) 2011 Intalio, Inc.
//Copyright (c) 2012 Geisha Tokyo Entertainment, Inc.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//The Eclipse Public License is available at
//http://www.eclipse.org/legal/epl-v10.html
//The Apache License v2.0 is available at
//http://www.opensource.org/licenses/apache2.0.php
//You may elect to redistribute this code under either of these licenses.
//========================================================================


import org.eclipse.jetty.server.session.AbstractLastAccessTimeTest;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.junit.Test;


/**
 * AbstractLastAccessTimeTest
 */
public abstract class AbstractMemcachedLastAccessTimeTest extends AbstractLastAccessTimeTest
{
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new MemcachedTestServer(port,max,scavenge);
    }

    @Test
    public void testLastAccessTime() throws Exception
    {
        super.testLastAccessTime();
    }
}
