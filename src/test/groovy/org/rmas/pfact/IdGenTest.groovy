package org.rmas.pfact;



import static org.junit.Assert.*

import javax.sql.DataSource

import org.junit.Before
import org.junit.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

class IdGenTest {

	DataSource ds

	@Test
	void idGenerationWorks() {
		def data = new Data()
		data.setDs(ds)

		def newid = data.idlookup( ['id':12] )
		def sameid = data.idlookup( ['id':12] )
		
		assertEquals(newid, sameid)
	}

	@Before
	void dbSetup() {
		ds = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:/bootstrap-h2.sql").build();
	}
}
