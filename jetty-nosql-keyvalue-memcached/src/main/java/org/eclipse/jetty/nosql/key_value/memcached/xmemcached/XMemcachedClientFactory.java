package org.eclipse.jetty.nosql.key_value.memcached.xmemcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;
import org.eclipse.jetty.nosql.key_value.memcached.AbstractMemcachedClientFactory;

public class XMemcachedClientFactory extends AbstractMemcachedClientFactory {
	@Override
	public AbstractKeyValueStoreClient create(String serverString) {
		return new XMemcachedClient(serverString);
	}
}
