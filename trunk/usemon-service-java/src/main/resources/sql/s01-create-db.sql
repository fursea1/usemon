CREATE TABLE location (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  location VARCHAR(255) NOT NULL,
  platform VARCHAR(255) NOT NULL,
  cluster VARCHAR(255) NOT NULL,
  server VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);


insert into location (id, location, platform, cluster, server) 
values(1,'unknown','unknown','unknown','unknown');


CREATE TABLE instance (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  instance VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

insert into instance values(1,'unknown');


CREATE TABLE method (
  id BIGINT NOT NULL AUTO_INCREMENT,
  method VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

insert into method values(1,'unknown');


CREATE TABLE principal (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  principal VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

insert into principal values(1,'unknown');


CREATE TABLE package (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  package VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

insert into package 
values(1,'unknown');


CREATE TABLE d_date (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  year_v CHAR(4) NOT NULL,
  month_v CHAR(2) NOT NULL,
  day_v CHAR(2) NOT NULL,
  day_of_week_v CHAR(3) NULL,
  date_v DATE NULL,
  PRIMARY KEY(id),
  INDEX d_date_value(date_v)
);

insert into d_date(id, year_v, month_v, day_v, day_of_week_v,date_v)
values(1,'na','na','na','na','2000-09-11');


CREATE TABLE class (
  id BIGINT NOT NULL AUTO_INCREMENT,
  class VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

insert into class values(1,'unknown');


CREATE TABLE channel (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  channel VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

insert into channel values(1,'unknown');


CREATE TABLE d_time (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  hh CHAR(2) NOT NULL,
  mm CHAR(2) NOT NULL,
  ss CHAR(2) NOT NULL,
  time_v TIME NULL,
  PRIMARY KEY(id),
  INDEX d_time_value(time_v)
);

insert into d_time (id, hh, mm, ss) values(1,'na','na','na');


CREATE TABLE src_package (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  src_package VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists src_package;
drop table if exists src_package;
create view src_package(id, src_package) as select id, package from package;


CREATE TABLE src_method (
  id BIGINT NOT NULL AUTO_INCREMENT,
  src_method VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists src_method;
drop table if exists src_method;
create view src_method (id, src_method) as select id, method from method;


CREATE TABLE target_instance (
  id INTEGER UNSIGNED NOT NULL,
  target_instance VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists target_instance;
drop table if exists target_instance;
create view target_instance(id, target_instance) as select id, instance from instance;


CREATE TABLE target_class (
  id BIGINT NOT NULL AUTO_INCREMENT,
  target_class VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists target_class;
drop table if exists target_class;
create view target_class(id, target_class) as select id, class from class;


CREATE TABLE src_instance (
  id INTEGER UNSIGNED NOT NULL,
  src_instance VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop table if exists src_instance;
drop view if exists src_instance;
create view src_instance (id, src_instance) as select id, instance from instance;


CREATE TABLE target_package (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  target_package VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists target_package ;
drop table if exists target_package ;
create view target_package (id, target_package) as select id, package from package;


CREATE TABLE target_method (
  id BIGINT NOT NULL AUTO_INCREMENT,
  target_method VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists target_method;
drop table if exists target_method;
create view target_method(id, target_method) as select id, method from method;


CREATE TABLE src_class (
  id BIGINT NOT NULL AUTO_INCREMENT,
  src_class VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

drop view if exists src_class;
drop table if exists src_class;
create view src_class(id, src_class) as select id, class from class;


CREATE TABLE heap_fact (
  id BIGINT NOT NULL AUTO_INCREMENT,
  d_date_id INTEGER UNSIGNED NOT NULL,
  d_time_id INTEGER UNSIGNED NOT NULL,
  location_id INTEGER UNSIGNED NOT NULL,
  free INTEGER UNSIGNED NOT NULL,
  total INTEGER UNSIGNED NOT NULL,
  max_mem INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX heap_fact_FKIndex1(location_id),
  INDEX heap_fact_FKIndex3(d_time_id),
  INDEX heap_fact_FKIndex5(d_date_id),
  FOREIGN KEY(location_id)
    REFERENCES location(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_time_id)
    REFERENCES d_time(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_date_id)
    REFERENCES d_date(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE method_measurement_fact (
  id BIGINT NOT NULL AUTO_INCREMENT,
  d_date_id INTEGER UNSIGNED NOT NULL,
  d_time_id INTEGER UNSIGNED NOT NULL,
  method_id BIGINT NOT NULL,
  class_id BIGINT NOT NULL,
  package_id INTEGER UNSIGNED NOT NULL,
  channel_id INTEGER UNSIGNED NOT NULL,
  principal_id INTEGER UNSIGNED NOT NULL,
  location_id INTEGER UNSIGNED NOT NULL,
  invocation_count INTEGER UNSIGNED NOT NULL,
  max_response_time INTEGER UNSIGNED NOT NULL,
  avg_response_time INTEGER UNSIGNED NOT NULL,
  checked_exceptions INTEGER UNSIGNED NOT NULL,
  unchecked_exceptions INTEGER UNSIGNED NOT NULL,
  period_length BIGINT NULL,
  PRIMARY KEY(id),
  INDEX fact_FKIndex2(location_id),
  INDEX fact_FKIndex3(principal_id),
  INDEX fact_FKIndex5(channel_id),
  INDEX fact_FKIndex7(package_id),
  INDEX fact_FKIndex8(class_id),
  INDEX fact_FKIndex9(method_id),
  INDEX method_measurement_fact_FKIndex7(d_time_id),
  INDEX method_measurement_fact_FKIndex8(d_date_id),
  FOREIGN KEY(location_id)
    REFERENCES location(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(principal_id)
    REFERENCES principal(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(channel_id)
    REFERENCES channel(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(package_id)
    REFERENCES package(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(class_id)
    REFERENCES class(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(method_id)
    REFERENCES method(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_time_id)
    REFERENCES d_time(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_date_id)
    REFERENCES d_date(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

CREATE TABLE invocation_fact (
  id BIGINT NOT NULL AUTO_INCREMENT,
  d_time_id INTEGER UNSIGNED NOT NULL,
  d_date_id INTEGER UNSIGNED NOT NULL,
  target_package_id INTEGER UNSIGNED NOT NULL,
  src_package_id INTEGER UNSIGNED NOT NULL,
  target_class_id BIGINT NOT NULL,
  src_class_id BIGINT NOT NULL,
  target_method_id BIGINT NOT NULL,
  src_method_id BIGINT NOT NULL,
  location_id INTEGER UNSIGNED NOT NULL,
  src_instance_id INTEGER UNSIGNED NOT NULL,
  target_instance_id INTEGER UNSIGNED NOT NULL,
  invocation_count BIGINT NULL,
  period_length BIGINT NULL,
  PRIMARY KEY(id),
  INDEX invocation_fact_FKIndex3(location_id),
  INDEX invocation_fact_FKIndex2(d_date_id),
  INDEX invocation_fact_FKIndex4(d_time_id),
  FOREIGN KEY(location_id)
    REFERENCES location(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_date_id)
    REFERENCES d_date(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  FOREIGN KEY(d_time_id)
    REFERENCES d_time(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);

/** Adds index on FK referencing view src_instance */
alter table invocation_fact add index(src_instance_id);
/** Adds index on FK referencing view target_instance */
alter table invocation_fact add index(target_instance_id);
/** Adds index FK referencing view src_class */
alter table invocation_fact add index(src_class_id);
/** Adds index FK referencing view target_class */
alter table invocation_fact add index(target_class_id);

/** Adds index FK referencing vieew src_package */
alter table invocation_fact add index(src_package_id);
alter table invocation_fact add index(target_package_id);

/** Adds cascading delete on target_package_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_target_package`
  FOREIGN KEY `FK_invocation_target_package` (`target_package_id`)
    REFERENCES `package` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on src_package_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_src_package`
  FOREIGN KEY `FK_invocation_src_package` (`src_package_id`)
    REFERENCES `package` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on src_class_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_src_class`
  FOREIGN KEY `FK_invocation_src_class` (`src_class_id`)
    REFERENCES `class` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;


/** Adds cascading delete on target_class_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_target_class`
  FOREIGN KEY `FK_invocation_target_class` (`target_class_id`)
    REFERENCES `class` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on src_method_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_src_method`
  FOREIGN KEY `FK_invocation_src_method` (`src_method_id`)
    REFERENCES `method` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on target_method_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_target_method`
  FOREIGN KEY `FK_invocation_target_method` (`target_method_id`)
    REFERENCES `method` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on src_instance_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_src_instance`
  FOREIGN KEY `FK_invocation_src_instance` (`src_instance_id`)
    REFERENCES `instance` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

/** Adds cascading delete on target_instance_id column */
ALTER TABLE `usemon`.`invocation_fact`
ADD CONSTRAINT `FK_invocation_target_instance`
  FOREIGN KEY `FK_invocation_target_instance` (`target_instance_id`)
    REFERENCES `instance` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;



