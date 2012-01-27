package org.rmas.pfact

import groovy.sql.Sql

import java.sql.ResultSet

import javax.sql.DataSource

class Data {

	DataSource ds
	DataSource pfact

	def idlookup(s) {
		Sql db = new Sql(ds)

		def res = db.firstRow("select rmasid as id from idlookup where pfactid = ?", s.id)

		if( res )
			s + ['rmasid':res['id']]
		else {
			def id = "urn:kent:pfact:" + UUID.randomUUID()
			db.execute("insert into idlookup (pfactid, rmasid) values (?,?)", s.id, id)
			s + ['rmasid':id]
		}
	}

	def selectall(s) {
		def db = new Sql(ds)

		[
			['id': new Random().nextInt(), 'title' : 'balls 1'],
			['id': new Random().nextInt(), 'title' : 'balls 2']
		]
	}

	def selectstaff(s) {
		def db = new Sql(ds)

		s + ['staff' : [ 'id': 'staff for ' + s.title ] ]
	}


	def selectfunder(s) {
		def db = new Sql(ds)

		s + ['funder' : [ 'id': 'funder for ' + s.title ] ]
	}
}
