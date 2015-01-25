package org.eclipse.jetty.nosql.key_value.memcached.spymemcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;

public class BinarySpyMemcachedClientFactory extends SpyMemcachedClientFactory {
	@Override
	public AbstractKeyValueStoreClient create(String serverString) {
		return new BinarySpyMemcachedClient(serverString);
	}
}
