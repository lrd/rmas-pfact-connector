package org.rmas.pfact;

import org.apache.camel.builder.RouteBuilder;

public class PfactRoutes extends RouteBuilder {

	@Override
	public void configure() throws Exception {

        from("timer:foo?fixedRate=true&period=10000")
        	.beanRef("rmasclient","pushEvent")
        	.log("pfact push ok ${body}");

	}

}
