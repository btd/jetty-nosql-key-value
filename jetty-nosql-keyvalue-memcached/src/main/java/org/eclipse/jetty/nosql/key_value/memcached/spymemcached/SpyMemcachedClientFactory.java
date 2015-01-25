package org.eclipse.jetty.nosql.key_value.memcached.spymemcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;
import org.eclipse.jetty.nosql.key_value.memcached.AbstractMemcachedClientFactory;

public class SpyMemcachedClientFactory extends AbstractMemcachedClientFactory {
	@Override
	public AbstractKeyValueStoreClient create(String serverString) {
		return new SpyMemcachedClient(serverString);
	}
}
