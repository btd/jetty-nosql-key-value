package org.eclipse.jetty.nosql.key_value.memcached;

import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;

public abstract class AbstractMemcachedClientFactory {
	public abstract AbstractKeyValueStoreClient create(String serverString);
}