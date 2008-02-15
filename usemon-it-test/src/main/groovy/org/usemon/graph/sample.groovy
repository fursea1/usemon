package org.usemon.graph;

import groovy.sql.Sql

dbHandle = null

def getDb() {
	if (dbHandle) return dbHandle
	def source = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource()
	source.databaseName='usemon'
	source.user='usemonuser'
	source.password='usemonpass'
	dbHandle = new Sql(source)
	return dbHandle
}

db.eachRow('select class as clname from class') { clazz -> 
	println """$clazz.clname"""		
}
