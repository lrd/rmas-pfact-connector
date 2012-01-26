package org.rmas.pfact;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.naming.Context;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import static org.mockito.Matchers.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PfactRouteTest extends CamelTestSupport {

	@Test
	public void convertFromSqlToJson() throws Exception {
		final IEventEndpoint ep = mock(IEventEndpoint.class);
		when(ep.pushEvent(anyString())).thenReturn("ok");

		final ImmutableList<ImmutableMap<String, String>> allresult = ImmutableList
				.of(ImmutableMap.of("id", "1", "title", "1 title"),
						ImmutableMap.of("id", "1", "title", "1 title"),
						ImmutableMap.of("id", "2", "title", "2 title"));

		final ImmutableList<ImmutableMap<String, String>> staffresult = ImmutableList
				.of(ImmutableMap.of("id", "1", "title", "some staff"),
						ImmutableMap.of("id", "2", "title", "more staff"));

		final ImmutableList<ImmutableMap<String, String>> funderresult = ImmutableList
				.of(ImmutableMap.of("id", "1", "title", "some funder"),
						ImmutableMap.of("id", "2", "title", "more funder"));
		
		context.getRouteDefinitions().get(0)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						FromDefinition f = getOriginalRoute().getInputs()
								.get(0);
						f.setUri("direct:start");
						weaveById("selectall").replace().setBody()
								.constant(allresult);
					}
				});

		context.getRouteDefinitions().get(2)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						mockEndpoints("log:org.rmas.pfact.*");
						weaveByToString(".*rmasclient.*").replace().bean(ep,
								"pushEvent");
					}
				});

		context.getRouteDefinitions().get(3)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						weaveById("selectstaff").replace().setBody()
								.constant(staffresult);
					}
				});

		context.getRouteDefinitions().get(4)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						weaveById("selectfunder").replace().setBody()
								.constant(funderresult);
					}
				});

		getMockEndpoint("mock:log:org.rmas.pfact").expectedMessageCount(2);

		template.sendBody("direct:start", "something in here");

		assertMockEndpointsSatisfied();
	}

	@Override
	protected Context createJndiContext() throws Exception {
		JndiContext answer = new JndiContext();
		IEventEndpoint ep = mock(IEventEndpoint.class);
		answer.bind("rmasclient", ep);
		return answer;
	}

	protected CamelContext createCamelContext() throws Exception {
		CamelContext camelContext = super.createCamelContext();
		camelContext.addComponent("sql", new MockComponent());
		camelContext.setTracing(true);
		return camelContext;
	}

	protected RouteBuilder createRouteBuilder() {
		return new PfactRoutes();
	}

}
