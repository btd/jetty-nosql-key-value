package org.eclipse.jetty.nosql.key_value.session.xstream;

import org.eclipse.jetty.nosql.key_value.session.AbstractFactoryTest;
import org.eclipse.jetty.nosql.key_value.session.AbstractSessionFactory;

public class XStreamFactoryTest extends AbstractFactoryTest {
	@Override
	public AbstractSessionFactory createFactory() {
		return new XStreamSessionFactory();
	}
}
