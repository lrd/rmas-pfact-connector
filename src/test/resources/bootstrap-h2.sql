
drop table if exists CAMEL_MESSAGEPROCESSED;
CREATE TABLE CAMEL_MESSAGEPROCESSED (
  processorName VARCHAR(255),
  messageId VARCHAR(100),
  createdAt TIMESTAMP
);

drop table if exists idlookup;
create table idlookup (
	pfactid int,
	rmasid varchar(255)
);

