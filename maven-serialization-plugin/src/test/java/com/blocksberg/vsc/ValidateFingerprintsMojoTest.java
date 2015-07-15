package com.blocksberg.vsc;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Justin Heesemann
 */
@Ignore
public class ValidateFingerprintsMojoTest {
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
        final CreateFingerprintsMojo mojo = (CreateFingerprintsMojo) rule.lookupMojo("validate",
                "src/test/resources/testproject/pom.xml");
        assertNotNull("mojo should be found", mojo);
        mojo.execute();
    }
}