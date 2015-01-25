package org.eclipse.jetty.nosql.key_value.session.xstream;

import org.eclipse.jetty.nosql.key_value.session.AbstractTranscoderTest;
import org.eclipse.jetty.nosql.key_value.session.Serializer;

public class XStreamSerializerTest extends AbstractTranscoderTest {
	@Override
	public Serializer createTranscoder() {
		return new XStreamSerializer();
	}
}
