package org.eclipse.jetty.nosql.key_value.session.serializable;

import org.eclipse.jetty.nosql.key_value.session.AbstractTranscoderTest;
import org.eclipse.jetty.nosql.key_value.session.Serializer;

public class NativeSerializerTest extends AbstractTranscoderTest {
	@Override
	public Serializer createTranscoder() {
		return new NativeSerializer();
	}
}
