package org.eclipse.jetty.nosql.key_value.session.serializable;

import org.eclipse.jetty.nosql.key_value.session.AbstractFactoryTest;
import org.eclipse.jetty.nosql.key_value.session.AbstractSessionFactory;

public class SerializableFactoryTest extends AbstractFactoryTest {
	@Override
	public AbstractSessionFactory createFactory() {
		return new NativeSerializationSessionFactory();
	}
}
