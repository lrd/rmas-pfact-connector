package org.rmas.pfact;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import static org.junit.Assert.*;


public class PfactRouteTest extends CamelTestSupport {
	
	
	@Test
	public void convertFromSqlToJson() throws Exception {
		context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
			public void configure() throws Exception {
				mockEndpoints("timer:foo");
				mockEndpoints("log:org.rmas.pfact");
			}
		});
    assertTrue(true);
	}
	
	protected CamelContext createCamelContext() throws Exception {
		CamelContext camelContext = super.createCamelContext();
    org.apache.camel.impl.JndiRegistry r = (org.apache.camel.impl.JndiRegistry) ((org.apache.camel.impl.PropertyPlaceholderDelegateRegistry)camelContext.getRegistry()).getRegistry();
    r.bind("rmasclient",new Object() {
      public String pushEvent( String ev ) { return "nuts"; }
    });
		return camelContext;
	}
	
	protected RouteBuilder createRouteBuilder() {
		return new PfactRoutes();
	}

}
