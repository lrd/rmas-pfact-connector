package org.rmas.pfact;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

import com.google.common.collect.ImmutableMap;

public class PfactRoutes extends RouteBuilder {

	@Override
	public void configure() throws Exception {

        from("timer:foo?fixedRate=true&period=10000").routeId("org.rmas.pfact")
        	.to("sql:select * from aplace").id("selectall") // the big query in here
        	.split(body())
        	.to("direct:splot");
        
        from("direct:splot") // make sure we dont see old ones
        	.setHeader("eid").groovy("request.body.id")
        	.idempotentConsumer(header("eid"),MemoryIdempotentRepository.memoryIdempotentRepository(200))
        	.to("direct:idempotented");

        from("direct:idempotented") // now enrich with extra bits
        	.enrich("direct:staff", new ThisAggregationStrategy("staff"))
        	.enrich("direct:funder", new ThisAggregationStrategy("funder"))
        	.marshal().json(JsonLibrary.Jackson) // to json
        	.convertBodyTo(String.class)
        	//	.log("\n\n------ after marshal ${body.class} ${body}")
        	.beanRef("rmasclient","pushEvent") // send
        	.to("log:org.rmas.pfact?level=DEBUG")
        	.log("pfact push ok ${body}");
        
        from("direct:staff")
        	.to("sql:select staff").id("selectstaff");
        
        from("direct:funder")
    		.to("sql:select funder").id("selectfunder");

	}
	
	public class ThisAggregationStrategy implements AggregationStrategy {
		
		private String what;

		public ThisAggregationStrategy(String what) {
			this.what = what;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
			Map in = oldExchange.getIn().getBody(Map.class);
			List l = newExchange.getIn().getBody(List.class);
			Map merge = ImmutableMap.builder().putAll(in).put(what, l).build();
			oldExchange.getOut().setBody(merge);
			return oldExchange;
		}
		
	}

}
