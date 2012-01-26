package org.rmas.pfact;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace="http://service.rmas.org/")
public interface IEventEndpoint {
	
	@WebResult( name="response", targetNamespace="http://service.rmas.org/" )
	public String pushEvent( @WebParam( name="event", targetNamespace="http://service.rmas.org/" ) String event );

	@WebResult( name="response", targetNamespace="http://service.rmas.org/" )
	public String getEvents( @WebParam( name="from", targetNamespace="http://service.rmas.org/" ) String when );
	
}
