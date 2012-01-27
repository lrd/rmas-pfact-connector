package org.rmas.pfact;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;

public class PfactRoutes extends RouteBuilder {

	JdbcMessageIdRepository repo;
	
	public PfactRoutes(JdbcMessageIdRepository repo) {
		this.repo = repo;
	}

	@Override
	public void configure() throws Exception {

        from("timer:foo?fixedRate=true&period=10000").routeId("org.rmas.pfact")
        	.beanRef("pfactdata","selectall").id("selectall") // the big query in here
        	.split(body())
        	.to("direct:splot");

        from("direct:splot") // make sure we dont see old ones
        	.setHeader("eid").groovy("request.body.id")
        	.idempotentConsumer(header("eid"),repo)
        	.to("direct:idempotented");

        from("direct:idempotented") // now enrich with extra bits
        	.beanRef("pfactdata","selectstaff")
        	.beanRef("pfactdata","selectfunder")
        	.beanRef("pfactdata","idlookup")
        	.transform().groovy("request.body + ['rmasmessageid' : 'urn:kent:pfact:' + UUID.randomUUID() ]")
        	.marshal().json(JsonLibrary.Jackson) // to json
        	.convertBodyTo(String.class)
        	.beanRef("rmasclient","pushEvent").id("rmasclient") // send
        	.to("log:org.rmas.pfact?level=DEBUG")
        	.log("pfact push ${body}");

	}

}
