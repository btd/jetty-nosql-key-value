package org.eclipse.jetty.nosql.key_value.memcached.spymemcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;

public class HerokuSpyMemcachedClientFactory extends BinarySpyMemcachedClientFactory {
	@Override
	public AbstractKeyValueStoreClient create(String serverString) {
		return new HerokuSpyMemcachedClient(serverString);
	}
}
