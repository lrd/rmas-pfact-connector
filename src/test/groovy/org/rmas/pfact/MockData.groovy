package org.rmas.pfact;


class MockData {
	def idlookup(s) {
		s + ["rmasid": "something"]
	}

	def selectall(s) {
		def id1 = new Random().nextInt()
		def id2 = new Random().nextInt()

		[
			['id': id1, 'title': 'title ' + id1],
			['id': id1, 'title': 'title ' + id1],
			['id': id2, 'title': 'another title ' + id2]
		]
	}

	def selectstaff(s) {
		s + ['staff' : [ 'id': 'staff for ' + s.title ] ]
	}


	def selectfunder(s) {
		s + ['funder' : [ 'id': 'funder for ' + s.title ] ]
	}
}
