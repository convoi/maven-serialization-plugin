package com.blocksberg.vsc;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Justin Heesemann
 */
public class SerializeInstancesMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Test
    public void test() throws Exception {
        final SerializeInstancesMojo mojo = (SerializeInstancesMojo) rule.lookupMojo("serialize",
                "src/test/resources/testproject/pom.xml");
        assertNotNull("mojo should be found", mojo);
        mojo.execute();
    }
}