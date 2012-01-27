package org.rmas.pfact;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class PfactRouteTest extends CamelTestSupport {

	@Test
	public void convertFromSqlToJson() throws Exception {

		final IEventEndpoint ep = mock(IEventEndpoint.class);
		when(ep.pushEvent(anyString())).thenReturn("ok");

		context.getRouteDefinitions().get(0)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						FromDefinition f = getOriginalRoute().getInputs()
								.get(0);
						f.setUri("direct:start");
					}
				});

		context.getRouteDefinitions().get(2)
				.adviceWith(context, new AdviceWithRouteBuilder() {
					public void configure() throws Exception {
						mockEndpoints("log:org.rmas.pfact.*");
						weaveById("rmasclient").replace().bean(ep, "pushEvent");
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
		answer.bind("pfactdata", new MockData());
		return answer;
	}

	protected CamelContext createCamelContext() throws Exception {
		CamelContext camelContext = super.createCamelContext();
		camelContext.setTracing(true);
		return camelContext;
	}

	protected RouteBuilder createRouteBuilder() {
		DataSource ds = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:/bootstrap-h2.sql").build();
		JdbcMessageIdRepository repo = new JdbcMessageIdRepository(ds,
				"idempotent-repo");
		return new PfactRoutes(repo);
	}

}
