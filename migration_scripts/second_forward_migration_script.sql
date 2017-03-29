SET FOREIGN_KEY_CHECKS=0;
# drop tables for Performance Module
SELECT 'STEP 1 : drop PollFilters table' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists PollFilters;
# alter table for PolledData table
SELECT 'STEP 2 : alter PolledData table' AS 'MIGRATION PROCESS STATUS ... ';
alter table PolledData drop key NAME;
alter table PolledData modify column ID bigint(20) NOT NULL PRIMARY KEY first;
alter table PolledData add UNIQUE KEY(`NAME`,`AGENT`,`OID`);
alter table PolledData drop column OWNERNAME;
alter table PolledData add column DISCRIMINATOR varchar(30) NOT NULL after `ID`;
update PolledData set DISCRIMINATOR = (select VALUESTRING from DBPOLL where ID=KEYSTRING);
update PolledData set DISCRIMINATOR = 'PolledData' where DISCRIMINATOR = '';
drop table if exists DBPOLL;
alter table PolledData change column ACTIVE ACTIVE_STR varchar(10) default NULL;
alter table PolledData add column ACTIVE bit(1) default NULL AFTER PERIOD;
update PolledData set ACTIVE = ACTIVE_STR like 'true';
alter table PolledData drop column ACTIVE_STR;
alter table PolledData change column LOGDIRECTLY LOGDIRECTLY_STR varchar(10) default NULL;
alter table PolledData add column LOGDIRECTLY bit(1) default NULL AFTER ACTIVE;
update PolledData set LOGDIRECTLY = LOGDIRECTLY_STR like 'true';
alter table PolledData drop column LOGDIRECTLY_STR;
alter table PolledData change column SSAVE SSAVE_STR varchar(10) default NULL;
alter table PolledData add column SAVEDATA bit(1) default NULL AFTER LOGFILE;
update PolledData set SAVEDATA = SSAVE_STR like 'true';
alter table PolledData drop column SSAVE_STR;
alter table PolledData change column THRESHOLD THRESHOLD_STR varchar(10) default NULL;
alter table PolledData add column THRESHOLD bit(1) default NULL AFTER SAVEDATA;
update PolledData set THRESHOLD = THRESHOLD_STR like 'true';
alter table PolledData drop column THRESHOLD_STR;
alter table PolledData change column ISMULTIPLEPOLLEDDATA ISMULTIPLEPOLLEDDATA_STR varchar(10) default NULL;
alter table PolledData add column ISMULTIPLEPOLLEDDATA bit(1) default NULL AFTER THRESHOLD;
update PolledData set ISMULTIPLEPOLLEDDATA = ISMULTIPLEPOLLEDDATA_STR like 'true';
alter table PolledData drop column ISMULTIPLEPOLLEDDATA_STR;
alter table PolledData change column SAVEABSOLUTES SAVEABSOLUTES_STR varchar(10) default NULL;
alter table PolledData add column SAVEABSOLUTES bit(1) default NULL AFTER NUMERICTYPE;
update PolledData set SAVEABSOLUTES = SAVEABSOLUTES_STR like 'true';
alter table PolledData drop column SAVEABSOLUTES_STR;
alter table PolledData change column TIMEAVG TIMEAVG_STR varchar(10) default NULL;
alter table PolledData add column TIMEAVG bit(1) default NULL AFTER SAVEABSOLUTES;
update PolledData set TIMEAVG = TIMEAVG_STR like 'true';
alter table PolledData drop column TIMEAVG_STR;
alter table PolledData change column SAVEONTHRESHOLD SAVEONTHRESHOLD_STR varchar(10) default NULL;
alter table PolledData add column SAVEONTHRESHOLD bit(1) default NULL AFTER CURRENTSAVECOUNT;
update PolledData set SAVEONTHRESHOLD = SAVEONTHRESHOLD_STR like 'true';
alter table PolledData drop column SAVEONTHRESHOLD_STR;
#drop index for PolledData
drop index PolledData0_ndx on PolledData;
drop index PolledData1_ndx on PolledData;
drop index PolledData2_ndx on PolledData;
drop index PolledData3_ndx on PolledData;
drop index PolledData4_ndx on PolledData;
drop index PolledData5_ndx on PolledData;
drop index PolledData6_ndx on PolledData;
create index `PARENTOBJ_ndx` on PolledData(PARENTOBJ);
# alter table for POLLUSERPROPS
SELECT 'STEP 3 : alter POLLUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table POLLUSERPROPS add column `ID` bigint(20) NOT NULL first;
update POLLUSERPROPS set ID = (select ID from PolledData where POLLUSERPROPS.NAME= PolledData.NAME and POLLUSERPROPS.AGENT = PolledData.AGENT and POLLUSERPROPS.OID=PolledData.OID);
alter table POLLUSERPROPS drop column OWNERNAME;
alter table POLLUSERPROPS drop column NAME;
alter table POLLUSERPROPS drop column AGENT;
alter table POLLUSERPROPS drop column OID;
alter table POLLUSERPROPS add constraint `FK5DBA4B86D7D7502` FOREIGN KEY (`ID`) REFERENCES `PolledData` (`ID`) ON DELETE CASCADE;
alter table POLLUSERPROPS add primary key (ID,PROPNAME);
# create index for POLLUSERPROPS table
create index `FK5DBA4B86D7D7502` on POLLUSERPROPS(ID);
# alter table for Alert
SELECT 'STEP 4 : alter Alert table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Alert drop column OWNERNAME;
alter table Alert drop column MAPNAME;
alter table Alert drop column STAGE;
alter table Alert drop column PRIORITY;
alter table Alert modify column ENTITY varchar(100) NOT NULL first;
alter table Alert add column DISCRIMINATOR varchar(30) NOT NULL after `ENTITY`;
update Alert set DISCRIMINATOR = (select VALUESTRING from DBALERT where ENTITY=KEYSTRING);
update Alert set DISCRIMINATOR = 'Alert' where DISCRIMINATOR = '';
# drop index for Alert table
alter table Alert drop index Alert0_ndx;
# drop table if exists DBALERT
SELECT 'STEP 5 : drop DBALERT table' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists DBALERT;
# alter table for EMHAlert
SELECT 'STEP 6 : alter EMHAlert table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EMHAlert drop column OWNERNAME;
alter table EMHAlert add constraint `FK6794709C196074E5` FOREIGN KEY (`ENTITY`) REFERENCES `Alert` (`ENTITY`) ON DELETE CASCADE;
# index creation for EMHAlert
#create index `FK6794709C196074E5` on EMHAlert(ENTITY);
# alter table for ALERTUSERPROPS
SELECT 'STEP 7 : alter ALERTUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ALERTUSERPROPS drop column OWNERNAME;
alter table ALERTUSERPROPS add column `ENTITY` varchar(100) NOT NULL first;
update ALERTUSERPROPS set ENTITY = NAME;
alter table ALERTUSERPROPS add constraint `FKCFB5A6A93E26DDB` FOREIGN KEY (`ENTITY`) REFERENCES `Alert` (`ENTITY`) ON DELETE CASCADE;
alter table ALERTUSERPROPS drop column NAME;
alter table ALERTUSERPROPS add primary key (ENTITY,PROPNAME);
# create index for ALERTUSERPROPS table
create index `FKCFB5A6A93E26DDB` on ALERTUSERPROPS(ENTITY);
# alter table for ANNOTATION
SELECT 'STEP 8 : alter ANNOTATION table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ANNOTATION drop column OWNERNAME;
alter table ANNOTATION add column AAID bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY first;
alter table ANNOTATION add column DISCRIMINATOR varchar(20) NOT NULL after `AAID`;
update ANNOTATION set DISCRIMINATOR = 'AlertHistory' where who = 'NULL';
update ANNOTATION set DISCRIMINATOR = 'AlertAnnotation' where who != 'NULL';
alter table ANNOTATION modify column AAID bigint(20) NOT NULL first;
# drop index for ANNOTATION table
alter table ANNOTATION drop index ANNOTATION0_ndx;
# drop tables for Map module
SELECT 'STEP 9 : drop table DBMAP of Map module' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists DBMAP;
# alter table for MapDB
SELECT 'STEP 10 : alter MapDB table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapDB drop column OWNERNAME;
alter table MapDB change column AUTOPLACEMENT AUTOPLACEMENT_STR varchar(10) default NULL;
alter table MapDB add column AUTOPLACEMENT bit(1) default NULL AFTER TREEICONFILENAME;
update MapDB set AUTOPLACEMENT = AUTOPLACEMENT_STR like 'true';
alter table MapDB drop column AUTOPLACEMENT_STR;
alter table MapDB change column ANCHORED ANCHORED_STR varchar(10) default NULL;
alter table MapDB add column ANCHORED bit(1) default NULL AFTER MAPLISTENER;
update MapDB set ANCHORED = ANCHORED_STR like 'true';
alter table MapDB drop column ANCHORED_STR;
alter table MapDB add `TYPE` varchar(100) default NULL after ANCHORED;
update MapDB set TYPE = 'CustomMap' where MapDB.name in (select VALUESTRING from CUSTOMMAPS);
update MapDB set TYPE = 'DefaultMap' where MapDB.name in (select VALUESTRING from DEFAULTMAPS);
drop table if exists CUSTOMMAPS;
drop table if exists DEFAULTMAPS;
alter table MapDB add `TABPANELS` varchar(100) default NULL after TYPE;
# drop index for MapDB
drop index MapDB0_ndx on MapDB;
# alter table MapSymbol
SELECT 'STEP 11 : alter MapSymbol table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapSymbol drop column OWNERNAME;
alter table MapSymbol add column DISCRIMINATOR varchar(30) NOT NULL after `MAPNAME`;
alter table MapSymbol add column `MAPWIDTH` int(11) default NULL after `ANCHORED`;
alter table MapSymbol add column `MAPHEIGHT` int(11) default NULL after `MAPWIDTH`;
alter table MapSymbol modify column OBJNAME varchar(100) default NULL after DISCRIMINATOR;
update MapSymbol set DISCRIMINATOR = 'MapSymbol', MAPWIDTH = 0, MAPHEIGHT = 0;
alter table MapSymbol change column ANCHORED ANCHORED_STR varchar(10) default NULL;
alter table MapSymbol add column ANCHORED bit(1) default NULL AFTER PARENTNAME;
update MapSymbol set ANCHORED = ANCHORED_STR like 'true';
alter table MapSymbol drop column ANCHORED_STR;
alter table MapSymbol change column WIDTH WIDTH_STR varchar(25) default NULL;
alter table MapSymbol add column WIDTH int(11) default NULL AFTER PARENTNAME;
update MapSymbol set WIDTH = WIDTH_STR;
alter table MapSymbol drop column WIDTH_STR;
alter table MapSymbol change column HEIGHT HEIGHT_STR varchar(25) default NULL;
alter table MapSymbol add column HEIGHT int(11) default NULL AFTER WIDTH;
update MapSymbol set HEIGHT = HEIGHT_STR;
alter table MapSymbol drop column HEIGHT_STR;
alter table MapSymbol change column X X_STR varchar(25) default NULL;
alter table MapSymbol add column X int(11) default NULL AFTER HEIGHT;
update MapSymbol set X = X_STR;
alter table MapSymbol drop column X_STR;
alter table MapSymbol change column Y Y_STR varchar(25) default NULL;
alter table MapSymbol add column Y int(11) default NULL AFTER X;
update MapSymbol set Y = Y_STR;
alter table MapSymbol drop column Y_STR;
# drop index for MapSymbol
drop index MapSymbol0_ndx on MapSymbol;
drop index MapSymbol1_ndx on MapSymbol;
drop index MapSymbol3_ndx on MapSymbol;
# alter table for MapContainer
SELECT 'STEP 12 : alter MapContainer table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapContainer drop column OWNERNAME;
insert into MapSymbol(NAME,OBJNAME,MAPNAME,LABEL,ICONNAME,MENUNAME,WIDTH,HEIGHT,X,Y,WEBNMS,GROUPNAME,ANCHORED,OBJTYPE,PARENTNAME,DISCRIMINATOR,MAPWIDTH,MAPHEIGHT) select NAME,OBJNAME,MAPNAME,LABEL,ICONNAME,MENUNAME,WIDTH,HEIGHT,X,Y,WEBNMS,GROUPNAME,ANCHORED,OBJTYPE,PARENTNAME,'MapContainer',0,0 from MapContainer;
alter table MapContainer drop column OBJNAME;
alter table MapContainer drop column LABEL;
alter table MapContainer drop column ICONNAME;
alter table MapContainer drop column MENUNAME;
alter table MapContainer drop column WIDTH;
alter table MapContainer drop column HEIGHT;
alter table MapContainer drop column X;
alter table MapContainer drop column Y;
alter table MapContainer drop column WEBNMS;
alter table MapContainer drop column GROUPNAME;
alter table MapContainer drop column ANCHORED;
alter table MapContainer drop column OBJTYPE;
alter table MapContainer drop column PARENTNAME;
alter table MapContainer add CONSTRAINT `FKFA2B62A5334AE2DB` FOREIGN KEY (`NAME`, `MAPNAME`) REFERENCES `MapSymbol` (`NAME`, `MAPNAME`) ON DELETE CASCADE;
alter table MapContainer change column CONTAINMENT CONTAINMENT_STR varchar(10) default NULL;
alter table MapContainer add column CONTAINMENT bit(1) default NULL AFTER TOPOLOGY;
update MapContainer set CONTAINMENT = CONTAINMENT_STR like 'true';
alter table MapContainer drop column CONTAINMENT_STR;
# drop index for MapContainer table
drop index MapContainer0_ndx on MapContainer;
drop index MapContainer1_ndx on MapContainer;
#create index `FKFA2B62A5334AE2DB` on MapContainer(NAME,MAPNAME);
# alter table for MapLink
SELECT 'STEP 13 : alter MapLink table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapLink drop column OWNERNAME;
insert into MapSymbol(NAME,MAPNAME,LABEL,MENUNAME,X,Y,OBJNAME,WEBNMS,GROUPNAME,DISCRIMINATOR,WIDTH,HEIGHT,OBJTYPE,MAPWIDTH,MAPHEIGHT,ANCHORED) select NAME,MAPNAME,LABEL,MENUNAME,X,Y,OBJNAME,WEBNMS,GROUPNAME, 'MapLink',-1,-1,-1,0,0,0 from MapLink;
alter table MapLink drop column LABEL;
alter table MapLink drop column MENUNAME;
alter table MapLink drop column X;
alter table MapLink drop column Y;
alter table MapLink drop column OBJNAME;
alter table MapLink drop column WEBNMS;
alter table MapLink drop column GROUPNAME;
alter table MapLink change column NX NX_STR varchar(25) default NULL;
alter table MapLink modify column MAPNAME varchar(100) NOT NULL after NAME;
alter table MapLink add column NX int(11) default NULL AFTER LINKTYPE;
update MapLink set NX = NX_STR;
alter table MapLink drop column NX_STR;
alter table MapLink change column NY NY_STR varchar(25) default NULL;
alter table MapLink add column NY int(11) default NULL AFTER NX;
update MapLink set NY = NY_STR;
alter table MapLink drop column NY_STR;
alter table MapLink change column STATUS STATUS_STR varchar(25) default NULL;
alter table MapLink add column STATUS int(11) default NULL AFTER NY;
update MapLink set STATUS = STATUS_STR;
alter table MapLink drop column STATUS_STR;
alter table MapLink add CONSTRAINT `FK951453561311A184` FOREIGN KEY (`NAME`, `MAPNAME`) REFERENCES `MapSymbol` (`NAME`, `MAPNAME`) ON DELETE CASCADE;
# create index for table MapLink
#create index `FK951453561311A184` on MapLink(NAME,MAPNAME);
drop index MapLink0_ndx on MapLink;
drop index MapLink1_ndx on MapLink;
# alter table for MapGroup
SELECT 'STEP 14 : alter MapGroup table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapGroup drop column OWNERNAME;
insert into MapSymbol(NAME,OBJNAME,MAPNAME,LABEL,ICONNAME,MENUNAME,WIDTH,HEIGHT,X,Y,WEBNMS,GROUPNAME,OBJTYPE,ANCHORED,DISCRIMINATOR,MAPWIDTH,MAPHEIGHT) select NAME,OBJNAME,MAPNAME,LABEL,ICONNAME,MENUNAME,WIDTH,HEIGHT,X,Y,WEBNMS,GROUPNAME,OBJTYPE,ANCHORED,'MapGroup',-1,-1 from MapGroup;
alter table MapGroup drop column OBJNAME;
alter table MapGroup drop column LABEL;
alter table MapGroup drop column ICONNAME;
alter table MapGroup drop column MENUNAME;
alter table MapGroup drop column WIDTH;
alter table MapGroup drop column HEIGHT;
alter table MapGroup drop column X;
alter table MapGroup drop column Y;
alter table MapGroup drop column WEBNMS;
alter table MapGroup drop column GROUPNAME;
alter table MapGroup drop column OBJTYPE;
alter table MapGroup drop column ANCHORED;
alter table MapGroup add CONSTRAINT `FKD33BEA36F4BC0D9` FOREIGN KEY (`NAME`, `MAPNAME`) REFERENCES `MapSymbol` (`NAME`, `MAPNAME`) ON DELETE CASCADE;
# alter index for table MapGroup
drop index MapGroup0_ndx on MapGroup;
drop index MapGroup1_ndx on MapGroup;
#create index `FKD33BEA36F4BC0D9` on MapGroup(NAME,MAPNAME);
# alter table to create CRITERIAPROPERTIES
SELECT 'STEP 15 : rename CUSTOMPROPS to CRITERIAPROPERTIES table' AS 'MIGRATION PROCESS STATUS ... ';
alter table CUSTOMPROPS RENAME TO CRITERIAPROPERTIES;
alter table CRITERIAPROPERTIES CHANGE KEYSTRING NAME  varchar(100) NOT NULL;
alter table CRITERIAPROPERTIES CHANGE PROPKEY PROPNAME varchar(255) NOT NULL;
alter table CRITERIAPROPERTIES CHANGE PROPVALUE PROPVAL  varchar(250);
alter table CRITERIAPROPERTIES add primary key (NAME,PROPNAME);
alter table CRITERIAPROPERTIES add CONSTRAINT `FK435EDDD2977A5201` FOREIGN KEY (`NAME`) REFERENCES `MapDB` (`NAME`);
drop table if exists CUSTOMPROPS;
# alter index for CRITERIAPROPERTIES
drop index CUSTOMPROPS0_ndx on CRITERIAPROPERTIES;
#create index `FK435EDDD2977A5201` on CRITERIAPROPERTIES(`NAME`);
# create table for MAPPEDPROPERTIES
SELECT 'STEP 16 : create and populate MAPPEDPROPERTIES' AS 'MIGRATION PROCESS STATUS ... ';
#CREATE TABLE `MAPPEDPROPERTIES` (`NAME` varchar(100) NOT NULL,`PROPVAL` varchar(255) default NULL,`PROPNAME` varchar(255) NOT NULL, PRIMARY KEY  (`NAME`,`PROPNAME`), KEY `FK4D2F47A6977A5201` (`NAME`),CONSTRAINT `FK4D2F47A6977A5201` FOREIGN KEY (`NAME`) REFERENCES `MapDB` (`NAME`));
insert into MAPPEDPROPERTIES(NAME,PROPNAME,PROPVAL) select NAME,PROPNAME,PROPVAL from MAPUSERPROPS where MAPNAME = 'NULL';
delete from MAPUSERPROPS where MAPNAME = 'NULL' ;
# alter table for MAPUSERPROPS
SELECT 'STEP 17 : alter MAPUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MAPUSERPROPS drop column OWNERNAME;
alter table MAPUSERPROPS add primary key (NAME,MAPNAME,PROPNAME);
alter table MAPUSERPROPS add constraint `FK30B70EA9AF738122` FOREIGN KEY (`NAME`,`MAPNAME`) REFERENCES `MapSymbol` (`NAME`,`MAPNAME`) ON DELETE CASCADE;
# create index for MAPUSERPROPS table
drop index MAPUSERPROPS0_ndx on MAPUSERPROPS;
drop index MAPUSERPROPS1_ndx on MAPUSERPROPS;
#create index `FK30B70EA9AF738122` on MAPUSERPROPS(`NAME`,`MAPNAME`);
drop table if exists TOPODBSPECIALKEY;
drop table if exists DBINTERFACES;
# alter table for ManagedObject to add;
alter table ManagedObject drop key name;
alter table ManagedObject drop column ownername;
alter table ManagedObject add column MOID bigint(20) auto_increment primary key first;
alter table ManagedObject modify column MOID bigint(20) NOT NULL;
alter table ManagedObject modify column NAME varchar(100) NOT NULL UNIQUE;
# alter column def of STATUSPOLLENABLED
SELECT 'STEP 18 : modify column def for STATUSPOLLENABLED column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column STATUSPOLLENABLED STATUSPOLLENABLED_STR varchar(10) default NULL;
alter table ManagedObject add column STATUSPOLLENABLED bit(1) default NULL AFTER STATUSCHANGETIME;
update ManagedObject set STATUSPOLLENABLED = STATUSPOLLENABLED_STR like 'true';
alter table ManagedObject drop column STATUSPOLLENABLED_STR;
# alter column def of ISCONTAINER
SELECT 'STEP 19 : modify column def for ISCONTAINER column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column ISCONTAINER ISCONTAINER_STR varchar(10) default NULL;
alter table ManagedObject add column ISCONTAINER bit(1) default NULL AFTER FAILURETHRESHOLD;
update ManagedObject set ISCONTAINER = ISCONTAINER_STR like 'true';
alter table ManagedObject drop column ISCONTAINER_STR;
# alter column def of ISGROUP
SELECT 'STEP 20 : modify column def for ISGROUP column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column ISGROUP ISGROUP_STR varchar(10) default NULL;
alter table ManagedObject add column ISGROUP bit(1) default NULL AFTER ISCONTAINER;
update ManagedObject set ISGROUP = ISGROUP_STR like 'true';
alter table ManagedObject drop column ISGROUP_STR;
# alter column definition for MANAGED
SELECT 'STEP 21 : modify column def for MANAGED column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column MANAGED MANAGED_STR varchar(10) default NULL;
alter table ManagedObject add column MANAGED bit(1) default NULL AFTER ISGROUP;
update ManagedObject set MANAGED = MANAGED_STR like 'true';
alter table ManagedObject drop column MANAGED_STR;
# alter column definition for STATUSCHANGETIME
SELECT 'STEP 22 : modify column def for STATUSCHANGETIME column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column STATUSCHANGETIME STATUSCHANGETIME_STR varchar(25) default NULL;
alter table ManagedObject add column STATUSCHANGETIME bigint(20) default NULL AFTER STATUS;
update ManagedObject set STATUSCHANGETIME = STATUSCHANGETIME_STR;
alter table ManagedObject drop column STATUSCHANGETIME_STR;
# alter column definition for STATUSUPDATETIME
SELECT 'STEP 23 : modify column def for STATUSUPDATETIME column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject change column STATUSUPDATETIME STATUSUPDATETIME_STR varchar(25) default NULL;
alter table ManagedObject add column STATUSUPDATETIME bigint(20) default NULL AFTER STATUSPOLLENABLED;
update ManagedObject set STATUSUPDATETIME = STATUSUPDATETIME_STR;
alter table ManagedObject drop column STATUSUPDATETIME_STR;
# add DISCRIMINATOR column
SELECT 'STEP 24 : add DISCRIMINATOR column in ManagedObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject add column DISCRIMINATOR varchar(30) NOT NULL after `MOID`;
update ManagedObject set DISCRIMINATOR = CLASSNAME;
# add PARENTID column
SELECT 'STEP 25 : add PARENTID column in ManagedObject table & populate data using a DUMMY table ' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject add column `PARENTID` bigint(20) default NULL after WEBNMS;
CREATE TABLE `DUMMY` (`MOID` bigint(20) NOT NULL,`PARENTKEY` varchar(250) default NULL);
insert into DUMMY(MOID,PARENTKEY) select moid,name from ManagedObject where name in (select parentKey from ManagedObject order by moid);
update ManagedObject set PARENTID = (select MOID from DUMMY where ManagedObject.PARENTKEY = DUMMY.PARENTKEY);
drop table if exists DUMMY;
alter table ManagedObject add CONSTRAINT `FKB855B9E41BE4C5D` FOREIGN KEY (`PARENTID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
# index for ManagedObject
alter table ManagedObject drop index ManagedObject0_ndx;
alter table ManagedObject drop index ManagedObject2_ndx;
create index `FKB855B9E41BE4C5D` on ManagedObject(PARENTID);
# alter table for ManagedGroupObject
SELECT 'STEP 26 : alter IpAddress table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedGroupObject add column MOID bigint(20) first;
update ManagedGroupObject set moid = (select moid from ManagedObject where ManagedGroupObject.name = ManagedObject.name);
alter table ManagedGroupObject add primary key (MOID);
alter table ManagedGroupObject add constraint `FK2D38159FD43F4EA2` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ManagedGroupObject drop column NAME;
# index creation for ManagedGroupObject
create index `FK2D38159FD43F4EA2` on ManagedGroupObject(MOID);
# alter tables for TopoObject
SELECT 'STEP 27 : alter TopoObject table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject drop column OWNERNAME;
alter table TopoObject add column MOID bigint(20) first;
update TopoObject set moid = (select moid from ManagedObject where TopoObject.name = ManagedObject.name);
alter table TopoObject add primary key (MOID);
alter table TopoObject add constraint `FK608221B9A4FF80BC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TopoObject drop column NAME;
SELECT 'STEP 28 : modify column def for ISDHCP column in TopoObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject change column ISDHCP ISDHCP_STR varchar(10) default NULL;
alter table TopoObject add column ISDHCP bit(1) default NULL AFTER IPADDRESS;
update TopoObject set ISDHCP = ISDHCP_STR like 'true';
alter table TopoObject drop column ISDHCP_STR;
SELECT 'STEP 29 : modify column def for ISINTERFACE column in TopoObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject change column ISINTERFACE ISINTERFACE_STR varchar(10) default NULL;
alter table TopoObject add column ISINTERFACE bit(1) default NULL AFTER ISDHCP;
update TopoObject set ISINTERFACE = ISINTERFACE_STR like 'true';
alter table TopoObject drop column ISINTERFACE_STR;
SELECT 'STEP 30 : modify column def for ISNETWORK column in TopoObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject change column ISNETWORK ISNETWORK_STR varchar(10) default NULL;
alter table TopoObject add column ISNETWORK bit(1) default NULL AFTER ISINTERFACE;
update TopoObject set ISNETWORK = ISNETWORK_STR like 'true';
alter table TopoObject drop column ISNETWORK_STR;
SELECT 'STEP 31 : modify column def for ISNODE column in TopoObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject change column ISNODE ISNODE_STR varchar(10) default NULL;
alter table TopoObject add column ISNODE bit(1) default NULL AFTER ISNETWORK;
update TopoObject set ISNODE = ISNODE_STR like 'true';
alter table TopoObject drop column ISNODE_STR;
SELECT 'STEP 32 : modify column def for ISSNMP column in TopoObject table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject change column ISSNMP ISSNMP_STR varchar(10) default NULL;
alter table TopoObject add column ISSNMP bit(1) default NULL AFTER ISNODE;
update TopoObject set ISSNMP = ISSNMP_STR like 'true';
alter table TopoObject drop column ISSNMP_STR;
# index creation for TopoObject
create index `FK608221B9A4FF80BC` on TopoObject(MOID);
# alter table for Node
SELECT 'STEP 33 : alter Node table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Node drop column OWNERNAME;
alter table Node add column MOID bigint(20) first;
update Node set moid = (select moid from ManagedObject where Node.name = ManagedObject.name);
alter table Node add primary key (MOID);
alter table Node add constraint `FK2522223D370DA5` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Node drop column NAME;
SELECT 'STEP 34 : modify column def for ISROUTER in Node table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Node change column ISROUTER ISROUTER_STR varchar(10) default NULL;
alter table Node add column ISROUTER bit(1) default NULL AFTER MOID;
update Node set ISROUTER = ISROUTER_STR like 'true';
alter table Node drop column ISROUTER_STR;
# index creation for Node
create index `FK2522223D370DA5` on Node(MOID);
# alter table for Network
SELECT 'STEP 35 : alter Network table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Network drop column OWNERNAME;
alter table Network add column MOID bigint(20) first;
update Network set moid = (select moid from ManagedObject where Network.name = ManagedObject.name);
alter table Network add primary key (MOID);
alter table Network add constraint `FKD119F20E5044AD45` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Network drop column NAME;
SELECT 'STEP 36 : modify column def for DISCOVER in Network table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Network change column DISCOVER DISCOVER_STR varchar(10) default NULL;
alter table Network add column DISCOVER bit(1) default NULL AFTER MOID;
update Network set DISCOVER = DISCOVER_STR like 'true';
alter table Network drop column DISCOVER_STR;
# index creation for Network
#drop index Network2_ndx on Network;
create index `FKD119F20E5044AD45` on Network(MOID);
# alter table for SnmpNode
SELECT 'STEP 37 : alter SnmpNode table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SnmpNode drop column OWNERNAME;
alter table SnmpNode add column MOID bigint(20) first;
update SnmpNode set moid = (select moid from ManagedObject where SnmpNode.name = ManagedObject.name);
alter table SnmpNode add primary key (MOID);
alter table SnmpNode add constraint `FK293EA880896A8103` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SnmpNode drop column NAME;
# index creation for SnmpNode
create index `FK293EA880896A8103` on SnmpNode(MOID);
# alter table for SnmpInterface
SELECT 'STEP 38 : alter SnmpInterface table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SnmpInterface drop column OWNERNAME;
alter table SnmpInterface add column MOID bigint(20) first;
update SnmpInterface set moid = (select moid from ManagedObject where SnmpInterface.name = ManagedObject.name);
alter table SnmpInterface add primary key (MOID);
alter table SnmpInterface add constraint `FK7DB9517B6E19E932` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SnmpInterface drop column NAME;
# index creation for SnmpInterface
create index `FK7DB9517B6E19E932` on SnmpInterface(MOID);
# alter table for TL1Node
SELECT 'STEP 39 : alter TL1Node table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TL1Node drop column OWNERNAME;
alter table TL1Node add column MOID bigint(20) first;
update TL1Node set moid = (select moid from ManagedObject where TL1Node.name = ManagedObject.name);
alter table TL1Node add primary key (MOID);
alter table TL1Node add constraint `FKE013625BB76D185D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TL1Node drop column NAME;
# index creation for TL1Node
create index `FKE013625BB76D185D` on TL1Node(MOID);
# alter table for TL1Interface
SELECT 'STEP 40 : alter TL1Interface table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TL1Interface drop column OWNERNAME;
alter table TL1Interface add column MOID bigint(20) first;
update TL1Interface set moid = (select moid from ManagedObject where TL1Interface.name = ManagedObject.name);
alter table TL1Interface add primary key (MOID);
alter table TL1Interface add constraint `FK11A58880F6B1DA18` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TL1Interface drop column NAME;
# index creation for TL1Interface
create index `FK11A58880F6B1DA18` on TL1Interface(MOID);
# alter table for IpAddress
SELECT 'STEP 41 : alter IpAddress table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table IpAddress drop column OWNERNAME;
alter table IpAddress add column MOID bigint(20) first;
update IpAddress set moid = (select moid from ManagedObject where IpAddress.name = ManagedObject.name);
alter table IpAddress add primary key (MOID);
alter table IpAddress add constraint `FKD8D77CAD7825E164` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table IpAddress drop column NAME;
# index creation for IpA	ddress
#drop index IpAddress2_ndx on IpAddress;
#drop index IpAddress3_ndx on IpAddress;
create index `FKD8D77CAD7825E164` on IpAddress(MOID);
# alter table for SwitchObject
SELECT 'STEP 42 : alter SwitchObject table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SwitchObject drop column OWNERNAME;
alter table SwitchObject add column MOID bigint(20) first;
update SwitchObject set moid = (select moid from ManagedObject where SwitchObject.name = ManagedObject.name);
alter table SwitchObject add primary key (MOID);
alter table SwitchObject add constraint `FK4C0B63B3695F01CC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SwitchObject drop column NAME;
# index creation for SwitchObject
create index `FK4C0B63B3695F01CC` on SwitchObject(MOID);
# alter table for PortObject
SELECT 'STEP 43 : alter PortObject table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PortObject drop column OWNERNAME;
alter table PortObject add column MOID bigint(20) first;
update PortObject set moid = (select moid from ManagedObject where PortObject.name = ManagedObject.name);
alter table PortObject add primary key (MOID);
alter table PortObject add constraint `FK679DDF409E30C459` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PortObject drop column NAME;
# index creation for PortObject
create index `FK679DDF409E30C459` on PortObject(MOID);
# alter table for Printer
SELECT 'STEP 44 : alter Printer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Printer drop column OWNERNAME;
alter table Printer add column `CONSOLELIGHTSTRING` varchar(100) default NULL after CONSOLEDISPBUFFERTEXT;
update Printer set CONSOLELIGHTSTRING = (select PROPVAL from TOPOUSERPROPS where PROPNAME = 'CONSOLELIGHTSTRING' and Printer.name = TOPOUSERPROPS.name);
alter table Printer add column MOID bigint(20) first;
update Printer set moid = (select moid from ManagedObject where Printer.name = ManagedObject.name);
alter table Printer add primary key (MOID);
alter table Printer add constraint `FK50765FFA21D93CDB` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Printer drop column NAME;
# index creation for Printer
create index `FK50765FFA21D93CDB` on Printer(MOID);
# alter table for CORBANode
SELECT 'STEP 45 : alter CORBANode table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CORBANode drop column OWNERNAME;
alter table CORBANode add column MOID bigint(20) first;
update CORBANode set moid = (select moid from ManagedObject where CORBANode.name = ManagedObject.name);
alter table CORBANode add primary key (MOID);
alter table CORBANode add constraint `FK427DD3C7435BA95` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CORBANode drop column NAME;
alter table CORBANode modify column STATUSPOLLIOR text after STATUSPOLLCOMMAND;
alter table CORBANode modify column DATAPOLLIOR text after STATUSPOLLIOR;
# index creation for CORBANode
create index `FK427DD3C7435BA95` on CORBANode(MOID);
# alter table for PhysicalEntity
SELECT 'STEP 46 : alter PhysicalEntity table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalEntity drop column OWNERNAME;
alter table PhysicalEntity add column MOID bigint(20) first;
update PhysicalEntity set moid = (select moid from ManagedObject where PhysicalEntity.name = ManagedObject.name);
alter table PhysicalEntity add primary key (MOID);
alter table PhysicalEntity add constraint `FKB2493FBAF24E35DD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalEntity drop column NAME;
# index creation for PhysicalEntity
create index `FKB2493FBAF24E35DD` on PhysicalEntity(MOID);
# alter table for PhysicalContainer
SELECT 'STEP 47 : alter PhysicalContainer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalContainer drop column OWNERNAME;
alter table PhysicalContainer add column MOID bigint(20) first;
update PhysicalContainer set moid = (select moid from ManagedObject where PhysicalContainer.name = ManagedObject.name);
alter table PhysicalContainer add primary key (MOID);
alter table PhysicalContainer add constraint `FK6DB5CD4A25E1F9E1` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalContainer drop column NAME;
# index creation for PhysicalContainer
create index `FK6DB5CD4A25E1F9E1` on PhysicalContainer(MOID);
# alter table for PhysicalElement
SELECT 'STEP 48 : alter PhysicalElement table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalElement drop column OWNERNAME;
alter table PhysicalElement add column MOID bigint(20) first;
update PhysicalElement set moid = (select moid from ManagedObject where PhysicalElement.name = ManagedObject.name);
alter table PhysicalElement add primary key (MOID);
alter table PhysicalElement add constraint `FK92A33E054D3C391C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalElement drop column NAME;
# index creation for PhysicalElement
create index `FK92A33E054D3C391C` on PhysicalElement(MOID);
# alter table for PhysicalSubUnit
SELECT 'STEP 49 : alter PhysicalSubUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalSubUnit drop column OWNERNAME;
alter table PhysicalSubUnit add column MOID bigint(20) first;
update PhysicalSubUnit set moid = (select moid from ManagedObject where PhysicalSubUnit.name = ManagedObject.name);
alter table PhysicalSubUnit add primary key (MOID);
alter table PhysicalSubUnit add constraint `FK86610AED40FA0604` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalSubUnit drop column NAME;
# index creation for PhysicalSubUnit
create index `FK86610AED40FA0604` on PhysicalSubUnit(MOID);
# alter table for PhysicalUnit
SELECT 'STEP 50 : alter PhysicalUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalUnit drop column OWNERNAME;
alter table PhysicalUnit add column MOID bigint(20) first;
update PhysicalUnit set moid = (select moid from ManagedObject where PhysicalUnit.name = ManagedObject.name);
alter table PhysicalUnit add primary key (MOID);
alter table PhysicalUnit add constraint `FKB1E53F1B32D3F6BE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalUnit drop column NAME;
# index creation for PhysicalUnit
create index `FKB1E53F1B32D3F6BE` on PhysicalUnit(MOID);
# alter table for LogicalContainer
SELECT 'STEP 51 : alter LogicalContainer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalContainer drop column OWNERNAME;
alter table LogicalContainer add column MOID bigint(20) first;
update LogicalContainer set moid = (select moid from ManagedObject where LogicalContainer.name = ManagedObject.name);
alter table LogicalContainer add primary key (MOID);
alter table LogicalContainer add constraint `FK73B48E9863A233B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogicalContainer drop column NAME;
# index creation for LogicalContainer
create index `FK73B48E9863A233B` on LogicalContainer(MOID);
# alter table for LogicalElement
SELECT 'STEP 52 : alter LogicalElement table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalElement drop column OWNERNAME;
alter table LogicalElement add column MOID bigint(20) first;
update LogicalElement set moid = (select moid from ManagedObject where LogicalElement.name = ManagedObject.name);
alter table LogicalElement add primary key (MOID);
alter table LogicalElement add constraint `FK1C5DFAD35C62F0F6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogicalElement drop column NAME;
# index creation for LogicalElement
create index `FK1C5DFAD35C62F0F6` on LogicalElement(MOID);
# alter table for LogicalUnit
SELECT 'STEP 53 : alter LogicalUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalUnit drop column OWNERNAME;
alter table LogicalUnit add column MOID bigint(20) first;
update LogicalUnit set moid = (select moid from ManagedObject where LogicalUnit.name = ManagedObject.name);
alter table LogicalUnit add primary key (MOID);
alter table LogicalUnit add constraint `FK834A650D1C49DD24` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogicalUnit drop column NAME;
# index creation for LogicalUnit
create index `FK834A650D1C49DD24` on LogicalUnit(MOID);
# alter table for ProtectionGroup
SELECT 'STEP 54 : alter ProtectionGroup table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ProtectionGroup drop column OWNERNAME;
alter table ProtectionGroup add column MOID bigint(20) first;
update ProtectionGroup set moid = (select moid from ManagedObject where ProtectionGroup.name = ManagedObject.name);
alter table ProtectionGroup add primary key (MOID);
alter table ProtectionGroup add constraint `FK41498306FBE27E1D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ProtectionGroup drop column NAME;
# index creation for ProtectionGroup
create index `FK41498306FBE27E1D` on ProtectionGroup(MOID);
# alter table for SBNE
SELECT 'STEP 55 : alter SBNE table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SBNE drop column OWNERNAME;
alter table SBNE add column MOID bigint(20) first;
update SBNE set moid = (select moid from ManagedObject where SBNE.name = ManagedObject.name);
alter table SBNE add primary key (MOID);
alter table SBNE add constraint `FK26BC468EC439E9` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SBNE drop column NAME;
# index creation for SBNE
create index `FK26BC468EC439E9` on SBNE(MOID);
# alter table for NetworkElementManagement
SELECT 'STEP 56 : alter NetworkElementManagement table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table NetworkElementManagement drop column OWNERNAME;
alter table NetworkElementManagement add column MOID bigint(20) first;
update NetworkElementManagement set moid = (select moid from ManagedObject where NetworkElementManagement.name = ManagedObject.name);
alter table NetworkElementManagement add primary key (MOID);
alter table NetworkElementManagement add constraint `FK7D44D3F1C014A294` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table NetworkElementManagement drop column NAME;
# index creation for NetworkElementManagement
create index `FK7D44D3F1C014A294` on NetworkElementManagement(MOID);
# alter table for DataObject
SELECT 'STEP 57 : alter DataObject table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table DataObject drop column OWNERNAME;
alter table DataObject add column MOID bigint(20) first;
update DataObject set moid = (select moid from ManagedObject where DataObject.name = ManagedObject.name);
alter table DataObject add primary key (MOID);
alter table DataObject add constraint `FKBB1A73A9A7754CCC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table DataObject drop column NAME;
# index creation for DataObject
create index `FKBB1A73A9A7754CCC` on DataObject(MOID);
# alter table for IpConfig
SELECT 'STEP 58 : alter IpConfig table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table IpConfig drop column OWNERNAME;
alter table IpConfig add column MOID bigint(20) first;
update IpConfig set moid = (select moid from ManagedObject where IpConfig.name = ManagedObject.name);
alter table IpConfig add primary key (MOID);
alter table IpConfig add constraint `FK3C9427693BF5820C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table IpConfig drop column NAME;
# index creation for IpConfig
create index `FK3C9427693BF5820C` on IpConfig(MOID);
# alter table for EventControl
SELECT 'STEP 59 : alter EventControl table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EventControl drop column OWNERNAME;
alter table EventControl add column MOID bigint(20) first;
update EventControl set moid = (select moid from ManagedObject where EventControl.name = ManagedObject.name);
alter table EventControl add primary key (MOID);
alter table EventControl add constraint `FK78A59D83F9945526` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EventControl drop column NAME;
# index creation for EventControl
create index `FK78A59D83F9945526` on EventControl(MOID);
# alter table for RelationObject
SELECT 'STEP 60 : alter RelationObject table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table RelationObject drop column OWNERNAME;
alter table RelationObject add column MOID bigint(20) first;
update RelationObject set moid = (select moid from ManagedObject where RelationObject.name = ManagedObject.name);
alter table RelationObject add primary key (MOID);
alter table RelationObject add constraint `FKEFA3E87B2FA8DE9E` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table RelationObject drop column NAME;
# index creation for RelationObject
create index `FKEFA3E87B2FA8DE9E` on RelationObject(MOID);
# alter table for PhysicalUnitStream
SELECT 'STEP 61 : alter PhysicalUnitStream table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalUnitStream drop column OWNERNAME;
alter table PhysicalUnitStream add column MOID bigint(20) first;
update PhysicalUnitStream set moid = (select moid from ManagedObject where PhysicalUnitStream.name = ManagedObject.name);
alter table PhysicalUnitStream add primary key (MOID);
alter table PhysicalUnitStream add constraint `FKC33C7A7B3D5D8F2` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PhysicalUnitStream drop column NAME;
# index creation for PhysicalUnitStream
create index `FKC33C7A7B3D5D8F2` on PhysicalUnitStream(MOID);
# alter table for LogicalUnitStream
SELECT 'STEP 62 : alter LogicalUnitStream table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalUnitStream drop column OWNERNAME;
alter table LogicalUnitStream add column MOID bigint(20) first;
update LogicalUnitStream set moid = (select moid from ManagedObject where LogicalUnitStream.name = ManagedObject.name);
alter table LogicalUnitStream add primary key (MOID);
alter table LogicalUnitStream add constraint `FK7D15E2ED4DD089B0` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogicalUnitStream drop column NAME;
# index creation for LogicalUnitStream
create index `FK7D15E2ED4DD089B0` on LogicalUnitStream(MOID);
# alter table for LogStreamDataConfig
SELECT 'STEP 63 : alter LogStreamDataConfig table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogStreamDataConfig drop column OWNERNAME;
alter table LogStreamDataConfig add column MOID bigint(20) first;
update LogStreamDataConfig set moid = (select moid from ManagedObject where LogStreamDataConfig.name = ManagedObject.name);
alter table LogStreamDataConfig add primary key (MOID);
alter table LogStreamDataConfig add constraint `FK3D63EA709F587B3` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogStreamDataConfig drop column NAME;
# index creation for LogStreamDataConfig
create index `FK3D63EA709F587B3` on LogStreamDataConfig(MOID);
# alter table for LogStream
SELECT 'STEP 64 : alter LogStream table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogStream drop column OWNERNAME;
alter table LogStream add column MOID bigint(20) first;
update LogStream set moid = (select moid from ManagedObject where LogStream.name = ManagedObject.name);
alter table LogStream add primary key (MOID);
alter table LogStream add constraint `FKEBCF7BC44C2A0887` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LogStream drop column NAME;
# index creation for LogStream
create index `FKEBCF7BC44C2A0887` on LogStream(MOID);
# alter table for Constituent
SELECT 'STEP 65 : alter Constituent table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Constituent drop column OWNERNAME;
alter table Constituent add column MOID bigint(20) first;
update Constituent set moid = (select moid from ManagedObject where Constituent.name = ManagedObject.name);
alter table Constituent add primary key (MOID);
alter table Constituent add constraint `FKF55AC104856D278` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Constituent drop column NAME;
# index creation for Constituent
create index `FKF55AC104856D278` on Constituent(MOID);
# alter table for ConnectivityData
SELECT 'STEP 66 : alter ConnectivityData table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ConnectivityData drop column OWNERNAME;
alter table ConnectivityData add column MOID bigint(20) first;
update ConnectivityData set moid = (select moid from ManagedObject where ConnectivityData.name = ManagedObject.name);
alter table ConnectivityData add primary key (MOID);
alter table ConnectivityData add constraint `FK8BE5E215D4EFF6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ConnectivityData drop column NAME;
# index creation for ConnectivityData
create index `FK8BE5E215D4EFF6` on ConnectivityData(MOID);
# alter table for Interface
SELECT 'STEP 67 : alter Interface table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Interface drop column OWNERNAME;
alter table Interface add column MOID bigint(20) first;
update Interface set moid = (select moid from ManagedObject where Interface.name = ManagedObject.name);
alter table Interface add primary key (MOID);
alter table Interface add constraint `FK95678D1931884ABE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Interface drop column NAME;
# index creation for Interface
create index `FK95678D1931884ABE` on Interface(MOID);
# alter table for InterfaceContainer
SELECT 'STEP 68 : alter InterfaceContainer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table InterfaceContainer drop column OWNERNAME;
alter table InterfaceContainer add column MOID bigint(20) first;
update InterfaceContainer set moid = (select moid from ManagedObject where InterfaceContainer.name = ManagedObject.name);
alter table InterfaceContainer add primary key (MOID);
alter table InterfaceContainer add constraint `FK3BE68B688D85973D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table InterfaceContainer drop column NAME;
# index creation for InterfaceContainer
create index `FK3BE68B688D85973D` on InterfaceContainer(MOID);
# alter table for Endpoint
SELECT 'STEP 69 : alter Endpoint table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Endpoint drop column OWNERNAME;
alter table Endpoint add column MOID bigint(20) first;
update Endpoint set moid = (select moid from ManagedObject where Endpoint.name = ManagedObject.name);
alter table Endpoint add primary key (MOID);
alter table Endpoint add constraint `FK6BA181B5D3F52B8A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Endpoint drop column NAME;
# index creation for Endpoint
create index `FK6BA181B5D3F52B8A` on Endpoint (MOID);
# alter table for BlackBoxLogStream
SELECT 'STEP 70 : alter BlackBoxLogStream table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BlackBoxLogStream drop column OWNERNAME;
alter table BlackBoxLogStream add column MOID bigint(20) first;
update BlackBoxLogStream set moid = (select moid from ManagedObject where BlackBoxLogStream.name = ManagedObject.name);
alter table BlackBoxLogStream add primary key (MOID);
alter table BlackBoxLogStream add constraint `FK4336FD8286709EC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BlackBoxLogStream drop column NAME;
# index creation for BlackBoxLogStream
create index `FK4336FD8286709EC` on BlackBoxLogStream(MOID);
# alter table for CoreFileMgmtLogStream
SELECT 'STEP 71 : alter CoreFileMgmtLogStream table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CoreFileMgmtLogStream drop column OWNERNAME;
alter table CoreFileMgmtLogStream add column MOID bigint(20) first;
update CoreFileMgmtLogStream set moid = (select moid from ManagedObject where CoreFileMgmtLogStream.name = ManagedObject.name);
alter table CoreFileMgmtLogStream add primary key (MOID);
alter table CoreFileMgmtLogStream add constraint `FK4F535E08BA15349C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CoreFileMgmtLogStream drop column NAME;
# index creation for CoreFileMgmtLogStream
create index `FK4F535E08BA15349C` on CoreFileMgmtLogStream(MOID);
# alter table for HapNeControl
SELECT 'STEP 72 : alter HapNeControl table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table HapNeControl drop column OWNERNAME;
alter table HapNeControl add column MOID bigint(20) first;
update HapNeControl set moid = (select moid from ManagedObject where HapNeControl.name = ManagedObject.name);
alter table HapNeControl add primary key (MOID);
alter table HapNeControl add constraint `FK39603EEF7A94456C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table HapNeControl drop column NAME;
# index creation for HapNeControl
create index `FK39603EEF7A94456C` on HapNeControl(MOID);
# alter table for CnpEthernetPortGroup
SELECT 'STEP 73 : alter CnpEthernetPortGroup table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEthernetPortGroup drop column OWNERNAME;
alter table CnpEthernetPortGroup add column MOID bigint(20) first;
update CnpEthernetPortGroup set moid = (select moid from ManagedObject where CnpEthernetPortGroup.name = ManagedObject.name);
alter table CnpEthernetPortGroup add primary key (MOID);
alter table CnpEthernetPortGroup add constraint `FK3CB491A2E1241391` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpEthernetPortGroup drop column NAME;
# index creation for CnpEthernetPortGroup
create index `FK3CB491A2E1241391` on CnpEthernetPortGroup(MOID);
# alter table for CnpFileSystemInfo
SELECT 'STEP 74 : alter CnpFileSystemInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFileSystemInfo drop column OWNERNAME;
alter table CnpFileSystemInfo add column MOID bigint(20) first;
update CnpFileSystemInfo set moid = (select moid from ManagedObject where CnpFileSystemInfo.name = ManagedObject.name);
alter table CnpFileSystemInfo add primary key (MOID);
alter table CnpFileSystemInfo add constraint `FK89CE7ABED717DA89` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpFileSystemInfo drop column NAME;
# index creation for CnpFileSystemInfo
create index `FK89CE7ABED717DA89` on CnpFileSystemInfo(MOID);
# alter table for CnpRaidPortGroup
SELECT 'STEP 75 : alter CnpRaidPortGroup table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPortGroup drop column OWNERNAME;
alter table CnpRaidPortGroup add column MOID bigint(20) first;
update CnpRaidPortGroup set moid = (select moid from ManagedObject where CnpRaidPortGroup.name = ManagedObject.name);
alter table CnpRaidPortGroup add primary key (MOID);
alter table CnpRaidPortGroup add constraint `FKFDDDE4CFADF90FBE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidPortGroup drop column NAME;
# index creation for CnpRaidPortGroup
create index `FKFDDDE4CFADF90FBE` on CnpRaidPortGroup(MOID);
# alter table for CnpServerInfo
SELECT 'STEP 76 : alter CnpServerInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpServerInfo drop column OWNERNAME;
alter table CnpServerInfo add column MOID bigint(20) first;
update CnpServerInfo set moid = (select moid from ManagedObject where CnpServerInfo.name = ManagedObject.name);
alter table CnpServerInfo add primary key (MOID);
alter table CnpServerInfo add constraint `FKE8A581B6F50BD881` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpServerInfo drop column NAME;
# index creation for CnpServerInfo
create index `FKE8A581B6F50BD881` on CnpServerInfo(MOID);
# alter table for CnpEthernetPort
SELECT 'STEP 77 : alter CnpEthernetPort table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEthernetPort drop column OWNERNAME;
alter table CnpEthernetPort add column MOID bigint(20) first;
update CnpEthernetPort set moid = (select moid from ManagedObject where CnpEthernetPort.name = ManagedObject.name);
alter table CnpEthernetPort add primary key (MOID);
alter table CnpEthernetPort add constraint `FKE27A36DDAE8BA228` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpEthernetPort drop column NAME;
# index creation for CnpEthernetPort
create index `FKE27A36DDAE8BA228` on CnpEthernetPort(MOID);
# alter table for CnpRaidPort
SELECT 'STEP 78 : alter CnpRaidPort table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPort drop column OWNERNAME;
alter table CnpRaidPort add column MOID bigint(20) first;
update CnpRaidPort set moid = (select moid from ManagedObject where CnpRaidPort.name = ManagedObject.name);
alter table CnpRaidPort add primary key (MOID);
alter table CnpRaidPort add constraint `FKD78ADB90A38AFDDB` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidPort drop column NAME;
# index creation for CnpRaidPort
create index `FKD78ADB90A38AFDDB` on CnpRaidPort(MOID);
# alter table for CnpCageInfo
SELECT 'STEP 79 : alter CnpCageInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpCageInfo drop column OWNERNAME;
alter table CnpCageInfo add column MOID bigint(20) first;
update CnpCageInfo set moid = (select moid from ManagedObject where CnpCageInfo.name = ManagedObject.name);
alter table CnpCageInfo add primary key (MOID);
alter table CnpCageInfo add constraint `FKBDFA6CEF89FA8F3A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpCageInfo drop column NAME;
# index creation for CnpCageInfo
create index `FKBDFA6CEF89FA8F3A` on CnpCageInfo(MOID);
# alter table for CnpACL
SELECT 'STEP 80 : alter CnpACL table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpACL drop column OWNERNAME;
alter table CnpACL add column MOID bigint(20) first;
update CnpACL set moid = (select moid from ManagedObject where CnpACL.name = ManagedObject.name);
alter table CnpACL add primary key (MOID);
alter table CnpACL add constraint `FK7896B1056453B274` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpACL drop column NAME;
# index creation for CnpACL
create index `FK7896B1056453B274` on CnpACL(MOID);
# alter table for CnpFruInfo
SELECT 'STEP 81 : alter CnpFruInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFruInfo drop column OWNERNAME;
alter table CnpFruInfo add column MOID bigint(20) first;
update CnpFruInfo set moid = (select moid from ManagedObject where CnpFruInfo.name = ManagedObject.name);
alter table CnpFruInfo add primary key (MOID);
alter table CnpFruInfo add constraint `FK573430724D7648E1` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpFruInfo drop column NAME;
# index creation for CnpFruInfo
create index `FK573430724D7648E1` on CnpFruInfo(MOID);
# alter table for CnpSystemInfo
SELECT 'STEP 82 : alter CnpSystemInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSystemInfo drop column OWNERNAME;
alter table CnpSystemInfo add column MOID bigint(20) first;
update CnpSystemInfo set moid = (select moid from ManagedObject where CnpSystemInfo.name = ManagedObject.name);
alter table CnpSystemInfo add primary key (MOID);
alter table CnpSystemInfo add constraint `FK7BCC6EA28832C56D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpSystemInfo drop column NAME;
# index creation for CnpSystemInfo
create index `FK7BCC6EA28832C56D` on CnpSystemInfo(MOID);
# alter table for CnpLocationInfo
SELECT 'STEP 83 : alter CnpLocationInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpLocationInfo drop column OWNERNAME;
alter table CnpLocationInfo add column MOID bigint(20) first;
update CnpLocationInfo set moid = (select moid from ManagedObject where CnpLocationInfo.name = ManagedObject.name);
alter table CnpLocationInfo add primary key (MOID);
alter table CnpLocationInfo add constraint `FKF15933E8BD6A9F33` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpLocationInfo drop column NAME;
# index creation for CnpLocationInfo
create index `FKF15933E8BD6A9F33` on CnpLocationInfo(MOID);
# alter table for CnpMonDevInfo
SELECT 'STEP 84 : alter CnpMonDevInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpMonDevInfo drop column OWNERNAME;
alter table CnpMonDevInfo add column MOID bigint(20) first;
update CnpMonDevInfo set moid = (select moid from ManagedObject where CnpMonDevInfo.name = ManagedObject.name);
alter table CnpMonDevInfo add primary key (MOID);
alter table CnpMonDevInfo add constraint `FK55548DC11BB9FA7` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpMonDevInfo drop column NAME;
# index creation for CnpMonDevInfo
create index `FK55548DC11BB9FA7` on CnpMonDevInfo(MOID);
# alter table for CnpCage
SELECT 'STEP 85 : alter CnpCage table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpCage drop column OWNERNAME;
alter table CnpCage add column MOID bigint(20) first;
update CnpCage set moid = (select moid from ManagedObject where CnpCage.name = ManagedObject.name);
alter table CnpCage add primary key (MOID);
alter table CnpCage add constraint `FK9A40CCA1793783C3` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpCage drop column NAME;
# index creation for CnpCage
create index `FK9A40CCA1793783C3` on CnpCage(MOID);
# alter table for CnpClusterManager
SELECT 'STEP 86 : alter CnpClusterManager table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpClusterManager drop column OWNERNAME;
alter table CnpClusterManager add column MOID bigint(20) first;
update CnpClusterManager set moid = (select moid from ManagedObject where CnpClusterManager.name = ManagedObject.name);
alter table CnpClusterManager add primary key (MOID);
alter table CnpClusterManager add constraint `FK83EF00F824E9DDA` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpClusterManager drop column NAME;
# index creation for CnpClusterManager
create index `FK83EF00F824E9DDA` on CnpClusterManager(MOID);
# alter table for CnpClusterManagerPG
SELECT 'STEP 87 : alter CnpClusterManagerPG table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpClusterManagerPG drop column OWNERNAME;
alter table CnpClusterManagerPG add column MOID bigint(20) first;
update CnpClusterManagerPG set moid = (select moid from ManagedObject where CnpClusterManagerPG.name = ManagedObject.name);
alter table CnpClusterManagerPG add primary key (MOID);
alter table CnpClusterManagerPG add constraint `FK4432ACEFE9043491` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpClusterManagerPG drop column NAME;
# index creation for CnpClusterManagerPG
create index `FK4432ACEFE9043491` on CnpClusterManagerPG(MOID);
# alter table for CnpEmsServer
SELECT 'STEP 88 : alter CnpEmsServer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsServer drop column OWNERNAME;
alter table CnpEmsServer add column MOID bigint(20) first;
update CnpEmsServer set moid = (select moid from ManagedObject where CnpEmsServer.name = ManagedObject.name);
alter table CnpEmsServer add primary key (MOID);
alter table CnpEmsServer add constraint `FK660A5429690D58A1` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpEmsServer drop column NAME;
# index creation for CnpEmsServer
create index `FK660A5429690D58A1` on CnpEmsServer(MOID);
# alter table for CnpEmsServerPG
SELECT 'STEP 89 : alter CnpEmsServerPG table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsServerPG drop column OWNERNAME;
alter table CnpEmsServerPG add column MOID bigint(20) first;
update CnpEmsServerPG set moid = (select moid from ManagedObject where CnpEmsServerPG.name = ManagedObject.name);
alter table CnpEmsServerPG add primary key (MOID);
alter table CnpEmsServerPG add constraint `FKCC5F7E09AFF5998` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpEmsServerPG drop column NAME;
# index creation for CnpEmsServerPG
create index `FKCC5F7E09AFF5998` on CnpEmsServerPG(MOID);
# alter table for CnpExtSwitch
SELECT 'STEP 90 : alter CnpExtSwitch table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpExtSwitch drop column OWNERNAME;
alter table CnpExtSwitch add column MOID bigint(20) first;
update CnpExtSwitch set moid = (select moid from ManagedObject where CnpExtSwitch.name = ManagedObject.name);
alter table CnpExtSwitch add primary key (MOID);
alter table CnpExtSwitch add constraint `FK129704F0159A0968` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpExtSwitch drop column NAME;
# index creation for CnpExtSwitch
create index `FK129704F0159A0968` on CnpExtSwitch(MOID);
# alter table for CnpFan
SELECT 'STEP 91 : alter CnpFan table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFan drop column OWNERNAME;
alter table CnpFan add column MOID bigint(20) first;
update CnpFan set moid = (select moid from ManagedObject where CnpFan.name = ManagedObject.name);
alter table CnpFan add primary key (MOID);
alter table CnpFan add constraint `FK7896C78E1CE0D446` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpFan drop column NAME;
# index creation for CnpFan
create index `FK7896C78E1CE0D446` on CnpFan(MOID);
# alter table for CnpPEM
SELECT 'STEP 92 : alter CnpPEM table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPEM drop column OWNERNAME;
alter table CnpPEM add column MOID bigint(20) first;
update CnpPEM set moid = (select moid from ManagedObject where CnpPEM.name = ManagedObject.name);
alter table CnpPEM add primary key (MOID);
alter table CnpPEM add constraint `FK7896E9931CE0F64B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPEM drop column NAME;
# index creation for CnpPEM
create index `FK7896E9931CE0F64B` on CnpPEM(MOID);
# alter table for CnpRaid
SELECT 'STEP 93 : alter CnpRaid table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaid drop column OWNERNAME;
alter table CnpRaid add column MOID bigint(20) first;
update CnpRaid set moid = (select moid from ManagedObject where CnpRaid.name = ManagedObject.name);
alter table CnpRaid add primary key (MOID);
alter table CnpRaid add constraint `FK9A479E6F793E5591` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaid drop column NAME;
# index creation for CnpRaid
create index `FK9A479E6F793E5591` on CnpRaid(MOID);
# alter table for CnpRaidController
SELECT 'STEP 94 : alter CnpRaidController table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidController drop column OWNERNAME;
alter table CnpRaidController add column MOID bigint(20) first;
update CnpRaidController set moid = (select moid from ManagedObject where CnpRaidController.name = ManagedObject.name);
alter table CnpRaidController add primary key (MOID);
alter table CnpRaidController add constraint `FKFFD3DF6B7E337C4D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidController drop column NAME;
# index creation for CnpRaidController
create index `FKFFD3DF6B7E337C4D` on CnpRaidController(MOID);
# alter table for CnpRaidFan
SELECT 'STEP 95 : alter CnpRaidFan table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidFan drop column OWNERNAME;
alter table CnpRaidFan add column MOID bigint(20) first;
update CnpRaidFan set moid = (select moid from ManagedObject where CnpRaidFan.name = ManagedObject.name);
alter table CnpRaidFan add primary key (MOID);
alter table CnpRaidFan add constraint `FKB45F2A24F3B5815C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidFan drop column NAME;
# index creation for CnpRaidFan
create index `FKB45F2A24F3B5815C` on CnpRaidFan(MOID);
# alter table for CnpRaidLogicalDrive
SELECT 'STEP 96 : alter CnpRaidLogicalDrive table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidLogicalDrive drop column OWNERNAME;
alter table CnpRaidLogicalDrive add column MOID bigint(20) first;
update CnpRaidLogicalDrive set moid = (select moid from ManagedObject where CnpRaidLogicalDrive.name = ManagedObject.name);
alter table CnpRaidLogicalDrive add primary key (MOID);
alter table CnpRaidLogicalDrive add constraint `FK95AEEF103A8076B2` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidLogicalDrive drop column NAME;
# index creation for CnpRaidLogicalDrive
create index `FK95AEEF103A8076B2` on CnpRaidLogicalDrive(MOID);
# alter table for CnpRaidLogicalUnit
SELECT 'STEP 97 : alter CnpRaidLogicalUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidLogicalUnit drop column OWNERNAME;
alter table CnpRaidLogicalUnit add column MOID bigint(20) first;
update CnpRaidLogicalUnit set moid = (select moid from ManagedObject where CnpRaidLogicalUnit.name = ManagedObject.name);
alter table CnpRaidLogicalUnit add primary key (MOID);
alter table CnpRaidLogicalUnit add constraint `FK80BABB3EC84DE776` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidLogicalUnit drop column NAME;
# index creation for CnpRaidLogicalUnit
create index `FK80BABB3EC84DE776` on CnpRaidLogicalUnit(MOID);
# alter table for CnpRaidPEM
SELECT 'STEP 98 : alter CnpRaidPEM table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPEM drop column OWNERNAME;
alter table CnpRaidPEM add column MOID bigint(20) first;
update CnpRaidPEM set moid = (select moid from ManagedObject where CnpRaidPEM.name = ManagedObject.name);
alter table CnpRaidPEM add primary key (MOID);
alter table CnpRaidPEM add constraint `FKB45F4C29F3B5A361` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidPEM drop column NAME;
# index creation for CnpRaidPEM
create index `FKB45F4C29F3B5A361` on CnpRaidPEM(MOID);
# alter table for CnpRaidPhysicalDrive
SELECT 'STEP 99 : alter CnpRaidPhysicalDrive table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPhysicalDrive drop column OWNERNAME;
alter table CnpRaidPhysicalDrive add column MOID bigint(20) first;
update CnpRaidPhysicalDrive set moid = (select moid from ManagedObject where CnpRaidPhysicalDrive.name = ManagedObject.name);
alter table CnpRaidPhysicalDrive add primary key (MOID);
alter table CnpRaidPhysicalDrive add constraint `FKEC8874C4DBE70E3C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRaidPhysicalDrive drop column NAME;
# index creation for CnpRaidPhysicalDrive
create index `FKEC8874C4DBE70E3C` on CnpRaidPhysicalDrive(MOID);
# alter table for CnpShmm
SELECT 'STEP 100 : alter CnpShmm table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShmm drop column OWNERNAME;
alter table CnpShmm add column MOID bigint(20) first;
update CnpShmm set moid = (select moid from ManagedObject where CnpShmm.name = ManagedObject.name);
alter table CnpShmm add primary key (MOID);
alter table CnpShmm add constraint `FK9A482D9A793EE4BC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpShmm drop column NAME;
# index creation for CnpShmm
create index `FK9A482D9A793EE4BC` on CnpShmm(MOID);
# alter table for CnpSwitch
SELECT 'STEP 101 : alter CnpSwitch table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSwitch drop column OWNERNAME;
alter table CnpSwitch add column MOID bigint(20) first;
update CnpSwitch set moid = (select moid from ManagedObject where CnpSwitch.name = ManagedObject.name);
alter table CnpSwitch add primary key (MOID);
alter table CnpSwitch add constraint `FK29C4E4D965CFF6BB` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpSwitch drop column NAME;
# index creation for CnpSwitch
create index `FK29C4E4D965CFF6BB` on CnpSwitch(MOID);
# alter table for CnpTermServer
SELECT 'STEP 102 : alter CnpTermServer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpTermServer drop column OWNERNAME;
alter table CnpTermServer add column MOID bigint(20) first;
update CnpTermServer set moid = (select moid from ManagedObject where CnpTermServer.name = ManagedObject.name);
alter table CnpTermServer add primary key (MOID);
alter table CnpTermServer add constraint `FKE17B31F438D7E956` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpTermServer drop column NAME;
# index creation for CnpTermServer
create index `FKE17B31F438D7E956` on CnpTermServer(MOID);
# alter table for CnpSwitchUnit
SELECT 'STEP 103 : alter CnpSwitchUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSwitchUnit drop column OWNERNAME;
alter table CnpSwitchUnit add column MOID bigint(20) first;
update CnpSwitchUnit set moid = (select moid from ManagedObject where CnpSwitchUnit.name = ManagedObject.name);
alter table CnpSwitchUnit add primary key (MOID);
alter table CnpSwitchUnit add constraint `FK40CD1DFD9829D55F` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpSwitchUnit drop column NAME;
# index creation for CnpSwitchUnit
create index `FK40CD1DFD9829D55F` on CnpSwitchUnit(MOID);
# alter table for CnpRTM
SELECT 'STEP 104 : alter CnpRTM table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRTM drop column OWNERNAME;
alter table CnpRTM add column MOID bigint(20) first;
update CnpRTM set moid = (select moid from ManagedObject where CnpRTM.name = ManagedObject.name);
alter table CnpRTM add primary key (MOID);
alter table CnpRTM add constraint `FK7896F2E61CE0FF9E` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpRTM drop column NAME;
# index creation for CnpRTM
create index `FK7896F2E61CE0FF9E` on CnpRTM(MOID);
# alter table for BlackBoxDataConfig
SELECT 'STEP 105 : alter BlackBoxDataConfig table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BlackBoxDataConfig drop column OWNERNAME;
alter table BlackBoxDataConfig add column MOID bigint(20) first;
update BlackBoxDataConfig set moid = (select moid from ManagedObject where BlackBoxDataConfig.name = ManagedObject.name);
alter table BlackBoxDataConfig add primary key (MOID);
alter table BlackBoxDataConfig add constraint `FK9B73E218E3070E50` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BlackBoxDataConfig drop column NAME;
# index creation for BlackBoxDataConfig
create index `FK9B73E218E3070E50` on BlackBoxDataConfig(MOID);
# alter table for CnpAirFilter
SELECT 'STEP 106 : alter CnpAirFilter table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpAirFilter drop column OWNERNAME;
alter table CnpAirFilter add column MOID bigint(20) first;
update CnpAirFilter set moid = (select moid from ManagedObject where CnpAirFilter.name = ManagedObject.name);
alter table CnpAirFilter add primary key (MOID);
alter table CnpAirFilter add constraint `FK2A94459D2D974A15` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpAirFilter drop column NAME;
# index creation for CnpAirFilter
create index `FK2A94459D2D974A15` on CnpAirFilter(MOID);
# alter table for CnpSystemAlarmPanel
SELECT 'STEP 107 : alter CnpSystemAlarmPanel table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSystemAlarmPanel drop column OWNERNAME;
alter table CnpSystemAlarmPanel add column MOID bigint(20) first;
update CnpSystemAlarmPanel set moid = (select moid from ManagedObject where CnpSystemAlarmPanel.name = ManagedObject.name);
alter table CnpSystemAlarmPanel add primary key (MOID);
alter table CnpSystemAlarmPanel add constraint `FK65012D279D2B4C9` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpSystemAlarmPanel drop column NAME;
# index creation for CnpSystemAlarmPanel
create index `FK65012D279D2B4C9` on CnpSystemAlarmPanel(MOID);
# alter table for CnpShelfEEPROM
SELECT 'STEP 108 : alter CnpShelfEEPROM table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShelfEEPROM drop column OWNERNAME;
alter table CnpShelfEEPROM add column MOID bigint(20) first;
update CnpShelfEEPROM set moid = (select moid from ManagedObject where CnpShelfEEPROM.name = ManagedObject.name);
alter table CnpShelfEEPROM add primary key (MOID);
alter table CnpShelfEEPROM add constraint `FK5A777C25E8B0DDDD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpShelfEEPROM drop column NAME;
# index creation for CnpShelfEEPROM
create index `FK5A777C25E8B0DDDD` on CnpShelfEEPROM(MOID);
# alter table for CnpShmmBMC
SELECT 'STEP 109 : alter CnpShmmBMC table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShmmBMC drop column OWNERNAME;
alter table CnpShmmBMC add column MOID bigint(20) first;
update CnpShmmBMC set moid = (select moid from ManagedObject where CnpShmmBMC.name = ManagedObject.name);
alter table CnpShmmBMC add primary key (MOID);
alter table CnpShmmBMC add constraint `FKF573B57E34CA0CB6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpShmmBMC drop column NAME;
# index creation for CnpShmmBMC
create index `FKF573B57E34CA0CB6` on CnpShmmBMC(MOID);
# alter table for CnpEmsSensor
SELECT 'STEP 110 : alter CnpEmsSensor table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsSensor drop column OWNERNAME;
alter table CnpEmsSensor add column MOID bigint(20) first;
update CnpEmsSensor set moid = (select moid from ManagedObject where CnpEmsSensor.name = ManagedObject.name);
alter table CnpEmsSensor add primary key (MOID);
alter table CnpEmsSensor add constraint `FK660878A0690B7D18` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpEmsSensor drop column NAME;
# index creation for CnpEmsSensor
create index `FK660878A0690B7D18` on CnpEmsSensor(MOID);
# alter table for CnpExternalServer
SELECT 'STEP 111 : alter CnpExternalServer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpExternalServer drop column OWNERNAME;
alter table CnpExternalServer add column MOID bigint(20) first;
update CnpExternalServer set moid = (select moid from ManagedObject where CnpExternalServer.name = ManagedObject.name);
alter table CnpExternalServer add primary key (MOID);
alter table CnpExternalServer add constraint `FK102F3937F629075` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpExternalServer drop column NAME;
# index creation for CnpExternalServer
create index `FK102F3937F629075` on CnpExternalServer(MOID);
# alter table for EnumServerLink
SELECT 'STEP 112 : alter EnumServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EnumServerLink drop column OWNERNAME;
alter table EnumServerLink add column MOID bigint(20) first;
update EnumServerLink set moid = (select moid from ManagedObject where EnumServerLink.name = ManagedObject.name);
alter table EnumServerLink add primary key (MOID);
alter table EnumServerLink add constraint `FK509DA9DE131CB3B8` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EnumServerLink drop column NAME;
# index creation for EnumServerLink
create index `FK509DA9DE131CB3B8` on EnumServerLink(MOID);
# alter table for SubLdapServerLink
SELECT 'STEP 113 : alter SubLdapServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SubLdapServerLink drop column OWNERNAME;
alter table SubLdapServerLink add column MOID bigint(20) first;
update SubLdapServerLink set moid = (select moid from ManagedObject where SubLdapServerLink.name = ManagedObject.name);
alter table SubLdapServerLink add primary key (MOID);
alter table SubLdapServerLink add constraint `FKF61927246A8190A4` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SubLdapServerLink drop column NAME;
# index creation for SubLdapServerLink
create index `FKF61927246A8190A4` on SubLdapServerLink(MOID);
# alter table for PeerMMSCLink
SELECT 'STEP 114 : alter PeerMMSCLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PeerMMSCLink drop column OWNERNAME;
alter table PeerMMSCLink add column MOID bigint(20) first;
update PeerMMSCLink set moid = (select moid from ManagedObject where PeerMMSCLink.name = ManagedObject.name);
alter table PeerMMSCLink add primary key (MOID);
alter table PeerMMSCLink add constraint `FK46A21B8CC61A68A6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PeerMMSCLink drop column NAME;
# index creation for PeerMMSCLink
create index `FK46A21B8CC61A68A6` on PeerMMSCLink(MOID);
# alter table for SmppServerLink
SELECT 'STEP 115 : alter SmppServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmppServerLink drop column OWNERNAME;
alter table SmppServerLink add column MOID bigint(20) first;
update SmppServerLink set moid = (select moid from ManagedObject where SmppServerLink.name = ManagedObject.name);
alter table SmppServerLink add primary key (MOID);
alter table SmppServerLink add constraint `FK5471877716F09151` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmppServerLink drop column NAME;
# index creation for SmppServerLink
create index `FK5471877716F09151` on SmppServerLink(MOID);
# alter table for SmtpServerLink
SELECT 'STEP 116 : alter SmtpServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpServerLink drop column OWNERNAME;
alter table SmtpServerLink add column MOID bigint(20) first;
update SmtpServerLink set moid = (select moid from ManagedObject where SmtpServerLink.name = ManagedObject.name);
alter table SmtpServerLink add primary key (MOID);
alter table SmtpServerLink add constraint `FK73381CF335B726CD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpServerLink drop column NAME;
# index creation for SmtpServerLink
create index `FK73381CF335B726CD` on SmtpServerLink(MOID);
# alter table for TranscoderLink
SELECT 'STEP 117 : alter TranscoderLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TranscoderLink drop column OWNERNAME;
alter table TranscoderLink add column MOID bigint(20) first;
update TranscoderLink set moid = (select moid from ManagedObject where TranscoderLink.name = ManagedObject.name);
alter table TranscoderLink add primary key (MOID);
alter table TranscoderLink add constraint `FKB27DF03774FCFA11` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TranscoderLink drop column NAME;
# index creation for TranscoderLink
create index `FKB27DF03774FCFA11` on TranscoderLink(MOID);
# alter table for VaspLink
SELECT 'STEP 118 : alter VaspLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table VaspLink drop column OWNERNAME;
alter table VaspLink add column MOID bigint(20) first;
update VaspLink set moid = (select moid from ManagedObject where VaspLink.name = ManagedObject.name);
alter table VaspLink add primary key (MOID);
alter table VaspLink add constraint `FKBB6D42825873861C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table VaspLink drop column NAME;
# index creation for VaspLink
create index `FKBB6D42825873861C` on VaspLink(MOID);
# alter table for Mm1Link
SELECT 'STEP 119 : alter Mm1Link table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Mm1Link drop column OWNERNAME;
alter table Mm1Link add column MOID bigint(20) first;
update Mm1Link set moid = (select moid from ManagedObject where Mm1Link.name = ManagedObject.name);
alter table Mm1Link add primary key (MOID);
alter table Mm1Link add constraint `FKA616B20BE71C34B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Mm1Link drop column NAME;
# index creation for Mm1Link
create index `FKA616B20BE71C34B` on Mm1Link(MOID);
# alter table for BackupServerLink
SELECT 'STEP 120 : alter BackupServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BackupServerLink drop column OWNERNAME;
alter table BackupServerLink add column MOID bigint(20) first;
update BackupServerLink set moid = (select moid from ManagedObject where BackupServerLink.name = ManagedObject.name);
alter table BackupServerLink add primary key (MOID);
alter table BackupServerLink add constraint `FKEF7EC55F4E485BF9` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BackupServerLink drop column NAME;
# index creation for BackupServerLink
create index `FKEF7EC55F4E485BF9` on BackupServerLink(MOID);
# alter table for PrepaidServerLink
SELECT 'STEP 121 : alter PrepaidServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PrepaidServerLink drop column OWNERNAME;
alter table PrepaidServerLink add column MOID bigint(20) first;
update PrepaidServerLink set moid = (select moid from ManagedObject where PrepaidServerLink.name = ManagedObject.name);
alter table PrepaidServerLink add primary key (MOID);
alter table PrepaidServerLink add constraint `FKA774430C1BDCAC8C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PrepaidServerLink drop column NAME;
# index creation for PrepaidServerLink
create index `FKA774430C1BDCAC8C` on PrepaidServerLink(MOID);
# alter table for TapLink
SELECT 'STEP 122 : alter TapLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapLink drop column OWNERNAME;
alter table TapLink add column MOID bigint(20) first;
update TapLink set moid = (select moid from ManagedObject where TapLink.name = ManagedObject.name);
alter table TapLink add primary key (MOID);
alter table TapLink add constraint `FK75FF21D6FBB035D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TapLink drop column NAME;
# index creation for TapLink
create index `FK75FF21D6FBB035D` on TapLink(MOID);
# alter table for MateLink
SELECT 'STEP 123 : alter MateLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MateLink drop column OWNERNAME;
alter table MateLink add column MOID bigint(20) first;
update MateLink set moid = (select moid from ManagedObject where MateLink.name = ManagedObject.name);
alter table MateLink add primary key (MOID);
alter table MateLink add constraint `FK159C84BFB2A2C859` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MateLink drop column NAME;
# index creation for MateLink
create index `FK159C84BFB2A2C859` on MateLink(MOID);
# alter table for Ss7PCLink
SELECT 'STEP 124 : alter Ss7PCLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7PCLink drop column OWNERNAME;
alter table Ss7PCLink add column MOID bigint(20) first;
update Ss7PCLink set moid = (select moid from ManagedObject where Ss7PCLink.name = ManagedObject.name);
alter table Ss7PCLink add primary key (MOID);
alter table Ss7PCLink add constraint `FKA625DD04A3E73984` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Ss7PCLink drop column NAME;
# index creation for Ss7PCLink
create index `FKA625DD04A3E73984` on Ss7PCLink(MOID);
# alter table for SmtpClientLink
SELECT 'STEP 125 : alter SmtpClientLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpClientLink drop column OWNERNAME;
alter table SmtpClientLink add column MOID bigint(20) first;
update SmtpClientLink set moid = (select moid from ManagedObject where SmtpClientLink.name = ManagedObject.name);
alter table SmtpClientLink add primary key (MOID);
alter table SmtpClientLink add constraint `FK21C35D7BE4426755` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpClientLink drop column NAME;
# index creation for SmtpClientLink
create index `FK21C35D7BE4426755` on SmtpClientLink(MOID);
# alter table for SmppClientLink
SELECT 'STEP 126 : alter SmppClientLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmppClientLink drop column OWNERNAME;
alter table SmppClientLink add column MOID bigint(20) first;
update SmppClientLink set moid = (select moid from ManagedObject where SmppClientLink.name = ManagedObject.name);
alter table SmppClientLink add primary key (MOID);
alter table SmppClientLink add constraint `FK2FCC7FFC57BD1D9` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmppClientLink drop column NAME;
# index creation for SmppClientLink
create index `FK2FCC7FFC57BD1D9` on SmppClientLink(MOID);
# alter table for Ss7AssociationLink
SELECT 'STEP 127 : alter Ss7AssociationLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7AssociationLink drop column OWNERNAME;
alter table Ss7AssociationLink add column MOID bigint(20) first;
update Ss7AssociationLink set moid = (select moid from ManagedObject where Ss7AssociationLink.name = ManagedObject.name);
alter table Ss7AssociationLink add primary key (MOID);
alter table Ss7AssociationLink add constraint `FKACBD4564BF6138BE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Ss7AssociationLink drop column NAME;
# index creation for Ss7AssociationLink
create index `FKACBD4564BF6138BE` on Ss7AssociationLink(MOID);
# alter table for BillingServerLink
SELECT 'STEP 128 : alter BillingServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BillingServerLink drop column OWNERNAME;
alter table BillingServerLink add column MOID bigint(20) first;
update BillingServerLink set moid = (select moid from ManagedObject where BillingServerLink.name = ManagedObject.name);
alter table BillingServerLink add primary key (MOID);
alter table BillingServerLink add constraint `FK96D09A98B390418` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BillingServerLink drop column NAME;
# index creation for BillingServerLink
create index `FK96D09A98B390418` on BillingServerLink(MOID);
# alter table for MsgArchiveServerLink
SELECT 'STEP 129 : alter MsgArchiveServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgArchiveServerLink drop column OWNERNAME;
alter table MsgArchiveServerLink add column MOID bigint(20) first;
update MsgArchiveServerLink set moid = (select moid from ManagedObject where MsgArchiveServerLink.name = ManagedObject.name);
alter table MsgArchiveServerLink add primary key (MOID);
alter table MsgArchiveServerLink add constraint `FKBDC6CADEF720EAF8` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MsgArchiveServerLink drop column NAME;
# index creation for MsgArchiveServerLink
create index `FKBDC6CADEF720EAF8` on MsgArchiveServerLink(MOID);
# alter table for DnsServerLink
SELECT 'STEP 130 : alter DnsServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table DnsServerLink drop column OWNERNAME;
alter table DnsServerLink add column MOID bigint(20) first;
update DnsServerLink set moid = (select moid from ManagedObject where DnsServerLink.name = ManagedObject.name);
alter table DnsServerLink add primary key (MOID);
alter table DnsServerLink add constraint `FK95D7EBC6FF686EC6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table DnsServerLink drop column NAME;
# index creation for DnsServerLink
create index `FK95D7EBC6FF686EC6` on DnsServerLink(MOID);
# alter table for AckFwdSystemLink
SELECT 'STEP 131 : alter AckFwdSystemLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table AckFwdSystemLink drop column OWNERNAME;
alter table AckFwdSystemLink add column MOID bigint(20) first;
update AckFwdSystemLink set moid = (select moid from ManagedObject where AckFwdSystemLink.name = ManagedObject.name);
alter table AckFwdSystemLink add primary key (MOID);
alter table AckFwdSystemLink add constraint `FKFF73EEF35E3D858D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table AckFwdSystemLink drop column NAME;
# index creation for AckFwdSystemLink
create index `FKFF73EEF35E3D858D` on AckFwdSystemLink(MOID);
# alter table for ExternalIPLink
SELECT 'STEP 132 : alter ExternalIPLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ExternalIPLink drop column OWNERNAME;
alter table ExternalIPLink add column MOID bigint(20) first;
update ExternalIPLink set moid = (select moid from ManagedObject where ExternalIPLink.name = ManagedObject.name);
alter table ExternalIPLink add primary key (MOID);
alter table ExternalIPLink add constraint `FK95982D6C58173746` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ExternalIPLink drop column NAME;
# index creation for ExternalIPLink
create index `FK95982D6C58173746` on ExternalIPLink(MOID);
# alter table for EsmeLink
SELECT 'STEP 133 : alter EsmeLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeLink drop column OWNERNAME;
alter table EsmeLink add column MOID bigint(20) first;
update EsmeLink set moid = (select moid from ManagedObject where EsmeLink.name = ManagedObject.name);
alter table EsmeLink add primary key (MOID);
alter table EsmeLink add constraint `FK82D15C001FD79F9A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EsmeLink drop column NAME;
# index creation for EsmeLink
create index `FK82D15C001FD79F9A` on EsmeLink(MOID);
# alter table for SmscLink
SELECT 'STEP 134 : alter SmscLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscLink drop column OWNERNAME;
alter table SmscLink add column MOID bigint(20) first;
update SmscLink set moid = (select moid from ManagedObject where SmscLink.name = ManagedObject.name);
alter table SmscLink add primary key (MOID);
alter table SmscLink add constraint `FKFDDD51649AE394FE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmscLink drop column NAME;
# index creation for SmscLink
create index `FKFDDD51649AE394FE` on SmscLink(MOID);
# alter table for TapClientLink
SELECT 'STEP 135 : alter TapClientLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapClientLink drop column OWNERNAME;
alter table TapClientLink add column MOID bigint(20) first;
update TapClientLink set moid = (select moid from ManagedObject where TapClientLink.name = ManagedObject.name);
alter table TapClientLink add primary key (MOID);
alter table TapClientLink add constraint `FK9E0164873709948` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TapClientLink drop column NAME;
# index creation for TapClientLink
create index `FK9E0164873709948` on TapClientLink(MOID);
# alter table for XmlClientLink
SELECT 'STEP 136 : alter XmlClientLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlClientLink drop column OWNERNAME;
alter table XmlClientLink add column MOID bigint(20) first;
update XmlClientLink set moid = (select moid from ManagedObject where XmlClientLink.name = ManagedObject.name);
alter table XmlClientLink add primary key (MOID);
alter table XmlClientLink add constraint `FKCCAD25BC363DA8BC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table XmlClientLink drop column NAME;
# index creation for XmlClientLink
create index `FKCCAD25BC363DA8BC` on XmlClientLink(MOID);
# alter table for InterMateLink
SELECT 'STEP 137 : alter InterMateLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table InterMateLink drop column OWNERNAME;
alter table InterMateLink add column MOID bigint(20) first;
update InterMateLink set moid = (select moid from ManagedObject where InterMateLink.name = ManagedObject.name);
alter table InterMateLink add primary key (MOID);
alter table InterMateLink add constraint `FK46627D7BAFF3007B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table InterMateLink drop column NAME;
# index creation for InterMateLink
create index `FK46627D7BAFF3007B` on InterMateLink(MOID);
# alter table for EsmeNetworkConnectionLink
SELECT 'STEP 138 : alter EsmeNetworkConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeNetworkConnectionLink drop column OWNERNAME;
alter table EsmeNetworkConnectionLink add column MOID bigint(20) first;
update EsmeNetworkConnectionLink set moid = (select moid from ManagedObject where EsmeNetworkConnectionLink.name = ManagedObject.name);
alter table EsmeNetworkConnectionLink add primary key (MOID);
alter table EsmeNetworkConnectionLink add constraint `FK555BE280230E5900` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EsmeNetworkConnectionLink drop column NAME;
# index creation for EsmeNetworkConnectionLink
create index `FK555BE280230E5900` on EsmeNetworkConnectionLink(MOID);
# alter table for EsmeQueuedMessageLink
SELECT 'STEP 139 : alter EsmeQueuedMessageLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeQueuedMessageLink drop column OWNERNAME;
alter table EsmeQueuedMessageLink add column MOID bigint(20) first;
update EsmeQueuedMessageLink set moid = (select moid from ManagedObject where EsmeQueuedMessageLink.name = ManagedObject.name);
alter table EsmeQueuedMessageLink add primary key (MOID);
alter table EsmeQueuedMessageLink add constraint `FK27D09EE813B9AEE8` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EsmeQueuedMessageLink drop column NAME;
# index creation for EsmeQueuedMessageLink
create index `FK27D09EE813B9AEE8` on EsmeQueuedMessageLink(MOID);
# alter table for LnpServerLink
SELECT 'STEP 140 : alter LnpServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LnpServerLink drop column OWNERNAME;
alter table LnpServerLink add column MOID bigint(20) first;
update LnpServerLink set moid = (select moid from ManagedObject where LnpServerLink.name = ManagedObject.name);
alter table LnpServerLink add primary key (MOID);
alter table LnpServerLink add constraint `FK4B42078BB4D28A8B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LnpServerLink drop column NAME;
# index creation for LnpServerLink
create index `FK4B42078BB4D28A8B` on LnpServerLink(MOID);
# alter table for EsmeLocalConnectionLink
SELECT 'STEP 141 : alter EsmeLocalConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeLocalConnectionLink drop column OWNERNAME;
alter table EsmeLocalConnectionLink add column MOID bigint(20) first;
update EsmeLocalConnectionLink set moid = (select moid from ManagedObject where EsmeLocalConnectionLink.name = ManagedObject.name);
alter table EsmeLocalConnectionLink add primary key (MOID);
alter table EsmeLocalConnectionLink add constraint `FK19C0DF7DEF8B8ABD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EsmeLocalConnectionLink drop column NAME;
# index creation for EsmeLocalConnectionLink
create index `FK19C0DF7DEF8B8ABD` on EsmeLocalConnectionLink(MOID);
# alter table for SmscLocalConnectionLink
SELECT 'STEP 142 : alter SmscLocalConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscLocalConnectionLink drop column OWNERNAME;
alter table SmscLocalConnectionLink add column MOID bigint(20) first;
update SmscLocalConnectionLink set moid = (select moid from ManagedObject where SmscLocalConnectionLink.name = ManagedObject.name);
alter table SmscLocalConnectionLink add primary key (MOID);
alter table SmscLocalConnectionLink add constraint `FK7DA6F5995371A0D9` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmscLocalConnectionLink drop column NAME;
# index creation for SmscLocalConnectionLink
create index `FK7DA6F5995371A0D9` on SmscLocalConnectionLink(MOID);
# alter table for SmscNetworkConnectionLink
SELECT 'STEP 143 : alter SmscNetworkConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscNetworkConnectionLink drop column OWNERNAME;
alter table SmscNetworkConnectionLink add column MOID bigint(20) first;
update SmscNetworkConnectionLink set moid = (select moid from ManagedObject where SmscNetworkConnectionLink.name = ManagedObject.name);
alter table SmscNetworkConnectionLink add primary key (MOID);
alter table SmscNetworkConnectionLink add constraint `FK5814E19C25C7581C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmscNetworkConnectionLink drop column NAME;
# index creation for SmscNetworkConnectionLink
create index `FK5814E19C25C7581C` on SmscNetworkConnectionLink(MOID);
# alter table for SmscQueuedMessageLink
SELECT 'STEP 144 : alter SmscQueuedMessageLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscQueuedMessageLink drop column OWNERNAME;
alter table SmscQueuedMessageLink add column MOID bigint(20) first;
update SmscQueuedMessageLink set moid = (select moid from ManagedObject where SmscQueuedMessageLink.name = ManagedObject.name);
alter table SmscQueuedMessageLink add primary key (MOID);
alter table SmscQueuedMessageLink add constraint `FKB33D8C049F269C04` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmscQueuedMessageLink drop column NAME;
# index creation for SmscQueuedMessageLink
create index `FKB33D8C049F269C04` on SmscQueuedMessageLink(MOID);
# alter table for TapLocalConnectionLink
SELECT 'STEP 145 : alter TapLocalConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapLocalConnectionLink drop column OWNERNAME;
alter table TapLocalConnectionLink add column MOID bigint(20) first;
update TapLocalConnectionLink set moid = (select moid from ManagedObject where TapLocalConnectionLink.name = ManagedObject.name);
alter table TapLocalConnectionLink add primary key (MOID);
alter table TapLocalConnectionLink add constraint `FK2CF9ABC0B831C89A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TapLocalConnectionLink drop column NAME;
# index creation for TapLocalConnectionLink
create index `FK2CF9ABC0B831C89A` on TapLocalConnectionLink(MOID);
# alter table for TapNetworkConnectionLink
SELECT 'STEP 146 : alter TapNetworkConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapNetworkConnectionLink drop column OWNERNAME;
alter table TapNetworkConnectionLink add column MOID bigint(20) first;
update TapNetworkConnectionLink set moid = (select moid from ManagedObject where TapNetworkConnectionLink.name = ManagedObject.name);
alter table TapNetworkConnectionLink add primary key (MOID);
alter table TapNetworkConnectionLink add constraint `FK7D92AA035B1C939D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table TapNetworkConnectionLink drop column NAME;
# index creation for TapNetworkConnectionLink
create index `FK7D92AA035B1C939D` on TapNetworkConnectionLink(MOID);
# alter table for XmlcLocalConnectionLink
SELECT 'STEP 147 : alter XmlcLocalConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlcLocalConnectionLink drop column OWNERNAME;
alter table XmlcLocalConnectionLink add column MOID bigint(20) first;
update XmlcLocalConnectionLink set moid = (select moid from ManagedObject where XmlcLocalConnectionLink.name = ManagedObject.name);
alter table XmlcLocalConnectionLink add primary key (MOID);
alter table XmlcLocalConnectionLink add constraint `FK98703D576E3AE897` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table XmlcLocalConnectionLink drop column NAME;
# index creation for XmlcLocalConnectionLink
create index `FK98703D576E3AE897` on XmlcLocalConnectionLink(MOID);
# alter table for XmlcNetworkConnectionLink
SELECT 'STEP 148 : alter XmlcNetworkConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlcNetworkConnectionLink drop column OWNERNAME;
alter table XmlcNetworkConnectionLink add column MOID bigint(20) first;
update XmlcNetworkConnectionLink set moid = (select moid from ManagedObject where XmlcNetworkConnectionLink.name = ManagedObject.name);
alter table XmlcNetworkConnectionLink add primary key (MOID);
alter table XmlcNetworkConnectionLink add constraint `FKE5AB31DAB35DA85A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table XmlcNetworkConnectionLink drop column NAME;
# index creation for XmlcNetworkConnectionLink
create index `FKE5AB31DAB35DA85A` on XmlcNetworkConnectionLink(MOID);
# alter table for LoadBalancerLink
SELECT 'STEP 149 : alter LoadBalancerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LoadBalancerLink drop column OWNERNAME;
alter table LoadBalancerLink add column MOID bigint(20) first;
update LoadBalancerLink set moid = (select moid from ManagedObject where LoadBalancerLink.name = ManagedObject.name);
alter table LoadBalancerLink add primary key (MOID);
alter table LoadBalancerLink add constraint `FK1B954A567A5EE0F0` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table LoadBalancerLink drop column NAME;
# index creation for LoadBalancerLink
create index `FK1B954A567A5EE0F0` on LoadBalancerLink(MOID);
# alter table for BmgwServerThrottleLink
SELECT 'STEP 150 : alter BmgwServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwServerThrottleLink drop column OWNERNAME;
alter table BmgwServerThrottleLink add column MOID bigint(20) first;
update BmgwServerThrottleLink set moid = (select moid from ManagedObject where BmgwServerThrottleLink.name = ManagedObject.name);
alter table BmgwServerThrottleLink add primary key (MOID);
alter table BmgwServerThrottleLink add constraint `FK751AD32252EFFC` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BmgwServerThrottleLink drop column NAME;
# index creation for BmgwServerThrottleLink
create index `FK751AD32252EFFC` on BmgwServerThrottleLink(MOID);
# alter table for BmgwServerUnavailableLink
SELECT 'STEP 151 : alter BmgwServerUnavailableLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwServerUnavailableLink drop column OWNERNAME;
alter table BmgwServerUnavailableLink add column MOID bigint(20) first;
update BmgwServerUnavailableLink set moid = (select moid from ManagedObject where BmgwServerUnavailableLink.name = ManagedObject.name);
alter table BmgwServerUnavailableLink add primary key (MOID);
alter table BmgwServerUnavailableLink add constraint `FK58C76CCD33EED4C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BmgwServerUnavailableLink drop column NAME;
# index creation for BmgwServerUnavailableLink
create index `FK58C76CCD33EED4C` on BmgwServerUnavailableLink(MOID);
# alter table for UsageControlServiceLink
SELECT 'STEP 152 : alter UsageControlServiceLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table UsageControlServiceLink drop column OWNERNAME;
alter table UsageControlServiceLink add column MOID bigint(20) first;
update UsageControlServiceLink set moid = (select moid from ManagedObject where UsageControlServiceLink.name = ManagedObject.name);
alter table UsageControlServiceLink add primary key (MOID);
alter table UsageControlServiceLink add constraint `FK6BBF0A134189B553` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table UsageControlServiceLink drop column NAME;
# index creation for UsageControlServiceLink
create index `FK6BBF0A134189B553` on UsageControlServiceLink(MOID);
# alter table for DiskUsage
SELECT 'STEP 153 : alter DiskUsage table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table DiskUsage drop column OWNERNAME;
alter table DiskUsage add column MOID bigint(20) first;
update DiskUsage set moid = (select moid from ManagedObject where DiskUsage.name = ManagedObject.name);
alter table DiskUsage add primary key (MOID);
alter table DiskUsage add constraint `FK7C6F99E47A30F664` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table DiskUsage drop column NAME;
# index creation for DiskUsage
create index `FK7C6F99E47A30F664` on DiskUsage(MOID);
# alter table for FileSystem
SELECT 'STEP 154 : alter FileSystem table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table FileSystem drop column OWNERNAME;
alter table FileSystem add column MOID bigint(20) first;
update FileSystem set moid = (select moid from ManagedObject where FileSystem.name = ManagedObject.name);
alter table FileSystem add primary key (MOID);
alter table FileSystem add constraint `FKE27C22EB96E58345` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table FileSystem drop column NAME;
# index creation for FileSystem
create index `FKE27C22EB96E58345` on FileSystem(MOID);
# alter table for HssServerLink
SELECT 'STEP 155 : alter HssServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerLink drop column OWNERNAME;
alter table HssServerLink add column MOID bigint(20) first;
update HssServerLink set moid = (select moid from ManagedObject where HssServerLink.name = ManagedObject.name);
alter table HssServerLink add primary key (MOID);
alter table HssServerLink add constraint `FK765C40A5DFECC3A5` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table HssServerLink drop column NAME;
# index creation for HssServerLink
create index `FK765C40A5DFECC3A5` on HssServerLink(MOID);
# alter table for HssServerUnavailableConnection
SELECT 'STEP 156 : alter HssServerUnavailableConnection table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerUnavailableConnection drop column OWNERNAME;
alter table HssServerUnavailableConnection add column MOID bigint(20) first;
update HssServerUnavailableConnection set moid = (select moid from ManagedObject where HssServerUnavailableConnection.name = ManagedObject.name);
alter table HssServerUnavailableConnection add primary key (MOID);
alter table HssServerUnavailableConnection add constraint `FKD6908A0375BEB9DD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table HssServerUnavailableConnection drop column NAME;
# index creation for HssServerUnavailableConnection
create index `FKD6908A0375BEB9DD` on HssServerUnavailableConnection(MOID);
# alter table for ImsGsmGwServerLink
SELECT 'STEP 157 : alter ImsGsmGwServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwServerLink drop column OWNERNAME;
alter table ImsGsmGwServerLink add column MOID bigint(20) first;
update ImsGsmGwServerLink set moid = (select moid from ManagedObject where ImsGsmGwServerLink.name = ManagedObject.name);
alter table ImsGsmGwServerLink add primary key (MOID);
alter table ImsGsmGwServerLink add constraint `FKD4646DF1FEA3A39` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ImsGsmGwServerLink drop column NAME;
# index creation for ImsGsmGwServerLink
create index `FKD4646DF1FEA3A39` on ImsGsmGwServerLink(MOID);
# alter table for ImsGsmGwServerThrottleLink
SELECT 'STEP 158 : alter ImsGsmGwServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwServerThrottleLink drop column OWNERNAME;
alter table ImsGsmGwServerThrottleLink add column MOID bigint(20) first;
update ImsGsmGwServerThrottleLink set moid = (select moid from ManagedObject where ImsGsmGwServerThrottleLink.name = ManagedObject.name);
alter table ImsGsmGwServerThrottleLink add primary key (MOID);
alter table ImsGsmGwServerThrottleLink add constraint `FKD876EC29BB127283` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ImsGsmGwServerThrottleLink drop column NAME;
# index creation for ImsGsmGwServerThrottleLink
create index `FKD876EC29BB127283` on ImsGsmGwServerThrottleLink(MOID);
# alter table for SCSCFServerThrottleLink
SELECT 'STEP 159 : alter SCSCFServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SCSCFServerThrottleLink drop column OWNERNAME;
alter table SCSCFServerThrottleLink add column MOID bigint(20) first;
update SCSCFServerThrottleLink set moid = (select moid from ManagedObject where SCSCFServerThrottleLink.name = ManagedObject.name);
alter table SCSCFServerThrottleLink add primary key (MOID);
alter table SCSCFServerThrottleLink add constraint `FKD1CD736DA7981EAD` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SCSCFServerThrottleLink drop column NAME;
# index creation for SCSCFServerThrottleLink
create index `FKD1CD736DA7981EAD` on SCSCFServerThrottleLink(MOID);
# alter table for HssServerThrottleLink
SELECT 'STEP 160 : alter HssServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerThrottleLink drop column OWNERNAME;
alter table HssServerThrottleLink add column MOID bigint(20) first;
update HssServerThrottleLink set moid = (select moid from ManagedObject where HssServerThrottleLink.name = ManagedObject.name);
alter table HssServerThrottleLink add primary key (MOID);
alter table HssServerThrottleLink add constraint `FK9971BFEF855ACFEF` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table HssServerThrottleLink drop column NAME;
# index creation for HssServerThrottleLink
create index `FK9971BFEF855ACFEF` on HssServerThrottleLink(MOID);
# alter table for ContentAdaptationLink
SELECT 'STEP 161 : alter ContentAdaptationLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ContentAdaptationLink drop column OWNERNAME;
alter table ContentAdaptationLink add column MOID bigint(20) first;
update ContentAdaptationLink set moid = (select moid from ManagedObject where ContentAdaptationLink.name = ManagedObject.name);
alter table ContentAdaptationLink add primary key (MOID);
alter table ContentAdaptationLink add constraint `FK9DC7746689B08466` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ContentAdaptationLink drop column NAME;
# index creation for ContentAdaptationLink
create index `FK9DC7746689B08466` on ContentAdaptationLink(MOID);
# alter table for SmtpLink
SELECT 'STEP 162 : alter SmtpLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpLink drop column OWNERNAME;
alter table SmtpLink add column MOID bigint(20) first;
update SmtpLink set moid = (select moid from ManagedObject where SmtpLink.name = ManagedObject.name);
alter table SmtpLink add primary key (MOID);
alter table SmtpLink add constraint `FK495B909D4F9F2A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpLink drop column NAME;
# index creation for SmtpLink
create index `FK495B909D4F9F2A` on SmtpLink(MOID);
# alter table for SmtpLocalConnectionLink
SELECT 'STEP 163 : alter SmtpLocalConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpLocalConnectionLink drop column OWNERNAME;
alter table SmtpLocalConnectionLink add column MOID bigint(20) first;
update SmtpLocalConnectionLink set moid = (select moid from ManagedObject where SmtpLocalConnectionLink.name = ManagedObject.name);
alter table SmtpLocalConnectionLink add primary key (MOID);
alter table SmtpLocalConnectionLink add constraint `FK4D77CDED2342792D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpLocalConnectionLink drop column NAME;
# index creation for SmtpLocalConnectionLink
create index `FK4D77CDED2342792D` on SmtpLocalConnectionLink(MOID);
# alter table for SmtpNetworkConnectionLink
SELECT 'STEP 164 : alter SmtpNetworkConnectionLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpNetworkConnectionLink drop column OWNERNAME;
alter table SmtpNetworkConnectionLink add column MOID bigint(20) first;
update SmtpNetworkConnectionLink set moid = (select moid from ManagedObject where SmtpNetworkConnectionLink.name = ManagedObject.name);
alter table SmtpNetworkConnectionLink add primary key (MOID);
alter table SmtpNetworkConnectionLink add constraint `FK7710F4F044C36B70` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpNetworkConnectionLink drop column NAME;
# index creation for SmtpNetworkConnectionLink
create index `FK7710F4F044C36B70` on SmtpNetworkConnectionLink(MOID);
# alter table for SmtpQueuedMessageLink
SELECT 'STEP 165 : alter SmtpQueuedMessageLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpQueuedMessageLink drop column OWNERNAME;
alter table SmtpQueuedMessageLink add column MOID bigint(20) first;
update SmtpQueuedMessageLink set moid = (select moid from ManagedObject where SmtpQueuedMessageLink.name = ManagedObject.name);
alter table SmtpQueuedMessageLink add primary key (MOID);
alter table SmtpQueuedMessageLink add constraint `FK7C50695868397958` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SmtpQueuedMessageLink drop column NAME;
# index creation for SmtpQueuedMessageLink
create index `FK7C50695868397958` on SmtpQueuedMessageLink(MOID);
# alter table for StatSizeAuditLink
SELECT 'STEP 166 : alter StatSizeAuditLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table StatSizeAuditLink drop column OWNERNAME;
alter table StatSizeAuditLink add column MOID bigint(20) first;
update StatSizeAuditLink set moid = (select moid from ManagedObject where StatSizeAuditLink.name = ManagedObject.name);
alter table StatSizeAuditLink add primary key (MOID);
alter table StatSizeAuditLink add constraint `FK17095D608B71C6E0` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table StatSizeAuditLink drop column NAME;
# index creation for StatSizeAuditLink
create index `FK17095D608B71C6E0` on StatSizeAuditLink(MOID);
# alter table for CapacityLicenseLink
SELECT 'STEP 167 : alter CapacityLicenseLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CapacityLicenseLink drop column OWNERNAME;
alter table CapacityLicenseLink add column MOID bigint(20) first;
update CapacityLicenseLink set moid = (select moid from ManagedObject where CapacityLicenseLink.name = ManagedObject.name);
alter table CapacityLicenseLink add primary key (MOID);
alter table CapacityLicenseLink add constraint `FK68CCA2A1A4A64761` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CapacityLicenseLink drop column NAME;
# index creation for CapacityLicenseLink
create index `FK68CCA2A1A4A64761` on CapacityLicenseLink(MOID);
# alter table for PersonalizationConfig
SELECT 'STEP 168 : alter PersonalizationConfig table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PersonalizationConfig drop column OWNERNAME;
alter table PersonalizationConfig add column MOID bigint(20) first;
update PersonalizationConfig set moid = (select moid from ManagedObject where PersonalizationConfig.name = ManagedObject.name);
alter table PersonalizationConfig add primary key (MOID);
alter table PersonalizationConfig add constraint `FK1AB2AA6669BBA66` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PersonalizationConfig drop column NAME;
# index creation for PersonalizationConfig
create index `FK1AB2AA6669BBA66` on PersonalizationConfig(MOID);
# alter table for SpamServerLink
SELECT 'STEP 169 : alter SpamServerLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerLink drop column OWNERNAME;
alter table SpamServerLink add column MOID bigint(20) first;
update SpamServerLink set moid = (select moid from ManagedObject where SpamServerLink.name = ManagedObject.name);
alter table SpamServerLink add primary key (MOID);
alter table SpamServerLink add constraint `FKEDE3D226B062DC00` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SpamServerLink drop column NAME;
# index creation for SpamServerLink
create index `FKEDE3D226B062DC00` on SpamServerLink(MOID);
# alter table for SpamServerThrottleLink
SELECT 'STEP 170 : alter SpamServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerThrottleLink drop column OWNERNAME;
alter table SpamServerThrottleLink add column MOID bigint(20) first;
update SpamServerThrottleLink set moid = (select moid from ManagedObject where SpamServerThrottleLink.name = ManagedObject.name);
alter table SpamServerThrottleLink add primary key (MOID);
alter table SpamServerThrottleLink add constraint `FK135440709E8C5D4A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SpamServerThrottleLink drop column NAME;
# index creation for SpamServerThrottleLink
create index `FK135440709E8C5D4A` on SpamServerThrottleLink(MOID);
# alter table for SpamServerUnavailableConnection
SELECT 'STEP 171 : alter SpamServerUnavailableConnection table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerUnavailableConnection drop column OWNERNAME;
alter table SpamServerUnavailableConnection add column MOID bigint(20) first;
update SpamServerUnavailableConnection set moid = (select moid from ManagedObject where SpamServerUnavailableConnection.name = ManagedObject.name);
alter table SpamServerUnavailableConnection add primary key (MOID);
alter table SpamServerUnavailableConnection add constraint `FK2C800AA26D1702E2` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SpamServerUnavailableConnection drop column NAME;
# index creation for SpamServerUnavailableConnection
create index `FK2C800AA26D1702E2` on SpamServerUnavailableConnection(MOID);
# alter table for PrepaidServerThrottleLink
SELECT 'STEP 172 : alter PrepaidServerThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PrepaidServerThrottleLink drop column OWNERNAME;
alter table PrepaidServerThrottleLink add column MOID bigint(20) first;
update PrepaidServerThrottleLink set moid = (select moid from ManagedObject where PrepaidServerThrottleLink.name = ManagedObject.name);
alter table PrepaidServerThrottleLink add primary key (MOID);
alter table PrepaidServerThrottleLink add constraint `FK62F06B5630A2E1D6` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table PrepaidServerThrottleLink drop column NAME;
# index creation for PrepaidServerThrottleLink
create index `FK62F06B5630A2E1D6` on PrepaidServerThrottleLink(MOID);
# alter table for M2paLink
SELECT 'STEP 173 : alter M2paLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paLink drop column OWNERNAME;
alter table M2paLink add column MOID bigint(20) first;
update M2paLink set moid = (select moid from ManagedObject where M2paLink.name = ManagedObject.name);
alter table M2paLink add primary key (MOID);
alter table M2paLink add constraint `FK584BE350F55226EA` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table M2paLink drop column NAME;
# index creation for M2paLink
create index `FK584BE350F55226EA` on M2paLink(MOID);
# alter table for M2paLogicalUnit
SELECT 'STEP 174 : alter M2paLogicalUnit table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paLogicalUnit drop column OWNERNAME;
alter table M2paLogicalUnit add column MOID bigint(20) first;
update M2paLogicalUnit set moid = (select moid from ManagedObject where M2paLogicalUnit.name = ManagedObject.name);
alter table M2paLogicalUnit add primary key (MOID);
alter table M2paLogicalUnit add constraint `FKEB3D3D17729E9B57` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table M2paLogicalUnit drop column NAME;
# index creation for M2paLogicalUnit
create index `FKEB3D3D17729E9B57` on M2paLogicalUnit(MOID);
# alter table for M2paSystemInfo
SELECT 'STEP 175 : alter M2paSystemInfo table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paSystemInfo drop column OWNERNAME;
alter table M2paSystemInfo add column MOID bigint(20) first;
update M2paSystemInfo set moid = (select moid from ManagedObject where M2paSystemInfo.name = ManagedObject.name);
alter table M2paSystemInfo add primary key (MOID);
alter table M2paSystemInfo add constraint `FKEACB7653AD4A802D` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table M2paSystemInfo drop column NAME;
# index creation for M2paSystemInfo
create index `FKEACB7653AD4A802D` on M2paSystemInfo(MOID);
# alter table for GeoRedSMSReplLink
SELECT 'STEP 176 : alter GeoRedSMSReplLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table GeoRedSMSReplLink drop column OWNERNAME;
alter table GeoRedSMSReplLink add column MOID bigint(20) first;
update GeoRedSMSReplLink set moid = (select moid from ManagedObject where GeoRedSMSReplLink.name = ManagedObject.name);
alter table GeoRedSMSReplLink add primary key (MOID);
alter table GeoRedSMSReplLink add constraint `FK848478C2F8ECE242` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table GeoRedSMSReplLink drop column NAME;
# index creation for GeoRedSMSReplLink
create index `FK848478C2F8ECE242` on GeoRedSMSReplLink(MOID);
# alter table for GeoRedSMSReplThrottleLink
SELECT 'STEP 177 : alter GeoRedSMSReplThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table GeoRedSMSReplThrottleLink drop column OWNERNAME;
alter table GeoRedSMSReplThrottleLink add column MOID bigint(20) first;
update GeoRedSMSReplThrottleLink set moid = (select moid from ManagedObject where GeoRedSMSReplThrottleLink.name = ManagedObject.name);
alter table GeoRedSMSReplThrottleLink add primary key (MOID);
alter table GeoRedSMSReplThrottleLink add constraint `FKC3A28B0C9155018C` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table GeoRedSMSReplThrottleLink drop column NAME;
# index creation for GeoRedSMSReplThrottleLink
create index `FKC3A28B0C9155018C` on GeoRedSMSReplThrottleLink(MOID);
# alter table for RemoteSMSCThrottleLink
SELECT 'STEP 178 : alter RemoteSMSCThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table RemoteSMSCThrottleLink drop column OWNERNAME;
alter table RemoteSMSCThrottleLink add column MOID bigint(20) first;
update RemoteSMSCThrottleLink set moid = (select moid from ManagedObject where RemoteSMSCThrottleLink.name = ManagedObject.name);
alter table RemoteSMSCThrottleLink add primary key (MOID);
alter table RemoteSMSCThrottleLink add constraint `FK6F80ADF4FAB8CACE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table RemoteSMSCThrottleLink drop column NAME;
# index creation for RemoteSMSCThrottleLink
create index `FK6F80ADF4FAB8CACE` on RemoteSMSCThrottleLink(MOID);
# alter table for MsmMemUtilization
SELECT 'STEP 179 : alter MsmMemUtilization table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsmMemUtilization drop primary key;
alter table MsmMemUtilization drop column OWNERNAME;
alter table MsmMemUtilization add column MOID bigint(20) first;
update MsmMemUtilization set moid = (select moid from ManagedObject where MsmMemUtilization.name = ManagedObject.name);
alter table MsmMemUtilization add primary key (MOID);
alter table MsmMemUtilization add constraint `FK76872454EAEF8DD4` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MsmMemUtilization drop column NAME;
# index creation for MsmMemUtilization
create index `FK76872454EAEF8DD4` on MsmMemUtilization(MOID);
# alter table for MsgBladeLU
SELECT 'STEP 180 : alter MsgBladeLU table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgBladeLU drop column OWNERNAME;
alter table MsgBladeLU add column MOID bigint(20) first;
update MsgBladeLU set moid = (select moid from ManagedObject where MsgBladeLU.name = ManagedObject.name);
alter table MsgBladeLU add primary key (MOID);
alter table MsgBladeLU add constraint `FK419A7D0077B3E8A` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MsgBladeLU drop column NAME;
# index creation for MsgBladeLU
create index `FK419A7D0077B3E8A` on MsgBladeLU(MOID);
# alter table for MsgBladeLUContainer
SELECT 'STEP 181 : alter MsgBladeLUContainer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgBladeLUContainer drop column OWNERNAME;
alter table MsgBladeLUContainer add column MOID bigint(20) first;
update MsgBladeLUContainer set moid = (select moid from ManagedObject where MsgBladeLUContainer.name = ManagedObject.name);
alter table MsgBladeLUContainer add primary key (MOID);
alter table MsgBladeLUContainer add constraint `FK26D58F61A8BA28F1` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MsgBladeLUContainer drop column NAME;
# index creation for MsgBladeLUContainer
create index `FK26D58F61A8BA28F1` on MsgBladeLUContainer(MOID);
# alter table for ClassOfService
SELECT 'STEP 182 : alter ClassOfService table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ClassOfService drop column OWNERNAME;
alter table ClassOfService add column MOID bigint(20) first;
update ClassOfService set moid = (select moid from ManagedObject where ClassOfService.name = ManagedObject.name);
alter table ClassOfService add primary key (MOID);
alter table ClassOfService add constraint `FK44AECA66DEB11D70` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ClassOfService drop column NAME;
# index creation for ClassOfService
create index `FK44AECA66DEB11D70` on ClassOfService(MOID);
# alter table for EnumServiceLink
SELECT 'STEP 183 : alter EnumServiceLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EnumServiceLink drop column OWNERNAME;
alter table EnumServiceLink add column MOID bigint(20) first;
update EnumServiceLink set moid = (select moid from ManagedObject where EnumServiceLink.name = ManagedObject.name);
alter table EnumServiceLink add primary key (MOID);
alter table EnumServiceLink add constraint `FK7E6AD86E1EB2137E` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table EnumServiceLink drop column NAME;
# index creation for EnumServiceLink
create index `FK7E6AD86E1EB2137E` on EnumServiceLink(MOID);
# alter table for Ss7Service
SELECT 'STEP 184 : alter Ss7Service table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7Service drop column OWNERNAME;
alter table Ss7Service add column MOID bigint(20) first;
update Ss7Service set moid = (select moid from ManagedObject where Ss7Service.name = ManagedObject.name);
alter table Ss7Service add primary key (MOID);
alter table Ss7Service add constraint `FKF96A8CFEBF4B4E88` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Ss7Service drop column NAME;
# index creation for Ss7Service
create index `FKF96A8CFEBF4B4E88` on Ss7Service(MOID);
# alter table for Congestion
SELECT 'STEP 185 : alter Congestion table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Congestion drop column OWNERNAME;
alter table Congestion add column MOID bigint(20) first;
update Congestion set moid = (select moid from ManagedObject where Congestion.name = ManagedObject.name);
alter table Congestion add primary key (MOID);
alter table Congestion add constraint `FKD72C34479D0CF5D1` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table Congestion drop column NAME;
# index creation for Congestion
create index `FKD72C34479D0CF5D1` on Congestion(MOID);
# alter table for MessagingSystem
SELECT 'STEP 186 : alter MessagingSystem table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MessagingSystem drop column OWNERNAME;
alter table MessagingSystem add column MOID bigint(20) first;
update MessagingSystem set moid = (select moid from ManagedObject where MessagingSystem.name = ManagedObject.name);
alter table MessagingSystem add primary key (MOID);
alter table MessagingSystem add constraint `FKC152FA33619A3543` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MessagingSystem drop column NAME;
# index creation for MessagingSystem
create index `FKC152FA33619A3543` on MessagingSystem(MOID);
# alter table for StatisticsThreshold
SELECT 'STEP 187 : alter StatisticsThreshold table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table StatisticsThreshold drop column OWNERNAME;
alter table StatisticsThreshold add column MOID bigint(20) first;
update StatisticsThreshold set moid = (select moid from ManagedObject where StatisticsThreshold.name = ManagedObject.name);
alter table StatisticsThreshold add primary key (MOID);
alter table StatisticsThreshold add constraint `FK806CB2682514BF8` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table StatisticsThreshold drop column NAME;
# index creation for StatisticsThreshold
create index `FK806CB2682514BF8` on StatisticsThreshold(MOID);
# alter table for QueueOverflow
SELECT 'STEP 188 : alter QueueOverflow table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table QueueOverflow drop column OWNERNAME;
alter table QueueOverflow add column MOID bigint(20) first;
update QueueOverflow set moid = (select moid from ManagedObject where QueueOverflow.name = ManagedObject.name);
alter table QueueOverflow add primary key (MOID);
alter table QueueOverflow add constraint `FK1DCC013FEC413E3` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table QueueOverflow drop column NAME;
# index creation for QueueOverflow
create index `FK1DCC013FEC413E3` on QueueOverflow(MOID);
# alter table for BmgwClientThrottleLink
SELECT 'STEP 189 : alter BmgwClientThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwClientThrottleLink drop column OWNERNAME;
alter table BmgwClientThrottleLink add column MOID bigint(20) first;
update BmgwClientThrottleLink set moid = (select moid from ManagedObject where BmgwClientThrottleLink.name = ManagedObject.name);
alter table BmgwClientThrottleLink add primary key (MOID);
alter table BmgwClientThrottleLink add constraint `FK48C10BAAC8F841B4` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table BmgwClientThrottleLink drop column NAME;
# index creation for BmgwClientThrottleLink
create index `FK48C10BAAC8F841B4` on BmgwClientThrottleLink(MOID);
# alter table for ImsGsmGwClientThrottleLink
SELECT 'STEP 190 : alter ImsGsmGwClientThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwClientThrottleLink drop column OWNERNAME;
alter table ImsGsmGwClientThrottleLink add column MOID bigint(20) first;
update ImsGsmGwClientThrottleLink set moid = (select moid from ManagedObject where ImsGsmGwClientThrottleLink.name = ManagedObject.name);
alter table ImsGsmGwClientThrottleLink add primary key (MOID);
alter table ImsGsmGwClientThrottleLink add constraint `FKAC1D24B14F27AC3B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table ImsGsmGwClientThrottleLink drop column NAME;
# index creation for ImsGsmGwClientThrottleLink
create index `FKAC1D24B14F27AC3B` on ImsGsmGwClientThrottleLink(MOID);
# alter table for SCSCFClientThrottleLink
SELECT 'STEP 191 : alter SCSCFClientThrottleLink table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SCSCFClientThrottleLink drop column OWNERNAME;
alter table SCSCFClientThrottleLink add column MOID bigint(20) first;
update SCSCFClientThrottleLink set moid = (select moid from ManagedObject where SCSCFClientThrottleLink.name = ManagedObject.name);
alter table SCSCFClientThrottleLink add primary key (MOID);
alter table SCSCFClientThrottleLink add constraint `FKA573ABF526226405` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table SCSCFClientThrottleLink drop column NAME;
# index creation for SCSCFClientThrottleLink
create index `FKA573ABF526226405` on SCSCFClientThrottleLink(MOID);
# alter table for MpSystemConfig
SELECT 'STEP 192 : alter MpSystemConfig table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table MpSystemConfig drop column OWNERNAME;
alter table MpSystemConfig add column MOID bigint(20) first;
update MpSystemConfig set moid = (select moid from ManagedObject where MpSystemConfig.name = ManagedObject.name);
alter table MpSystemConfig add primary key (MOID);
alter table MpSystemConfig add constraint `FKE3C13B44C4BC5CE` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table MpSystemConfig drop column NAME;
# index creation for MpSystemConfig
create index `FKE3C13B44C4BC5CE` on MpSystemConfig(MOID);
# alter table for CnpPSBlackBoxLS
SELECT 'STEP 193 : alter CnpPSBlackBoxLS table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSBlackBoxLS drop column OWNERNAME;
alter table CnpPSBlackBoxLS add column MOID bigint(20) first;
update CnpPSBlackBoxLS set moid = (select moid from ManagedObject where CnpPSBlackBoxLS.name = ManagedObject.name);
alter table CnpPSBlackBoxLS add primary key (MOID);
alter table CnpPSBlackBoxLS add constraint `FKAC4BAFFBBA405086` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPSBlackBoxLS drop column NAME;
# index creation for CnpPSBlackBoxLS
create index `FKAC4BAFFBBA405086` on CnpPSBlackBoxLS(MOID);
# alter table for CnpPSCoreFileMgmtLS
SELECT 'STEP 194 : alter CnpPSCoreFileMgmtLS table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSCoreFileMgmtLS drop column OWNERNAME;
alter table CnpPSCoreFileMgmtLS add column MOID bigint(20) first;
update CnpPSCoreFileMgmtLS set moid = (select moid from ManagedObject where CnpPSCoreFileMgmtLS.name = ManagedObject.name);
alter table CnpPSCoreFileMgmtLS add primary key (MOID);
alter table CnpPSCoreFileMgmtLS add constraint `FKBEC113CBA5DDDD56` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPSCoreFileMgmtLS drop column NAME;
# index creation for CnpPSCoreFileMgmtLS
create index `FKBEC113CBA5DDDD56` on CnpPSCoreFileMgmtLS(MOID);
# alter table for CnpPayloadServer
SELECT 'STEP 195 : alter CnpPayloadServer table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPayloadServer drop column OWNERNAME;
alter table CnpPayloadServer add column MOID bigint(20) first;
update CnpPayloadServer set moid = (select moid from ManagedObject where CnpPayloadServer.name = ManagedObject.name);
alter table CnpPayloadServer add primary key (MOID);
alter table CnpPayloadServer add constraint `FK3C27392CD5B95AE4` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPayloadServer drop column NAME;
# index creation for CnpPayloadServer
create index `FK3C27392CD5B95AE4` on CnpPayloadServer(MOID);
# alter table for CnpPayloadServerPG
SELECT 'STEP 196 : alter CnpPayloadServerPG table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPayloadServerPG drop column OWNERNAME;
alter table CnpPayloadServerPG add column MOID bigint(20) first;
update CnpPayloadServerPG set moid = (select moid from ManagedObject where CnpPayloadServerPG.name = ManagedObject.name);
alter table CnpPayloadServerPG add primary key (MOID);
alter table CnpPayloadServerPG add constraint `FKCF3DA8238CB3D71B` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPayloadServerPG drop column NAME;
# index creation for CnpPayloadServerPG
create index `FKCF3DA8238CB3D71B` on CnpPayloadServerPG(MOID);
# alter table for CnpPSBlackBoxDC
SELECT 'STEP 197 : alter CnpPSBlackBoxDC table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSBlackBoxDC drop column OWNERNAME;
alter table CnpPSBlackBoxDC add column MOID bigint(20) first;
update CnpPSBlackBoxDC set moid = (select moid from ManagedObject where CnpPSBlackBoxDC.name = ManagedObject.name);
alter table CnpPSBlackBoxDC add primary key (MOID);
alter table CnpPSBlackBoxDC add constraint `FKAC4BAEF3CA37A655` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
alter table CnpPSBlackBoxDC drop column NAME;
# index creation for CnpPSBlackBoxDC
create index `FKAC4BAEF3CA37A655` on CnpPSBlackBoxDC(MOID);
# alter table for TOPOUSERPROPS
SELECT 'STEP 198 : alter TOPOUSERPROPS table to change pk and populate MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table TOPOUSERPROPS add column `MOID` bigint(20) NOT NULL first;
update TOPOUSERPROPS set moid = (select moid from ManagedObject where TOPOUSERPROPS.name = ManagedObject.name);
alter table TOPOUSERPROPS drop column NAME;
alter table TOPOUSERPROPS drop column OWNERNAME;
alter table TOPOUSERPROPS add primary key (MOID,PROPNAME);
alter table TOPOUSERPROPS add constraint `FK483583EBFBE5F355` FOREIGN KEY (`MOID`) REFERENCES `ManagedObject` (`MOID`) ON DELETE CASCADE;
# index creation for TOPOUSERPROPS
create index `FK483583EBFBE5F355` on TOPOUSERPROPS(MOID);
# alter table for ObjectSchedulerRUNNABLE
SELECT 'STEP 199 : alter ObjectSchedulerRUNNABLE table to add CLASSNAME & MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ObjectSchedulerRUNNABLE drop primary key;
alter table ObjectSchedulerRUNNABLE add column CLASSNAME varchar(150) NOT NULL after TIMEVAL;
alter table ObjectSchedulerRUNNABLE add column MOID bigint(20) NOT NULL first;
update ObjectSchedulerRUNNABLE set MOID = (select MOID from ManagedObject where ManagedObject.name = ObjectSchedulerRUNNABLE.VALUESTRING), CLASSNAME = (select CLASSNAME from ManagedObject where ManagedObject.name = ObjectSchedulerRUNNABLE.VALUESTRING);
#alter table ObjectSchedulerRUNNABLE add primary key (MOID);
alter table ObjectSchedulerRUNNABLE drop column VALUESTRING;
# index creation for ObjectSchedulerRUNNABLE
create index `ObjectSchedulerRUNNABLE1_ndx` on ObjectSchedulerRUNNABLE(MOID);
# drop columns from GroupTable
SELECT 'STEP 200 : drop OWNERNAME and MEMBEROWNERNAME columns in GroupTable table' AS 'MIGRATION PROCESS STATUS ... ';
alter table GroupTable drop column OWNERNAME;
alter table GroupTable drop column MEMBEROWNERNAME;
alter table GroupTable add primary key (MEMBERNAME,NAME);
# drop tables for Fault Management module
SELECT 'STEP 201 : drop redundant tables in Fault Management module' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists EVENTLOGGER;
drop table if exists EventAlertFilter;
drop table if exists GenericFaultTable;
drop table if exists TrapEventParser;
drop table if exists TrapFilter;
drop table if exists AgentDefValObject;
drop table if exists ALERTLOGGER;
# alter table for Event
SELECT 'STEP 202 : alter Event table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Event add column DISCRIMINATOR varchar(30) NOT NULL default 'Event' after `ID`, drop column DDOMAIN, drop column HELPURL, drop column OWNERNAME;
update Event set DISCRIMINATOR = 'Event' where DISCRIMINATOR = '';
# drop index for Event table
alter table Event drop index Event0_ndx;
# drop table if exists DBEVENT
SELECT 'STEP 203 : drop DBEVENT table' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists DBEVENT;
# alter table for CNEOMIManagementEvent
SELECT 'STEP 204 : alter CNEOMIManagementEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CNEOMIManagementEvent drop column OWNERNAME;
alter table CNEOMIManagementEvent add constraint `FK7A783126CD9C739B` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for CNEOMIManagementEvent
#create index `FK7A783126CD9C739B` on CNEOMIManagementEvent(ID);
# alter table for EMHEvent
SELECT 'STEP 205 : alter EMHEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EMHEvent drop column OWNERNAME;
alter table EMHEvent add constraint `FK67D159DA66FF875B` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for EMHEvent
#create index `FK67D159DA66FF875B` on EMHEvent(ID);
# alter table for EMHInfoEvent
SELECT 'STEP 206 : alter EMHInfoEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EMHInfoEvent drop column OWNERNAME;
alter table EMHInfoEvent add constraint `FK4FC121CCD07CAC4D` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for EMHInfoEvent
#create index `FK4FC121CCD07CAC4D` on EMHInfoEvent(ID);
# alter table for ObjectCreationDeletionEvent
SELECT 'STEP 207 : alter ObjectCreationDeletionEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ObjectCreationDeletionEvent drop column OWNERNAME;
alter table ObjectCreationDeletionEvent add constraint `FK69B254AE150BABA3` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for ObjectCreationDeletionEvent
#create index `FK69B254AE150BABA3` on ObjectCreationDeletionEvent(ID);
# alter table for StateChangeEvent
SELECT 'STEP 208 : alter StateChangeEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table StateChangeEvent drop column OWNERNAME;
alter table StateChangeEvent add constraint `FK8A948F991CE6F71A` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for StateChangeEvent
#create index `FK8A948F991CE6F71A` on StateChangeEvent(ID);
# alter table for UserDefinedEvent
SELECT 'STEP 209 : alter UserDefinedEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table UserDefinedEvent drop column OWNERNAME;
alter table UserDefinedEvent add constraint `FKB4D6AD9C4729151D` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for UserDefinedEvent
#create index `FKB4D6AD9C4729151D` on UserDefinedEvent(ID);
# alter table for AttributeValueChangeEvent
SELECT 'STEP 210 : alter AttributeValueChangeEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table AttributeValueChangeEvent drop column OWNERNAME;
alter table AttributeValueChangeEvent add constraint `FK28B9493539AF4EAA` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for AttributeValueChangeEvent
#create index `FK28B9493539AF4EAA` on AttributeValueChangeEvent(ID);
# alter table for AvailabilityEvent
SELECT 'STEP 211 : alter AvailabilityEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table AvailabilityEvent drop column OWNERNAME;
alter table AvailabilityEvent add constraint `FKD2C5CDDF8ABECD54` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for AvailabilityEvent
#create index `FKD2C5CDDF8ABECD54` on AvailabilityEvent(ID);
# alter table for RelationshipChangeEvent
SELECT 'STEP 212 : alter RelationshipChangeEvent table to change pk and populate ID' AS 'MIGRATION PROCESS STATUS ... ';
alter table RelationshipChangeEvent drop column OWNERNAME;
alter table RelationshipChangeEvent add constraint `FKF35DB352E4A0747` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
# index creation for RelationshipChangeEvent
#create index `FKF35DB352E4A0747` on RelationshipChangeEvent(ID);
# alter table for EVENTUSERPROPS
SELECT 'STEP 213 : alter EVENTUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table EVENTUSERPROPS drop column OWNERNAME;
alter table EVENTUSERPROPS add column `ID` int(11) NOT NULL first;
update EVENTUSERPROPS set ID = NAME;
#alter table EVENTUSERPROPS add constraint `FK54B8432B87584B8F` FOREIGN KEY (`ID`) REFERENCES `Event` (`ID`) ON DELETE CASCADE;
alter table EVENTUSERPROPS drop column NAME;
alter table EVENTUSERPROPS add primary key (ID,PROPNAME);
# create index for EVENTUSERPROPS table
#create index `FK54B8432B87584B8F` on EVENTUSERPROPS(ID);
#To AuthAudit table and rename AuthAuditExt table as AuthAudit
SELECT 'STEP 214 : drop AuthAudit table and rename AuthAuditExt to AuthAudit' AS 'MIGRATION PROCESS STATUS ... ';
drop table if exists AuthAudit;
alter table AuthAuditExt rename to AuthAudit;
#To dump data in PanelProps table to PANELPROPS column in PanelTree table
SELECT 'STEP 215 : Move content of PanelProps table to PanelTree.PANELPROPS and drop PanelProps table' AS 'MIGRATION PROCESS STATUS ... ';
alter table PanelTree add column `PANELPROPS` varchar(1000) after `MODULENAME`;
CREATE TABLE `DUMMY` (`NODEID` varchar(100) NOT NULL, `USERNAME` varchar(50) NOT NULL,`PANELPROPS` varchar(1000) default NULL);
insert into DUMMY(NODEID,USERNAME,PANELPROPS) select NODEID,USERNAME,GROUP_CONCAT('"',ATTRIBNAME, '":"', ATTRIBVALUE,'"') from PanelProps group by(concat(NODEID,'   ',USERNAME));
update PanelTree set PANELPROPS = (select PANELPROPS from DUMMY where PanelTree.USERNAME = DUMMY.USERNAME and PanelTree.NODEID = DUMMY.NODEID);
update PanelTree set PANELPROPS = concat('{',PANELPROPS,'}');
drop table if exists DUMMY;
drop table if exists PanelProps;
