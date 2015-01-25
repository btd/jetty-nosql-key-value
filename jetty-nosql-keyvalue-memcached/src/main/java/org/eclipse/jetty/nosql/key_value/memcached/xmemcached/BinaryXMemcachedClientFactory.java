package org.eclipse.jetty.nosql.key_value.memcached.xmemcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;

/**
 * Created by IntelliJ IDEA.
 * User: yyuu
 * Date: 12/03/11
 * Time: 0:16
 * To change this template use File | Settings | File Templates.
 */
public class BinaryXMemcachedClientFactory extends XMemcachedClientFactory {
	@Override
	public AbstractKeyValueStoreClient create(String serverString) {
		return new BinaryXMemcachedClient(serverString);
	}
}
