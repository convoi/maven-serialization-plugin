package com.blocksberg.vsc.manufacturing;

import com.blocksberg.vsc.testmodel.good.Bar;
import com.blocksberg.vsc.testmodel.good.Foo;
import com.blocksberg.vsc.testmodel.good.NoSetters;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class InstanceFactoryTest {

    @Test
    @Ignore("known issue, podam fails to fully initialize instances without setters")
    public void testNoSettersManufacturing() {
        final NoSetters noSetters = new InstanceFactory().createInstance(NoSetters.class);
        assertThat(noSetters.getDate(), not(is(nullValue())));
    }

    @Test
    public void testSimpleManufacturing() {
        final Bar bar = new InstanceFactory().createInstance(Bar.class);
        assertThat(bar.getDate(), not(is(nullValue())));
        assertThat(bar.getString(), not(is(nullValue())));
    }

    @Test
    @Ignore("known issue, podam fails to fully initialize instances without setters")
    public void testManufactureInstance() throws Exception {
        final Foo foo = new InstanceFactory().createInstance(Foo.class);
        assertThat(foo.getLongObject(), not(is(nullValue())));

    }

    @Test
    @Ignore("known issue, podam fails to fully initialize instances without setters")
    public void testManufactureInstanceStrict() throws Exception {
        final Foo foo = new InstanceFactory(true).createInstance(Foo.class);
        assertThat(foo.getLongObject(), not(is(nullValue())));
    }
}