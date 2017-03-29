SELECT 'DROPPING ALL FOREIGN KEYS' AS 'MIGRATION PROCESS STATUS ... ';
SET FOREIGN_KEY_CHECKS=0;
#SET GLOBAL FOREIGN_KEY_CHECKS=0;

alter table ALERTUSERPROPS DROP FOREIGN KEY FKCFB5A6A93E26DDB;
alter table AttributeValueChangeEvent DROP FOREIGN KEY FK28B9493539AF4EAA;
alter table AvailabilityEvent DROP FOREIGN KEY FKD2C5CDDF8ABECD54;
alter table BlackBoxDataConfig DROP FOREIGN KEY FK9B73E218E3070E50;
alter table BlackBoxLogStream DROP FOREIGN KEY FK4336FD8286709EC;
alter table CNEOMIManagementEvent DROP FOREIGN KEY FK7A783126CD9C739B;
alter table CORBANode DROP FOREIGN KEY FK427DD3C7435BA95;
alter table CRITERIAPROPERTIES DROP FOREIGN KEY FK435EDDD2977A5201;
alter table CnpACL DROP FOREIGN KEY FK7896B1056453B274;
alter table CnpAirFilter DROP FOREIGN KEY FK2A94459D2D974A15;
alter table CnpCage DROP FOREIGN KEY FK9A40CCA1793783C3;
alter table CnpCageInfo DROP FOREIGN KEY FKBDFA6CEF89FA8F3A;
alter table CnpClusterManager DROP FOREIGN KEY FK83EF00F824E9DDA;
alter table CnpClusterManagerPG DROP FOREIGN KEY FK4432ACEFE9043491;
alter table CnpEmsSensor DROP FOREIGN KEY FK660878A0690B7D18;
alter table CnpEmsServer DROP FOREIGN KEY FK660A5429690D58A1;
alter table CnpEmsServerPG DROP FOREIGN KEY FKCC5F7E09AFF5998;
alter table CnpEthernetPort DROP FOREIGN KEY FKE27A36DDAE8BA228;
alter table CnpEthernetPortGroup DROP FOREIGN KEY FK3CB491A2E1241391;
alter table CnpExtSwitch DROP FOREIGN KEY FK129704F0159A0968;
alter table CnpExternalServer DROP FOREIGN KEY FK102F3937F629075;
alter table CnpFan DROP FOREIGN KEY FK7896C78E1CE0D446;
alter table CnpFileSystemInfo DROP FOREIGN KEY FK89CE7ABED717DA89;
alter table CnpFruInfo DROP FOREIGN KEY FK573430724D7648E1;
alter table CnpLocationInfo DROP FOREIGN KEY FKF15933E8BD6A9F33;
alter table CnpMonDevInfo DROP FOREIGN KEY FK55548DC11BB9FA7;
alter table CnpPEM DROP FOREIGN KEY FK7896E9931CE0F64B;
alter table CnpPSBlackBoxDC DROP FOREIGN KEY FKAC4BAEF3CA37A655;
alter table CnpPSBlackBoxLS DROP FOREIGN KEY FKAC4BAFFBBA405086;
alter table CnpPSCoreFileMgmtLS DROP FOREIGN KEY FKBEC113CBA5DDDD56;
alter table CnpPayloadServer DROP FOREIGN KEY FK3C27392CD5B95AE4;
alter table CnpPayloadServerPG DROP FOREIGN KEY FKCF3DA8238CB3D71B;
alter table CnpRTM DROP FOREIGN KEY FK7896F2E61CE0FF9E;
alter table CnpRaid DROP FOREIGN KEY FK9A479E6F793E5591;
alter table CnpRaidController DROP FOREIGN KEY FKFFD3DF6B7E337C4D;
alter table CnpRaidFan DROP FOREIGN KEY FKB45F2A24F3B5815C;
alter table CnpRaidLogicalDrive DROP FOREIGN KEY FK95AEEF103A8076B2;
alter table CnpRaidLogicalUnit DROP FOREIGN KEY FK80BABB3EC84DE776;
alter table CnpRaidPEM DROP FOREIGN KEY FKB45F4C29F3B5A361;
alter table CnpRaidPhysicalDrive DROP FOREIGN KEY FKEC8874C4DBE70E3C;
alter table CnpRaidPort DROP FOREIGN KEY FKD78ADB90A38AFDDB;
alter table CnpRaidPortGroup DROP FOREIGN KEY FKFDDDE4CFADF90FBE;
alter table CnpServerInfo DROP FOREIGN KEY FKE8A581B6F50BD881;
alter table CnpShelfEEPROM DROP FOREIGN KEY FK5A777C25E8B0DDDD;
alter table CnpShmm DROP FOREIGN KEY FK9A482D9A793EE4BC;
alter table CnpShmmBMC DROP FOREIGN KEY FKF573B57E34CA0CB6;
alter table CnpSwitch DROP FOREIGN KEY FK29C4E4D965CFF6BB;
alter table CnpSwitchUnit DROP FOREIGN KEY FK40CD1DFD9829D55F;
alter table CnpSystemAlarmPanel DROP FOREIGN KEY FK65012D279D2B4C9;
alter table CnpSystemInfo DROP FOREIGN KEY FK7BCC6EA28832C56D;
alter table CnpTermServer DROP FOREIGN KEY FKE17B31F438D7E956;
alter table ConnectivityData DROP FOREIGN KEY FK8BE5E215D4EFF6;
alter table Constituent DROP FOREIGN KEY FKF55AC104856D278;
alter table CoreFileMgmtLogStream DROP FOREIGN KEY FK4F535E08BA15349C;
alter table DataObject DROP FOREIGN KEY FKBB1A73A9A7754CCC;
alter table EMHAlert DROP FOREIGN KEY FK6794709C196074E5;
alter table EMHEvent DROP FOREIGN KEY FK67D159DA66FF875B;
alter table EMHInfoEvent DROP FOREIGN KEY FK4FC121CCD07CAC4D;
alter table EVENTUSERPROPS DROP FOREIGN KEY FK54B8432B87584B8F;
alter table Endpoint DROP FOREIGN KEY FK6BA181B5D3F52B8A;
alter table EventControl DROP FOREIGN KEY FK78A59D83F9945526;
alter table HapNeControl DROP FOREIGN KEY FK39603EEF7A94456C;
alter table Interface DROP FOREIGN KEY FK95678D1931884ABE;
alter table InterfaceContainer DROP FOREIGN KEY FK3BE68B688D85973D;
alter table IpAddress DROP FOREIGN KEY FKD8D77CAD7825E164;
alter table IpConfig DROP FOREIGN KEY FK3C9427693BF5820C;
alter table LogStream DROP FOREIGN KEY FKEBCF7BC44C2A0887;
alter table LogStreamDataConfig DROP FOREIGN KEY FK3D63EA709F587B3;
alter table LogicalContainer DROP FOREIGN KEY FK73B48E9863A233B;
alter table LogicalElement DROP FOREIGN KEY FK1C5DFAD35C62F0F6;
alter table LogicalUnit DROP FOREIGN KEY FK834A650D1C49DD24;
alter table LogicalUnitStream DROP FOREIGN KEY FK7D15E2ED4DD089B0;
alter table MAPPEDPROPERTIES DROP FOREIGN KEY FK4D2F47A6977A5201;
alter table MAPUSERPROPS DROP FOREIGN KEY FK30B70EA9AF738122;
alter table ManagedGroupObject DROP FOREIGN KEY FK2D38159FD43F4EA2;
alter table ManagedObject DROP FOREIGN KEY FKB855B9E41BE4C5D;
alter table MapContainer DROP FOREIGN KEY FKFA2B62A5334AE2DB;
alter table MapGroup DROP FOREIGN KEY FKD33BEA36F4BC0D9;
alter table MapLink DROP FOREIGN KEY FK951453561311A184;
alter table NECF_ATTR DROP FOREIGN KEY CRTFK_ATTR;
alter table NECF_ATTR DROP FOREIGN KEY IPPARAMFK_ATTR;
alter table NECF_ATTR DROP FOREIGN KEY OPPARAMFK_ATTR;
alter table NECF_ATTR DROP FOREIGN KEY RELFK_ATTR;
alter table NECF_ATTR DROP FOREIGN KEY SETFK_ATTR;
alter table NECF_CRT DROP FOREIGN KEY NECFFK_NECFCRT;
alter table NECF_DEL DROP FOREIGN KEY NECFFK_DEL;
alter table NECF_IPPARAM DROP FOREIGN KEY OPFK_IPPARAM;
alter table NECF_MOD DROP FOREIGN KEY NECFFK_MOD;
alter table NECF_OP DROP FOREIGN KEY NECFFK_OP;
alter table NECF_OPPARAM DROP FOREIGN KEY OPFK_OPPARAM;
alter table NECF_REL DROP FOREIGN KEY NECFFK_REL;
alter table NECF_SET DROP FOREIGN KEY MODFK_SET;
alter table Network DROP FOREIGN KEY FKD119F20E5044AD45;
alter table NetworkElementManagement DROP FOREIGN KEY FK7D44D3F1C014A294;
alter table Node DROP FOREIGN KEY FK2522223D370DA5;
alter table ObjectCreationDeletionEvent DROP FOREIGN KEY FK69B254AE150BABA3;
alter table POLLUSERPROPS DROP FOREIGN KEY FK5DBA4B86D7D7502;
alter table PhysicalContainer DROP FOREIGN KEY FK6DB5CD4A25E1F9E1;
alter table PhysicalElement DROP FOREIGN KEY FK92A33E054D3C391C;
alter table PhysicalEntity DROP FOREIGN KEY FKB2493FBAF24E35DD;
alter table PhysicalSubUnit DROP FOREIGN KEY FK86610AED40FA0604;
alter table PhysicalUnit DROP FOREIGN KEY FKB1E53F1B32D3F6BE;
alter table PhysicalUnitStream DROP FOREIGN KEY FKC33C7A7B3D5D8F2;
alter table PortObject DROP FOREIGN KEY FK679DDF409E30C459;
alter table Printer DROP FOREIGN KEY FK50765FFA21D93CDB;
alter table ProtectionGroup DROP FOREIGN KEY FK41498306FBE27E1D;
alter table RelationObject DROP FOREIGN KEY FKEFA3E87B2FA8DE9E;
alter table RelationshipChangeEvent DROP FOREIGN KEY FKF35DB352E4A0747;
alter table SBNE DROP FOREIGN KEY FK26BC468EC439E9;
alter table SnmpInterface DROP FOREIGN KEY FK7DB9517B6E19E932;
alter table SnmpNode DROP FOREIGN KEY FK293EA880896A8103;
alter table StateChangeEvent DROP FOREIGN KEY FK8A948F991CE6F71A;
alter table SwitchObject DROP FOREIGN KEY FK4C0B63B3695F01CC;
alter table TL1Interface DROP FOREIGN KEY FK11A58880F6B1DA18;
alter table TL1Node DROP FOREIGN KEY FKE013625BB76D185D;
alter table TOPOUSERPROPS DROP FOREIGN KEY FK483583EBFBE5F355;
alter table TopoObject DROP FOREIGN KEY FK608221B9A4FF80BC;
alter table UserDefinedEvent DROP FOREIGN KEY FKB4D6AD9C4729151D;

SELECT 'Creating PollFilters table' AS 'MIGRATION PROCESS STATUS ... ';
create table PollFilters(CLASSNAME varchar(100) NOT NULL,PRIMARY KEY(CLASSNAME));

SELECT 'alter PolledData table' AS 'MIGRATION PROCESS STATUS ... ';
alter table PolledData drop primary key;
alter table PolledData add column OWNERNAME varchar(25) default 'NULL' after FAILURETHRESHOLD;
alter table PolledData add UNIQUE KEY(`NAME`,`AGENT`,`OID`);
alter table PolledData drop column DISCRIMINATOR;

SELECT 'create table DBPOLL' AS 'MIGRATION PROCESS STATUS ... ';
create table DBPOLL (KEYSTRING VARCHAR(250) NOT NULL, VALUESTRING varchar(250), PRIMARY KEY (KEYSTRING),index DBPOLL0_ndx (KEYSTRING));
alter table PolledData add column ACTIVE_STR varchar(10) default NULL after period ;
update PolledData set ACTIVE_STR = 'true' where ACTIVE is true;
update PolledData set ACTIVE_STR = 'false' where ACTIVE is false;
alter table PolledData drop column ACTIVE;
alter table PolledData change ACTIVE_STR ACTIVE varchar(10);

alter table PolledData add column LOGDIRECTLY_STR varchar(10) default NULL AFTER ACTIVE;
update PolledData set LOGDIRECTLY_STR = 'true' where LOGDIRECTLY is true;
update PolledData set LOGDIRECTLY_STR = 'false' where LOGDIRECTLY is false;
alter table PolledData drop column LOGDIRECTLY;
alter table PolledData change LOGDIRECTLY_STR LOGDIRECTLY varchar(10);
alter table PolledData add column SAVEDATA_STR varchar(10) default NULL AFTER LOGFILE;
update PolledData set SAVEDATA_STR = 'true' where SAVEDATA is true;
update PolledData set SAVEDATA_STR = 'false' where SAVEDATA is false;
alter table PolledData drop column SAVEDATA;
alter table PolledData change SAVEDATA_STR SSAVE varchar(10);

alter table PolledData change column THRESHOLD THRESHOLD_STR varchar(10) default NULL;
alter table PolledData add column THRESHOLD bit(1) default NULL AFTER SSAVE;
update PolledData set THRESHOLD = THRESHOLD_STR like 'true';
alter table PolledData drop column THRESHOLD_STR;
alter table PolledData add column ISMULTIPLEPOLLEDDATA_STR varchar(10) default NULL AFTER THRESHOLD;
update PolledData set ISMULTIPLEPOLLEDDATA_STR = 'true' where ISMULTIPLEPOLLEDDATA is true;
update PolledData set ISMULTIPLEPOLLEDDATA_STR = 'false' where ISMULTIPLEPOLLEDDATA is false;
alter table PolledData drop column ISMULTIPLEPOLLEDDATA;
alter table PolledData change ISMULTIPLEPOLLEDDATA_STR ISMULTIPLEPOLLEDDATA varchar(10);
alter table PolledData add column SAVEABSOLUTES_STR varchar(10) default NULL AFTER NUMERICTYPE;
update PolledData set SAVEABSOLUTES_STR = 'true' where SAVEABSOLUTES is true;
update PolledData set SAVEABSOLUTES_STR = 'false' where SAVEABSOLUTES is false;
alter table PolledData drop column SAVEABSOLUTES;
alter table PolledData change SAVEABSOLUTES_STR SAVEABSOLUTES varchar(10);
alter table PolledData add column TIMEAVG_STR varchar(10) default NULL AFTER THRESHOLD;
update PolledData set TIMEAVG_STR = 'true' where TIMEAVG is true;
update PolledData set TIMEAVG_STR = 'false' where TIMEAVG is false;
alter table PolledData drop column TIMEAVG;
alter table PolledData change TIMEAVG_STR TIMEAVG varchar(10);
alter table PolledData add column SAVEONTHRESHOLD_STR varchar(10) default NULL AFTER CURRENTSAVECOUNT;
update PolledData set SAVEONTHRESHOLD_STR = 'true' where SAVEONTHRESHOLD is true;
update PolledData set SAVEONTHRESHOLD_STR = 'false' where SAVEONTHRESHOLD is false;
alter table PolledData drop column SAVEONTHRESHOLD;
alter table PolledData change SAVEONTHRESHOLD_STR SAVEONTHRESHOLD varchar(10);
create index PolledData0_ndx ON PolledData (NAME);
create index PolledData1_ndx ON PolledData (AGENT);
create index PolledData2_ndx ON PolledData (OID);
create index PolledData3_ndx ON PolledData (ID);
create index PolledData4_ndx ON PolledData (COMMUNITY);
create index PolledData5_ndx ON PolledData (PERIOD);
create index PolledData6_ndx ON PolledData (TIMEVAL);
create index PolledData7_ndx ON PolledData (OWNERNAME);
drop index PARENTOBJ_ndx on PolledData;

SELECT 'create table POLLUSERPROPS' AS 'MIGRATION PROCESS STATUS ... ';
drop table POLLUSERPROPS;
create table POLLUSERPROPS (NAME varchar(100) NOT NULL, AGENT varchar(100) NOT NULL, OID varchar(200) NOT NULL, OWNERNAME varchar(25) NOT NULL, PROPNAME varchar(150) NOT NULL, PROPVAL varchar(150));
create index POLLUSERPROPS0_ndx on POLLUSERPROPS (NAME);
create index POLLUSERPROPS1_ndx on POLLUSERPROPS (AGENT);
create index POLLUSERPROPS2_ndx on POLLUSERPROPS (OID);
create index POLLUSERPROPS3_ndx on POLLUSERPROPS (OWNERNAME);

SELECT 'alter Alert table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Alert add column OWNERNAME varchar(100) NOT NULL after WEBNMS;
alter table Alert add column MAPNAME varchar(100) default NULL after SOURCE;
alter table Alert add column STAGE INTEGER default 0 after WHO;
alter table Alert add column PRIORITY INTEGER default 0 after OWNERNAME;
update Alert set OWNERNAME='NULL' where OWNERNAME is null;
create index Alert0_ndx on Alert (ENTITY);

SELECT 'create table DBALERT' AS 'MIGRATION PROCESS STATUS ... ';
create table DBALERT (KEYSTRING VARCHAR(250) NOT NULL,VALUESTRING varchar(250),PRIMARY KEY (KEYSTRING));
create index DBALERT0_ndx on DBALERT(KEYSTRING);
update DBALERT set VALUESTRING = (select DISCRIMINATOR from Alert where ENTITY=KEYSTRING);
alter table Alert drop column DISCRIMINATOR;

SELECT 'alter ALERTUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ALERTUSERPROPS add column OWNERNAME VARCHAR(25) NOT NULL;
update ALERTUSERPROPS set OWNERNAME='NULL' where OWNERNAME is null;
alter table ALERTUSERPROPS add column `NAME` varchar(100) NOT NULL first;
alter table ALERTUSERPROPS drop primary key;
update ALERTUSERPROPS set NAME = ENTITY;
alter table ALERTUSERPROPS drop column ENTITY;
create index ALERTUSERPROPS0_ndx ON ALERTUSERPROPS (NAME);
create index ALERTUSERPROPS1_ndx ON ALERTUSERPROPS (OWNERNAME);

SELECT 'alter ANNOTATION table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ANNOTATION add column OWNERNAME varchar(25);
alter table ANNOTATION drop column AAID;
alter table ANNOTATION drop column DISCRIMINATOR;
ALTER TABLE ANNOTATION add index ANNOTATION0_ndx (ENTITY);

SELECT 'create table DBMAP' AS 'MIGRATION PROCESS STATUS ... ';
create table DBMAP (KEYSTRING VARCHAR(250) NOT NULL, VALUESTRING varchar(250), PRIMARY KEY (KEYSTRING));
create index DBMAP0_ndx on DBMAP (KEYSTRING);
insert into DBMAP values ('Failed_Objects_Map.netmap','MapDB');
alter table MapDB add column OWNERNAME varchar(25) NOT NULL after NAME;

alter table MapDB change column ANCHORED ANCHORED varchar(10);
update MapDB set ANCHORED='true' where ANCHORED like '1';
update MapDB set ANCHORED='false' where ANCHORED like '0';

alter table MapDB change column AUTOPLACEMENT AUTOPLACEMENT varchar(10);
update MapDB set AUTOPLACEMENT='true' where AUTOPLACEMENT like '1';
update MapDB set AUTOPLACEMENT='false' where AUTOPLACEMENT like '0';

SELECT 'create table CUSTOMMAPS' AS 'MIGRATION PROCESS STATUS ... ';
create table  CUSTOMMAPS (VALUESTRING varchar(250));
insert into CUSTOMMAPS(VALUESTRING) (select Name from MapDB where Name like '%Failed%');

SELECT 'create table DEFAULTMAPS' AS 'MIGRATION PROCESS STATUS ... ';
create table  DEFAULTMAPS (VALUESTRING varchar(250));
insert into DEFAULTMAPS(VALUESTRING) (select Name from MapDB where Name NOT like '%Failed%');

SELECT 'alter MapDB table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapDB drop column TABPANELS;
alter table MapDB drop column `TYPE`;
create index MapDB0_ndx ON MapDB (NAME);

SELECT 'alter MapSymbol table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapSymbol add column OWNERNAME varchar(25) NOT NULL AFTER NAME;

alter table MapSymbol add column ANCHORED_STR varchar(10) default NULL AFTER PARENTNAME;
update MapSymbol set ANCHORED_STR = 'true' where ANCHORED is true;
update MapSymbol set ANCHORED_STR = 'false' where ANCHORED is false;
alter table MapSymbol drop column ANCHORED;
alter table MapSymbol change ANCHORED_STR ANCHORED varchar(10);

alter table MapSymbol add column WIDTH_STR varchar(25) default NULL after MENUNAME;
update MapSymbol set WIDTH_STR=WIDTH;
alter table MapSymbol drop column WIDTH;
alter table MapSymbol change WIDTH_STR WIDTH varchar(25);
alter table MapSymbol add column HEIGHT_STR varchar(25) default NULL after WIDTH;
update MapSymbol set HEIGHT_STR=HEIGHT;
alter table MapSymbol drop column HEIGHT;
alter table MapSymbol change HEIGHT_STR HEIGHT varchar(25);
alter table MapSymbol add column X_STR varchar(25) default NULL after HEIGHT;
update MapSymbol set X_STR=X;
alter table MapSymbol drop column X;
alter table MapSymbol change X_STR X varchar(25);
alter table MapSymbol add column Y_STR varchar(25) default NULL after X;
update MapSymbol set Y_STR=Y;
alter table MapSymbol drop column Y;
alter table MapSymbol change Y_STR Y varchar(25);

create index MapSymbol0_ndx ON MapSymbol (NAME);
create index MapSymbol1_ndx ON MapSymbol (MAPNAME);
create index MapSymbol2_ndx ON MapSymbol (OWNERNAME);
create index MapSymbol3_ndx ON MapSymbol (OBJNAME,OWNERNAME);
alter table MapSymbol drop column DISCRIMINATOR;
alter table MapSymbol drop column MAPWIDTH;
alter table MapSymbol drop column MAPHEIGHT;

SELECT 'alter MapContainer table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapContainer add column OWNERNAME varchar(25) NOT NULL AFTER MAPNAME;
alter table MapContainer add column OBJNAME varchar(100) NOT NULL after NAME;
alter table MapContainer add column LABEL varchar(200) after OWNERNAME;
alter table MapContainer add column ICONNAME varchar(100) after LABEL;
alter table MapContainer add column MENUNAME  varchar(100) after ICONNAME;
alter table MapContainer add column WIDTH INTEGER after MENUNAME;
alter table MapContainer add column HEIGHT INTEGER after WIDTH;
alter table MapContainer add column X INTEGER after HEIGHT;
alter table MapContainer add column Y INTEGER after X;
alter table MapContainer add column WEBNMS varchar(100) after Y;
alter table MapContainer add column GROUPNAME varchar(100) after WEBNMS;
alter table MapContainer add column ANCHORED varchar(10) after GROUPNAME;
alter table MapContainer add column OBJTYPE INTEGER after ANCHORED;
alter table MapContainer add column PARENTNAME varchar(100) after OBJTYPE;

alter table MapContainer add column CONTAINMENT_STR varchar(10);
update MapContainer set CONTAINMENT_STR='true' where CONTAINMENT is true;
update MapContainer set CONTAINMENT_STR='false' where CONTAINMENT is false;
alter table MapContainer drop column CONTAINMENT;
alter table MapContainer change CONTAINMENT_STR CONTAINMENT varchar(10);

create index MapContainer0_ndx on MapContainer(MAPNAME);
create index MapContainer1_ndx on MapContainer(NAME);
create index MapContainer2_ndx on MapContainer(OWNERNAME);
create index MapContainer3_ndx on MapContainer(OBJNAME,OWNERNAME);

SELECT 'alter MapLink table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapLink add column OWNERNAME varchar(25) AFTER MAPNAME;
alter table MapLink add column LABEL varchar(100) after LINKTYPE;
alter table MapLink add column MENUNAME varchar(100) after LABEL;
alter table MapLink add column X varchar(25) after NX;
alter table MapLink add column Y varchar(25) after NY;
alter table MapLink add column OBJNAME varchar(100) after Y;
alter table MapLink add column WEBNMS varchar(100) after STATUS;
alter table MapLink add column GROUPNAME varchar(100) after WEBNMS;
alter table MapLink change column NX NX varchar(25);
alter table MapLink change column NY NY varchar(25);
alter table MapLink change column NY NY varchar(25);
alter table MapLink change column STATUS STATUS varchar(25);

create index MapLink0_ndx on MapLink(NAME);
create index MapLink1_ndx on MapLink(MAPNAME);
create index MapLink2_ndx on MapLink(OWNERNAME);
create index MapLink3_ndx on MapLink(OBJNAME,OWNERNAME);

SELECT 'alter MapGroup table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MapGroup add column OBJNAME varchar(100) not NULL after NAME;
alter table MapGroup add column OWNERNAME varchar(25) not NULL after NAME;
alter table MapGroup add column LABEL varchar(200) not NULL after OWNERNAME;
alter table MapGroup add column ICONNAME varchar(100) after LABEL;
alter table MapGroup add column MENUNAME varchar(100) after ICONNAME;
alter table MapGroup add column WIDTH INTEGER after MENUNAME;
alter table MapGroup add column HEIGHT INTEGER after WIDTH;
alter table MapGroup add column X INTEGER after HEIGHT;
alter table MapGroup add column Y INTEGER after X;
alter table MapGroup add column WEBNMS varchar(100) after Y;
alter table MapGroup add column GROUPNAME varchar(100) after WEBNMS;
alter table MapGroup add column OBJTYPE INTEGER after GROUPNAME;
alter table MapGroup add column ANCHORED varchar(10) after OBJTYPE;

create index MapGroup0_ndx on MapGroup(MAPNAME);
create index MapGroup1_ndx on MapGroup(NAME);
create index MapGroup2_ndx on MapGroup(OWNERNAME);
create index MapGroup3_ndx on MapGroup(OBJNAME,OWNERNAME);

SELECT 'RENAME CRITERIAPROPERTIES table to CUSTOMPROPS' AS 'MIGRATION PROCESS STATUS ... ';
alter table CRITERIAPROPERTIES RENAME TO CUSTOMPROPS;
alter table CUSTOMPROPS CHANGE NAME KEYSTRING varchar(250) NOT NULL;
alter table CUSTOMPROPS CHANGE PROPNAME PROPKEY varchar(255) NOT NULL;
alter table CUSTOMPROPS CHANGE PROPVAL PROPVALUE varchar(250);
alter table CUSTOMPROPS drop primary key;
create index CUSTOMPROPS0_ndx on CUSTOMPROPS(KEYSTRING);

drop table if exists CRITERIAPROPERTIES;

SELECT 'alter MAPUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table MAPUSERPROPS add column OWNERNAME varchar(25) default 'NULL' after MAPNAME;
alter table MAPUSERPROPS drop primary key;
create index MAPUSERPROPS0_ndx on MAPUSERPROPS(NAME);
create index MAPUSERPROPS1_ndx on MAPUSERPROPS(MAPNAME);
create index MAPUSERPROPS2_ndx on MAPUSERPROPS(OWNERNAME);

SELECT 'create table TOPODBSPECIALKEY' AS 'MIGRATION PROCESS STATUS ... ';
create table  TOPODBSPECIALKEY (KEYSTRING VARCHAR(250) NOT NULL, VALUESTRING varchar(250), PRIMARY KEY(KEYSTRING), index TOPODBSPECIALKEY0_ndx (KEYSTRING));

SELECT 'create table DBINTERFACES' AS 'MIGRATION PROCESS STATUS ... ';
create table DBINTERFACES (VALUESTRING varchar(250));

SELECT 'alter table ManagedObject  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedObject add column OWNERNAME varchar(25) not NULL after NAME;
alter table ManagedObject change column STATUSPOLLENABLED STATUSPOLLENABLED varchar(10);
update ManagedObject set STATUSPOLLENABLED='true' where STATUSPOLLENABLED like '1';
update ManagedObject set STATUSPOLLENABLED='false' where STATUSPOLLENABLED like '0';
alter table ManagedObject change column ISCONTAINER ISCONTAINER varchar(10);
update ManagedObject set ISCONTAINER='true' where ISCONTAINER like '1';
update ManagedObject set ISCONTAINER='false' where ISCONTAINER like '0';

alter table ManagedObject change column ISGROUP ISGROUP varchar(10);
update ManagedObject set ISGROUP='true' where ISGROUP like '1';
update ManagedObject set ISGROUP='false' where ISGROUP like '0';

alter table ManagedObject change column MANAGED MANAGED varchar(10);
update ManagedObject set MANAGED='true' where MANAGED like '1';
update ManagedObject set MANAGED='false' where MANAGED like '0';

alter table ManagedObject change column STATUSCHANGETIME STATUSCHANGETIME varchar(25) default NULL;
alter table ManagedObject change column STATUSUPDATETIME STATUSUPDATETIME varchar(25) default NULL;

alter table ManagedObject drop column DISCRIMINATOR;
alter table ManagedObject drop column PARENTID;

create index ManagedObject0_ndx on ManagedObject(NAME);
create index ManagedObject1_ndx on ManagedObject (OWNERNAME);
create index ManagedObject2_ndx on ManagedObject (PARENTKEY);

SELECT 'alter table ManagedGroupObject  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ManagedGroupObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ManagedGroupObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ManagedGroupObject.MOID group by ManagedObject.MOID);

SELECT 'alter table TopoObject  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TopoObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TopoObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TopoObject.MOID group by ManagedObject.MOID);

alter table TopoObject change column ISDHCP ISDHCP varchar(10);
update TopoObject set ISDHCP='true' where ISDHCP like '1';
update TopoObject set ISDHCP='false' where ISDHCP like '0';

alter table TopoObject change column ISINTERFACE ISINTERFACE varchar(10);
update TopoObject set ISINTERFACE='true' where ISINTERFACE like '1';
update TopoObject set ISINTERFACE='false' where ISINTERFACE like '0';

alter table TopoObject change column ISNETWORK ISNETWORK varchar(10);
update TopoObject set ISNETWORK='true' where ISNETWORK like '1';
update TopoObject set ISNETWORK='false' where ISNETWORK like '0';

alter table TopoObject change column ISNODE ISNODE varchar(10);
update TopoObject set ISNODE='true' where ISNODE like '1';
update TopoObject set ISNODE='false' where ISNODE like '0';

alter table TopoObject change column ISSNMP ISSNMP varchar(10);
update TopoObject set ISSNMP='true' where ISSNMP like '1';
update TopoObject set ISSNMP='false' where ISSNMP like '0';

SELECT 'alter table Node  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Node add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Node set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Node.MOID group by ManagedObject.MOID);

alter table Node change column ISROUTER ISROUTER varchar(10);
update Node set ISROUTER='true' where ISROUTER like '1';
update Node set ISROUTER='false' where ISROUTER like '0';

SELECT 'alter table Network  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Network add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL  after MOID;
update Network set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Network.MOID group by ManagedObject.MOID);

alter table Network change column DISCOVER DISCOVER varchar(10);
update Network set DISCOVER='true' where DISCOVER like '1';
update Network set DISCOVER='false' where DISCOVER like '0';

SELECT 'alter table SnmpNode  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SnmpNode add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SnmpNode set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SnmpNode.MOID group by ManagedObject.MOID);

SELECT 'alter table SnmpInterface  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SnmpInterface add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SnmpInterface set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SnmpInterface.MOID group by ManagedObject.MOID);

SELECT 'alter table TL1Node  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TL1Node add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TL1Node set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID =TL1Node.MOID group by ManagedObject.MOID);

SELECT 'alter table TL1Interface  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TL1Interface add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TL1Interface set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TL1Interface.MOID group by ManagedObject.MOID);

SELECT 'alter table IpAddress  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table IpAddress add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update IpAddress set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = IpAddress.MOID group by ManagedObject.MOID);

SELECT 'alter table SwitchObject  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SwitchObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SwitchObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SwitchObject.MOID group by ManagedObject.MOID);

SELECT 'alter table PortObject  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PortObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PortObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PortObject.MOID group by ManagedObject.MOID);

SELECT 'alter table Printer  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Printer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Printer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Printer.MOID group by ManagedObject.MOID);
alter table Printer drop column CONSOLELIGHTSTRING;

SELECT 'alter table CORBANode  populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table CORBANode add column NAME varchar(100) first;
alter table CORBANode add column OWNERNAME varchar(25) NOT NULL after MOID;
update CORBANode set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CORBANode.MOID group by ManagedObject.MOID);

SELECT 'alter TOPOUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table TOPOUSERPROPS add column NAME varchar(100) NOT NULL first;
alter table TOPOUSERPROPS add column OWNERNAME varchar(25) NOT NULL;
update TOPOUSERPROPS set NAME=(select ManagedObject.NAME from ManagedObject where TOPOUSERPROPS.MOID=ManagedObject.MOID);
alter table TOPOUSERPROPS drop PRIMARY KEY;

SELECT 'alter ObjectSchedulerRUNNABLE table' AS 'MIGRATION PROCESS STATUS ... ';
alter table ObjectSchedulerRUNNABLE add column VALUESTRING varchar(250) NOT NULL first;
update ObjectSchedulerRUNNABLE set VALUESTRING = (select NAME from ManagedObject where ObjectSchedulerRUNNABLE.MOID=ManagedObject.MOID);
alter table ObjectSchedulerRUNNABLE drop primary key;
alter table ObjectSchedulerRUNNABLE add primary key (VALUESTRING);
alter table ObjectSchedulerRUNNABLE drop column MOID;
alter table ObjectSchedulerRUNNABLE drop column CLASSNAME;
alter table ObjectSchedulerRUNNABLE drop index ObjectSchedulerRUNNABLE0_ndx;
create index ObjectSchedulerRUNNABLE0_ndx on ObjectSchedulerRUNNABLE (TIMEVAL);
create index ObjectSchedulerRUNNABLE1_ndx on ObjectSchedulerRUNNABLE (VALUESTRING);
alter table GroupTable add column OWNERNAME varchar (25) NOT NULL after NAME;
alter table GroupTable add column MEMBEROWNERNAME varchar (25) NOT NULL after MEMBERNAME;
alter table GroupTable drop PRIMARY KEY;

SELECT 'create table EVENTLOGGER' AS 'MIGRATION PROCESS STATUS ... ';
create table  EVENTLOGGER (VALUESTRING BIGINT NOT NULL, POSITION INTEGER NOT NULL);
create index EVENTLOGGER0_ndx on EVENTLOGGER(POSITION);

SELECT 'create table EventAlertFilter' AS 'MIGRATION PROCESS STATUS ... ';
create table EventAlertFilter (TYPE varchar(100) NOT NULL, FILTERNAME varchar(100) NOT NULL, ACTIONNAME varchar(100), PROPKEY varchar(100), PROPVALUE blob);

SELECT 'create table GenericFaultTable' AS 'MIGRATION PROCESS STATUS ... ';
create table GenericFaultTable (TYPE varchar(100) NOT NULL, NAME varchar(100) NOT NULL, PRIMARY KEY (TYPE,NAME));

SELECT 'create table TrapEventParser' AS 'MIGRATION PROCESS STATUS ... ';
create table TrapEventParser (TYPE varchar(100) NOT NULL, NAME varchar(100) NOT NULL, PROPKEY varchar(100), PROPVALUE blob);

SELECT 'create table TrapFilter' AS 'MIGRATION PROCESS STATUS ... ';
create table TrapFilter (NAME varchar(100), ENTERPRISEOID varchar(100), GT varchar(10), ST varchar(10), TRAPOID varchar(100), CLASSNAME varchar(100), STATE varchar(50));

SELECT 'create table AgentDefValObject' AS 'MIGRATION PROCESS STATUS ... ';
create table AgentDefValObject (NAME VARCHAR (100) NOT NULL , VALUE VARCHAR (100) , PRIMARY KEY (NAME), index AgentDefValObject0_ndx (NAME));

SELECT 'create table ALERTLOGGER' AS 'MIGRATION PROCESS STATUS ... ';
create table  ALERTLOGGER (VALUESTRING varchar(250));

SELECT 'alter Event table' AS 'MIGRATION PROCESS STATUS ... ';
alter table Event add column DDOMAIN varchar(100) default NULL after CATEGORY;
alter table Event add column HELPURL varchar(100) default NULL after SOURCE;
alter table Event add column OWNERNAME varchar(25) NOT NULL after GROUPNAME;
alter table Event drop column DISCRIMINATOR;

SELECT 'Adding OWNERNAME column to custom Event tables' AS 'MIGRATION PROCESS STATUS ... ';
alter table EMHEvent  add column OWNERNAME varchar(25);
alter table EMHInfoEvent add column OWNERNAME varchar(25);
alter table EMHAlert add column OWNERNAME varchar(25);
alter table ObjectCreationDeletionEvent add column OWNERNAME varchar(25);
alter table StateChangeEvent add column OWNERNAME varchar(25);
alter table UserDefinedEvent add column OWNERNAME varchar(25);
alter table AttributeValueChangeEvent add column OWNERNAME varchar(25);
alter table AvailabilityEvent add column OWNERNAME varchar(25);
alter table RelationshipChangeEvent add column OWNERNAME varchar(25);
alter table CNEOMIManagementEvent add column OWNERNAME varchar(25);

create index Event0_ndx on Event (ID);

SELECT 'create table DBEVENT' AS 'MIGRATION PROCESS STATUS ... ';
create table DBEVENT (KEYSTRING VARCHAR(250) NOT NULL, VALUESTRING varchar(250), PRIMARY KEY (KEYSTRING), index DBEVENT0_ndx (KEYSTRING));

SELECT 'alter EVENTUSERPROPS table' AS 'MIGRATION PROCESS STATUS ... ';
alter table EVENTUSERPROPS change column ID NAME INTEGER;
alter table EVENTUSERPROPS add column OWNERNAME varchar(25) NOT NULL after NAME;
alter table EVENTUSERPROPS drop primary key;
create index EVENTUSERPROPS0_ndx on EVENTUSERPROPS (NAME);
create index EVENTUSERPROPS1_ndx  on EVENTUSERPROPS (OWNERNAME);

SELECT 'RENAME AuthAudit table to AuthAuditExt' AS 'MIGRATION PROCESS STATUS ... ';
alter table AuthAudit rename to AuthAuditExt;

SELECT 'alter PhysicalContainer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table PhysicalContainer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalContainer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalContainer.MOID group by ManagedObject.MOID);

SELECT 'alter PhysicalElement add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table PhysicalElement add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalElement set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalElement.MOID group by ManagedObject.MOID);

SELECT 'alter PhysicalSubUnit add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table PhysicalSubUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalSubUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalSubUnit.MOID group by ManagedObject.MOID);

SELECT 'alter PhysicalUnit add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table PhysicalUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalUnit.MOID group by ManagedObject.MOID);

SELECT 'alter LogicalContainer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogicalContainer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogicalContainer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogicalContainer.MOID group by ManagedObject.MOID);

SELECT 'alter LogicalElement add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogicalElement add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogicalElement set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogicalElement.MOID group by ManagedObject.MOID);

SELECT 'alter LogicalUnit add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogicalUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogicalUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogicalUnit.MOID group by ManagedObject.MOID);

SELECT 'alter ProtectionGroup add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table ProtectionGroup add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ProtectionGroup set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ProtectionGroup.MOID group by ManagedObject.MOID);

SELECT 'alter SBNE add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table SBNE add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SBNE set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SBNE.MOID group by ManagedObject.MOID);

SELECT 'alter NetworkElementManagement add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table NetworkElementManagement add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update NetworkElementManagement set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = NetworkElementManagement.MOID group by ManagedObject.MOID);

SELECT 'alter DataObject add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table DataObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update DataObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = DataObject.MOID group by ManagedObject.MOID);

SELECT 'alter IpConfig add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table IpConfig add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update IpConfig set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = IpConfig.MOID group by ManagedObject.MOID);

SELECT 'alter EventControl add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table EventControl add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EventControl set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EventControl.MOID group by ManagedObject.MOID);

SELECT 'alter RelationObject add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table RelationObject add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update RelationObject set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = RelationObject.MOID group by ManagedObject.MOID);

SELECT 'alter PhysicalUnitStream add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table PhysicalUnitStream add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalUnitStream set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalUnitStream.MOID group by ManagedObject.MOID);

SELECT 'alter LogicalUnitStream add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogicalUnitStream add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogicalUnitStream set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogicalUnitStream.MOID group by ManagedObject.MOID);

SELECT 'alter LogStreamDataConfig add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogStreamDataConfig add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogStreamDataConfig set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogStreamDataConfig.MOID group by ManagedObject.MOID);

SELECT 'alter LogStream add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table LogStream add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LogStream set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LogStream.MOID group by ManagedObject.MOID);

SELECT 'alter Constituent add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table Constituent add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Constituent set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Constituent.MOID group by ManagedObject.MOID);

SELECT 'alter ConnectivityData add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table ConnectivityData add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ConnectivityData set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ConnectivityData.MOID group by ManagedObject.MOID);

SELECT 'alter Interface add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table Interface add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Interface set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Interface.MOID group by ManagedObject.MOID);

SELECT 'alter InterfaceContainer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table InterfaceContainer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update InterfaceContainer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = InterfaceContainer.MOID group by ManagedObject.MOID);

SELECT 'alter Endpoint add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table Endpoint add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Endpoint set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Endpoint.MOID group by ManagedObject.MOID);

SELECT 'alter BlackBoxLogStream add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table BlackBoxLogStream add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BlackBoxLogStream set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BlackBoxLogStream.MOID group by ManagedObject.MOID);

SELECT 'alter CoreFileMgmtLogStream add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CoreFileMgmtLogStream add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CoreFileMgmtLogStream set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CoreFileMgmtLogStream.MOID group by ManagedObject.MOID);

SELECT 'alter HapNeControl add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table HapNeControl add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update HapNeControl set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = HapNeControl.MOID group by ManagedObject.MOID);

SELECT 'alter CnpEthernetPortGroup add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpEthernetPortGroup add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpEthernetPortGroup set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpEthernetPortGroup.MOID group by ManagedObject.MOID);

SELECT 'alter CnpFileSystemInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpFileSystemInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpFileSystemInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpFileSystemInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidPortGroup add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidPortGroup add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidPortGroup set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidPortGroup.MOID group by ManagedObject.MOID);

SELECT 'alter CnpServerInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpServerInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpServerInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpServerInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpEthernetPort add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpEthernetPort add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpEthernetPort set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpEthernetPort.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidPort add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidPort add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidPort set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidPort.MOID group by ManagedObject.MOID);

SELECT 'alter CnpCageInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpCageInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpCageInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpCageInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpACL add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpACL add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpACL set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpACL.MOID group by ManagedObject.MOID);

SELECT 'alter CnpFruInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpFruInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpFruInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpFruInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpSystemInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpSystemInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpSystemInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpSystemInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpLocationInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpLocationInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpLocationInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpLocationInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpMonDevInfo add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpMonDevInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpMonDevInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpMonDevInfo.MOID group by ManagedObject.MOID);

SELECT 'alter CnpCage add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpCage add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpCage set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpCage.MOID group by ManagedObject.MOID);

SELECT 'alter CnpClusterManager add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpClusterManager add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpClusterManager set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpClusterManager.MOID group by ManagedObject.MOID);

SELECT 'alter CnpClusterManagerPG add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpClusterManagerPG add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpClusterManagerPG set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpClusterManagerPG.MOID group by ManagedObject.MOID);

SELECT 'alter CnpEmsServer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpEmsServer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpEmsServer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpEmsServer.MOID group by ManagedObject.MOID);

SELECT 'alter CnpEmsServerPG add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpEmsServerPG add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpEmsServerPG set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpEmsServerPG.MOID group by ManagedObject.MOID);

SELECT 'alter CnpExtSwitch add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpExtSwitch add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpExtSwitch set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpExtSwitch.MOID group by ManagedObject.MOID);

SELECT 'alter CnpFan add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpFan add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpFan set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpFan.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPEM add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPEM add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPEM set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPEM.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaid add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaid add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaid set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaid.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidController add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidController add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidController set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidController.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidFan add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidFan add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidFan set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidFan.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidLogicalDrive add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidLogicalDrive add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidLogicalDrive set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidLogicalDrive.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidLogicalUnit add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidLogicalUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidLogicalUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidLogicalUnit.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidPEM add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidPEM add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidPEM set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidPEM.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRaidPhysicalDrive add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRaidPhysicalDrive add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRaidPhysicalDrive set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRaidPhysicalDrive.MOID group by ManagedObject.MOID);

SELECT 'alter CnpShmm add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpShmm add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpShmm set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpShmm.MOID group by ManagedObject.MOID);

SELECT 'alter CnpSwitch add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpSwitch add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpSwitch set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpSwitch.MOID group by ManagedObject.MOID);

SELECT 'alter CnpTermServer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpTermServer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpTermServer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpTermServer.MOID group by ManagedObject.MOID);

SELECT 'alter CnpSwitchUnit add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpSwitchUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpSwitchUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpSwitchUnit.MOID group by ManagedObject.MOID);

SELECT 'alter CnpRTM add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpRTM add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpRTM set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpRTM.MOID group by ManagedObject.MOID);

SELECT 'alter BlackBoxDataConfig add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table BlackBoxDataConfig add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BlackBoxDataConfig set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BlackBoxDataConfig.MOID group by ManagedObject.MOID);

SELECT 'alter CnpAirFilter add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpAirFilter add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpAirFilter set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpAirFilter.MOID group by ManagedObject.MOID);

SELECT 'alter CnpSystemAlarmPanel add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpSystemAlarmPanel add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpSystemAlarmPanel set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpSystemAlarmPanel.MOID group by ManagedObject.MOID);

SELECT 'alter CnpShelfEEPROM add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpShelfEEPROM add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpShelfEEPROM set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpShelfEEPROM.MOID group by ManagedObject.MOID);

SELECT 'alter CnpShmmBMC add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpShmmBMC add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpShmmBMC set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpShmmBMC.MOID group by ManagedObject.MOID);

SELECT 'alter CnpEmsSensor add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpEmsSensor add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpEmsSensor set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpEmsSensor.MOID group by ManagedObject.MOID);

SELECT 'alter CnpExternalServer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpExternalServer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpExternalServer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpExternalServer.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPSBlackBoxLS add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPSBlackBoxLS add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPSBlackBoxLS set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPSBlackBoxLS.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPSCoreFileMgmtLS add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPSCoreFileMgmtLS add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPSCoreFileMgmtLS set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPSCoreFileMgmtLS.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPayloadServer add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPayloadServer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPayloadServer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPayloadServer.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPayloadServerPG add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPayloadServerPG add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPayloadServerPG set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPayloadServerPG.MOID group by ManagedObject.MOID);

SELECT 'alter CnpPSBlackBoxDC add NAME AND OWNERNAME columns, populate NAME' AS 'MIGRATION PROCESS STATUS...';
alter table CnpPSBlackBoxDC add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CnpPSBlackBoxDC set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CnpPSBlackBoxDC.MOID group by ManagedObject.MOID);

SELECT 'alter PhysicalContainer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalContainer drop column MOID;

SELECT 'alter PhysicalElement table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalElement drop column MOID;

SELECT 'alter PhysicalSubUnit table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalSubUnit drop column MOID;

SELECT 'alter PhysicalUnit table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalUnit drop column MOID;

SELECT 'alter LogicalContainer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalContainer drop column MOID;

SELECT 'alter LogicalElement table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalElement drop column MOID;

SELECT 'alter LogicalUnit table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalUnit drop column MOID;

SELECT 'alter ProtectionGroup table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ProtectionGroup drop column MOID;

SELECT 'alter SBNE table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table SBNE drop column MOID;

SELECT 'alter NetworkElementManagement table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table NetworkElementManagement drop column MOID;

SELECT 'alter DataObject table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table DataObject drop column MOID;

SELECT 'alter IpConfig table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table IpConfig drop column MOID;

SELECT 'alter EventControl table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table EventControl drop column MOID;

SELECT 'alter RelationObject table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table RelationObject drop column MOID;

SELECT 'alter PhysicalUnitStream table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalUnitStream drop column MOID;

SELECT 'alter LogicalUnitStream table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogicalUnitStream drop column MOID;

SELECT 'alter LogStreamDataConfig table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogStreamDataConfig drop column MOID;

SELECT 'alter LogStream table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table LogStream drop column MOID;

SELECT 'alter Constituent table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Constituent drop column MOID;

SELECT 'alter ConnectivityData table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table ConnectivityData drop column MOID;

SELECT 'alter Interface table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Interface drop column MOID;

SELECT 'alter InterfaceContainer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table InterfaceContainer drop column MOID;

SELECT 'alter Endpoint table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table Endpoint drop column MOID;

SELECT 'alter BlackBoxLogStream table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BlackBoxLogStream drop column MOID;

SELECT 'alter CoreFileMgmtLogStream table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CoreFileMgmtLogStream drop column MOID;

SELECT 'alter HapNeControl table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table HapNeControl drop column MOID;

SELECT 'alter CnpEthernetPortGroup table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEthernetPortGroup drop column MOID;

SELECT 'alter CnpFileSystemInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFileSystemInfo drop column MOID;

SELECT 'alter CnpRaidPortGroup table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPortGroup drop column MOID;

SELECT 'alter CnpServerInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpServerInfo drop column MOID;

SELECT 'alter CnpEthernetPort table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEthernetPort drop column MOID;

SELECT 'alter CnpRaidPort table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPort drop column MOID;

SELECT 'alter CnpCageInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpCageInfo drop column MOID;

SELECT 'alter CnpACL table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpACL drop column MOID;

SELECT 'alter CnpFruInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFruInfo drop column MOID;

SELECT 'alter CnpSystemInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSystemInfo drop column MOID;

SELECT 'alter CnpLocationInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpLocationInfo drop column MOID;

SELECT 'alter CnpMonDevInfo table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpMonDevInfo drop column MOID;

SELECT 'alter CnpCage table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpCage drop column MOID;

SELECT 'alter CnpClusterManager table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpClusterManager drop column MOID;

SELECT 'alter CnpClusterManagerPG table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpClusterManagerPG drop column MOID;

SELECT 'alter CnpEmsServer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsServer drop column MOID;

SELECT 'alter CnpEmsServerPG table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsServerPG drop column MOID;

SELECT 'alter CnpExtSwitch table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpExtSwitch drop column MOID;

SELECT 'alter CnpFan table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpFan drop column MOID;

SELECT 'alter CnpPEM table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPEM drop column MOID;

SELECT 'alter CnpRaid table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaid drop column MOID;

SELECT 'alter CnpRaidController table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidController drop column MOID;

SELECT 'alter CnpRaidFan table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidFan drop column MOID;

SELECT 'alter CnpRaidLogicalDrive table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidLogicalDrive drop column MOID;

SELECT 'alter CnpRaidLogicalUnit table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidLogicalUnit drop column MOID;

SELECT 'alter CnpRaidPEM table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPEM drop column MOID;

SELECT 'alter CnpRaidPhysicalDrive table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRaidPhysicalDrive drop column MOID;

SELECT 'alter CnpShmm table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShmm drop column MOID;

SELECT 'alter CnpSwitch table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSwitch drop column MOID;

SELECT 'alter CnpTermServer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpTermServer drop column MOID;

SELECT 'alter CnpSwitchUnit table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSwitchUnit drop column MOID;

SELECT 'alter CnpRTM table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpRTM drop column MOID;

SELECT 'alter BlackBoxDataConfig table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table BlackBoxDataConfig drop column MOID;

SELECT 'alter CnpAirFilter table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpAirFilter drop column MOID;

SELECT 'alter CnpSystemAlarmPanel table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpSystemAlarmPanel drop column MOID;

SELECT 'alter CnpShelfEEPROM table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShelfEEPROM drop column MOID;

SELECT 'alter CnpShmmBMC table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpShmmBMC drop column MOID;

SELECT 'alter CnpEmsSensor table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpEmsSensor drop column MOID;

SELECT 'alter CnpExternalServer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpExternalServer drop column MOID;

SELECT 'alter CnpPSBlackBoxLS table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSBlackBoxLS drop column MOID;

SELECT 'alter CnpPSCoreFileMgmtLS table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSCoreFileMgmtLS drop column MOID;

SELECT 'alter CnpPayloadServer table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPayloadServer drop column MOID;

SELECT 'alter CnpPayloadServerPG table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPayloadServerPG drop column MOID;

SELECT 'alter CnpPSBlackBoxDC table to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table CnpPSBlackBoxDC drop column MOID;

alter table AckFwdSystemLink DROP FOREIGN KEY FKFF73EEF35E3D858D;
alter table BackupServerLink DROP FOREIGN KEY FKEF7EC55F4E485BF9;
alter table BillingServerLink DROP FOREIGN KEY FK96D09A98B390418;
alter table BmgwClientThrottleLink DROP FOREIGN KEY FK48C10BAAC8F841B4;
alter table BmgwServerThrottleLink DROP FOREIGN KEY FK751AD32252EFFC;
alter table BmgwServerUnavailableLink DROP FOREIGN KEY FK58C76CCD33EED4C;
alter table CapacityLicenseLink DROP FOREIGN KEY FK68CCA2A1A4A64761;
alter table ClassOfService DROP FOREIGN KEY FK44AECA66DEB11D70;
alter table Congestion DROP FOREIGN KEY FKD72C34479D0CF5D1;
alter table ContentAdaptationLink DROP FOREIGN KEY FK9DC7746689B08466;
alter table DiskUsage DROP FOREIGN KEY FK7C6F99E47A30F664;
alter table DnsServerLink DROP FOREIGN KEY FK95D7EBC6FF686EC6;
alter table EnumServerLink DROP FOREIGN KEY FK509DA9DE131CB3B8;
alter table EnumServiceLink DROP FOREIGN KEY FK7E6AD86E1EB2137E;
alter table EsmeLink DROP FOREIGN KEY FK82D15C001FD79F9A;
alter table EsmeLocalConnectionLink DROP FOREIGN KEY FK19C0DF7DEF8B8ABD;
alter table EsmeNetworkConnectionLink DROP FOREIGN KEY FK555BE280230E5900;
alter table EsmeQueuedMessageLink DROP FOREIGN KEY FK27D09EE813B9AEE8;
alter table ExternalIPLink DROP FOREIGN KEY FK95982D6C58173746;
alter table FileSystem DROP FOREIGN KEY FKE27C22EB96E58345;
alter table GeoRedSMSReplLink DROP FOREIGN KEY FK848478C2F8ECE242;
alter table GeoRedSMSReplThrottleLink DROP FOREIGN KEY FKC3A28B0C9155018C;
alter table HssServerLink DROP FOREIGN KEY FK765C40A5DFECC3A5;
alter table HssServerThrottleLink DROP FOREIGN KEY FK9971BFEF855ACFEF;
alter table HssServerUnavailableConnection DROP FOREIGN KEY FKD6908A0375BEB9DD;
alter table ImsGsmGwClientThrottleLink DROP FOREIGN KEY FKAC1D24B14F27AC3B;
alter table ImsGsmGwServerLink DROP FOREIGN KEY FKD4646DF1FEA3A39;
alter table ImsGsmGwServerThrottleLink DROP FOREIGN KEY FKD876EC29BB127283;
alter table InterMateLink DROP FOREIGN KEY FK46627D7BAFF3007B;
alter table LnpServerLink DROP FOREIGN KEY FK4B42078BB4D28A8B;
alter table LoadBalancerLink DROP FOREIGN KEY FK1B954A567A5EE0F0;
alter table M2paLink DROP FOREIGN KEY FK584BE350F55226EA;
alter table M2paLogicalUnit DROP FOREIGN KEY FKEB3D3D17729E9B57;
alter table M2paSystemInfo DROP FOREIGN KEY FKEACB7653AD4A802D;
alter table MateLink DROP FOREIGN KEY FK159C84BFB2A2C859;
alter table MessagingSystem DROP FOREIGN KEY FKC152FA33619A3543;
alter table Mm1Link DROP FOREIGN KEY FKA616B20BE71C34B;
alter table MpSystemConfig DROP FOREIGN KEY FKE3C13B44C4BC5CE;
alter table MsgArchiveServerLink DROP FOREIGN KEY FKBDC6CADEF720EAF8;
alter table MsgBladeLU DROP FOREIGN KEY FK419A7D0077B3E8A;
alter table MsgBladeLUContainer DROP FOREIGN KEY FK26D58F61A8BA28F1;
alter table MsmMemUtilization DROP FOREIGN KEY FK76872454EAEF8DD4;
alter table PeerMMSCLink DROP FOREIGN KEY FK46A21B8CC61A68A6;
alter table PersonalizationConfig DROP FOREIGN KEY FK1AB2AA6669BBA66;
alter table PrepaidServerLink DROP FOREIGN KEY FKA774430C1BDCAC8C;
alter table PrepaidServerThrottleLink DROP FOREIGN KEY FK62F06B5630A2E1D6;
alter table QueueOverflow DROP FOREIGN KEY FK1DCC013FEC413E3;
alter table RemoteSMSCThrottleLink DROP FOREIGN KEY FK6F80ADF4FAB8CACE;
alter table SCSCFClientThrottleLink DROP FOREIGN KEY FKA573ABF526226405;
alter table SCSCFServerThrottleLink DROP FOREIGN KEY FKD1CD736DA7981EAD;
alter table SmppClientLink DROP FOREIGN KEY FK2FCC7FFC57BD1D9;
alter table SmppServerLink DROP FOREIGN KEY FK5471877716F09151;
alter table SmscLink DROP FOREIGN KEY FKFDDD51649AE394FE;
alter table SmscLocalConnectionLink DROP FOREIGN KEY FK7DA6F5995371A0D9;
alter table SmscNetworkConnectionLink DROP FOREIGN KEY FK5814E19C25C7581C;
alter table SmscQueuedMessageLink DROP FOREIGN KEY FKB33D8C049F269C04;
alter table SmtpClientLink DROP FOREIGN KEY FK21C35D7BE4426755;
alter table SmtpLink DROP FOREIGN KEY FK495B909D4F9F2A;
alter table SmtpLocalConnectionLink DROP FOREIGN KEY FK4D77CDED2342792D;
alter table SmtpNetworkConnectionLink DROP FOREIGN KEY FK7710F4F044C36B70;
alter table SmtpQueuedMessageLink DROP FOREIGN KEY FK7C50695868397958;
alter table SmtpServerLink DROP FOREIGN KEY FK73381CF335B726CD;
alter table SpamServerLink DROP FOREIGN KEY FKEDE3D226B062DC00;
alter table SpamServerThrottleLink DROP FOREIGN KEY FK135440709E8C5D4A;
alter table SpamServerUnavailableConnection DROP FOREIGN KEY FK2C800AA26D1702E2;
alter table Ss7AssociationLink DROP FOREIGN KEY FKACBD4564BF6138BE;
alter table Ss7PCLink DROP FOREIGN KEY FKA625DD04A3E73984;
alter table Ss7Service DROP FOREIGN KEY FKF96A8CFEBF4B4E88;
alter table StatSizeAuditLink DROP FOREIGN KEY FK17095D608B71C6E0;
alter table StatisticsThreshold DROP FOREIGN KEY FK806CB2682514BF8;
alter table SubLdapServerLink DROP FOREIGN KEY FKF61927246A8190A4;
alter table TapClientLink DROP FOREIGN KEY FK9E0164873709948;
alter table TapLink DROP FOREIGN KEY FK75FF21D6FBB035D;
alter table TapLocalConnectionLink DROP FOREIGN KEY FK2CF9ABC0B831C89A;
alter table TapNetworkConnectionLink DROP FOREIGN KEY FK7D92AA035B1C939D;
alter table TranscoderLink DROP FOREIGN KEY FKB27DF03774FCFA11;
alter table UsageControlServiceLink DROP FOREIGN KEY FK6BBF0A134189B553;
alter table VaspLink DROP FOREIGN KEY FKBB6D42825873861C;
alter table XmlClientLink DROP FOREIGN KEY FKCCAD25BC363DA8BC;
alter table XmlcLocalConnectionLink DROP FOREIGN KEY FK98703D576E3AE897;
alter table XmlcNetworkConnectionLink DROP FOREIGN KEY FKE5AB31DAB35DA85A;

SELECT 'alter table AckFwdSystemLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table AckFwdSystemLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update AckFwdSystemLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = AckFwdSystemLink.MOID group by ManagedObject.MOID);
alter table AckFwdSystemLink drop column MOID;

SELECT 'alter table BackupServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table BackupServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BackupServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BackupServerLink.MOID group by ManagedObject.MOID);
alter table BackupServerLink drop column MOID;

SELECT 'alter table BillingServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table BillingServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BillingServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BillingServerLink.MOID group by ManagedObject.MOID);
alter table BillingServerLink drop column MOID;

SELECT 'alter table BmgwClientThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwClientThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BmgwClientThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BmgwClientThrottleLink.MOID group by ManagedObject.MOID);
alter table BmgwClientThrottleLink drop column MOID;

SELECT 'alter table BmgwServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BmgwServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BmgwServerThrottleLink.MOID group by ManagedObject.MOID);
alter table BmgwServerThrottleLink drop column MOID;

SELECT 'alter table BmgwServerUnavailableLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table BmgwServerUnavailableLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update BmgwServerUnavailableLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = BmgwServerUnavailableLink.MOID group by ManagedObject.MOID);
alter table BmgwServerUnavailableLink drop column MOID;

SELECT 'alter table CapacityLicenseLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table CapacityLicenseLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update CapacityLicenseLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = CapacityLicenseLink.MOID group by ManagedObject.MOID);
alter table CapacityLicenseLink drop column MOID;

SELECT 'alter table ClassOfService populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ClassOfService add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ClassOfService set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ClassOfService.MOID group by ManagedObject.MOID);
alter table ClassOfService drop column MOID;

SELECT 'alter table Congestion populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Congestion add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Congestion set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Congestion.MOID group by ManagedObject.MOID);
alter table Congestion drop column MOID;

SELECT 'alter table ContentAdaptationLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ContentAdaptationLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ContentAdaptationLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ContentAdaptationLink.MOID group by ManagedObject.MOID);
alter table ContentAdaptationLink drop column MOID;

SELECT 'alter table DiskUsage populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table DiskUsage add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update DiskUsage set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = DiskUsage.MOID group by ManagedObject.MOID);
alter table DiskUsage drop column MOID;

SELECT 'alter table DnsServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table DnsServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update DnsServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = DnsServerLink.MOID group by ManagedObject.MOID);
alter table DnsServerLink drop column MOID;

SELECT 'alter table EnumServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EnumServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EnumServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EnumServerLink.MOID group by ManagedObject.MOID);
alter table EnumServerLink drop column MOID;

SELECT 'alter table EnumServiceLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EnumServiceLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EnumServiceLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EnumServiceLink.MOID group by ManagedObject.MOID);
alter table EnumServiceLink drop column MOID;

SELECT 'alter table EsmeLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EsmeLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EsmeLink.MOID group by ManagedObject.MOID);
alter table EsmeLink drop column MOID;

SELECT 'alter table EsmeLocalConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeLocalConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EsmeLocalConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EsmeLocalConnectionLink.MOID group by ManagedObject.MOID);
alter table EsmeLocalConnectionLink drop column MOID;

SELECT 'alter table EsmeNetworkConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeNetworkConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EsmeNetworkConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EsmeNetworkConnectionLink.MOID group by ManagedObject.MOID);
alter table EsmeNetworkConnectionLink drop column MOID;

SELECT 'alter table EsmeQueuedMessageLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table EsmeQueuedMessageLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update EsmeQueuedMessageLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = EsmeQueuedMessageLink.MOID group by ManagedObject.MOID);
alter table EsmeQueuedMessageLink drop column MOID;

SELECT 'alter table ExternalIPLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ExternalIPLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ExternalIPLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ExternalIPLink.MOID group by ManagedObject.MOID);
alter table ExternalIPLink drop column MOID;

SELECT 'alter table FileSystem populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table FileSystem add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update FileSystem set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = FileSystem.MOID group by ManagedObject.MOID);
alter table FileSystem drop column MOID;

SELECT 'alter table GeoRedSMSReplLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table GeoRedSMSReplLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update GeoRedSMSReplLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = GeoRedSMSReplLink.MOID group by ManagedObject.MOID);
alter table GeoRedSMSReplLink drop column MOID;

SELECT 'alter table GeoRedSMSReplThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table GeoRedSMSReplThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update GeoRedSMSReplThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = GeoRedSMSReplThrottleLink.MOID group by ManagedObject.MOID);
alter table GeoRedSMSReplThrottleLink drop column MOID;

SELECT 'alter table HssServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update HssServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = HssServerLink.MOID group by ManagedObject.MOID);
alter table HssServerLink drop column MOID;

SELECT 'alter table HssServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update HssServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = HssServerThrottleLink.MOID group by ManagedObject.MOID);
alter table HssServerThrottleLink drop column MOID;

SELECT 'alter table HssServerUnavailableConnection populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table HssServerUnavailableConnection add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update HssServerUnavailableConnection set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = HssServerUnavailableConnection.MOID group by ManagedObject.MOID);
alter table HssServerUnavailableConnection drop column MOID;

SELECT 'alter table ImsGsmGwClientThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwClientThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ImsGsmGwClientThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ImsGsmGwClientThrottleLink.MOID group by ManagedObject.MOID);
alter table ImsGsmGwClientThrottleLink drop column MOID;

SELECT 'alter table ImsGsmGwServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ImsGsmGwServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ImsGsmGwServerLink.MOID group by ManagedObject.MOID);
alter table ImsGsmGwServerLink drop column MOID;

SELECT 'alter table ImsGsmGwServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table ImsGsmGwServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update ImsGsmGwServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = ImsGsmGwServerThrottleLink.MOID group by ManagedObject.MOID);
alter table ImsGsmGwServerThrottleLink drop column MOID;

SELECT 'alter table InterMateLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table InterMateLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update InterMateLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = InterMateLink.MOID group by ManagedObject.MOID);
alter table InterMateLink drop column MOID;

SELECT 'alter table LnpServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table LnpServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LnpServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LnpServerLink.MOID group by ManagedObject.MOID);
alter table LnpServerLink drop column MOID;

SELECT 'alter table LoadBalancerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table LoadBalancerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update LoadBalancerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = LoadBalancerLink.MOID group by ManagedObject.MOID);
alter table LoadBalancerLink drop column MOID;

SELECT 'alter table M2paLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update M2paLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = M2paLink.MOID group by ManagedObject.MOID);
alter table M2paLink drop column MOID;

SELECT 'alter table M2paLogicalUnit populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paLogicalUnit add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update M2paLogicalUnit set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = M2paLogicalUnit.MOID group by ManagedObject.MOID);
alter table M2paLogicalUnit drop column MOID;

SELECT 'alter table M2paSystemInfo populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table M2paSystemInfo add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update M2paSystemInfo set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = M2paSystemInfo.MOID group by ManagedObject.MOID);
alter table M2paSystemInfo drop column MOID;

SELECT 'alter table MateLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MateLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MateLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MateLink.MOID group by ManagedObject.MOID);
alter table MateLink drop column MOID;

SELECT 'alter table MessagingSystem populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MessagingSystem add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MessagingSystem set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MessagingSystem.MOID group by ManagedObject.MOID);
alter table MessagingSystem drop column MOID;

SELECT 'alter table Mm1Link populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Mm1Link add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Mm1Link set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Mm1Link.MOID group by ManagedObject.MOID);
alter table Mm1Link drop column MOID;

SELECT 'alter table MpSystemConfig populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MpSystemConfig add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MpSystemConfig set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MpSystemConfig.MOID group by ManagedObject.MOID);
alter table MpSystemConfig drop column MOID;

SELECT 'alter table MsgArchiveServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgArchiveServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MsgArchiveServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MsgArchiveServerLink.MOID group by ManagedObject.MOID);
alter table MsgArchiveServerLink drop column MOID;

SELECT 'alter table MsgBladeLU populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgBladeLU add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MsgBladeLU set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MsgBladeLU.MOID group by ManagedObject.MOID);
alter table MsgBladeLU drop column MOID;

SELECT 'alter table MsgBladeLUContainer populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsgBladeLUContainer add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MsgBladeLUContainer set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MsgBladeLUContainer.MOID group by ManagedObject.MOID);
alter table MsgBladeLUContainer drop column MOID;

SELECT 'alter table MsmMemUtilization populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table MsmMemUtilization add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update MsmMemUtilization set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = MsmMemUtilization.MOID group by ManagedObject.MOID);
alter table MsmMemUtilization drop column MOID;

SELECT 'alter table PeerMMSCLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PeerMMSCLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PeerMMSCLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PeerMMSCLink.MOID group by ManagedObject.MOID);
alter table PeerMMSCLink drop column MOID;

SELECT 'alter table PersonalizationConfig populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PersonalizationConfig add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PersonalizationConfig set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PersonalizationConfig.MOID group by ManagedObject.MOID);
alter table PersonalizationConfig drop column MOID;

SELECT 'alter table PrepaidServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PrepaidServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PrepaidServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PrepaidServerLink.MOID group by ManagedObject.MOID);
alter table PrepaidServerLink drop column MOID;

SELECT 'alter table PrepaidServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PrepaidServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PrepaidServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PrepaidServerThrottleLink.MOID group by ManagedObject.MOID);
alter table PrepaidServerThrottleLink drop column MOID;

SELECT 'alter table QueueOverflow populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table QueueOverflow add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update QueueOverflow set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = QueueOverflow.MOID group by ManagedObject.MOID);
alter table QueueOverflow drop column MOID;

SELECT 'alter table RemoteSMSCThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table RemoteSMSCThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update RemoteSMSCThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = RemoteSMSCThrottleLink.MOID group by ManagedObject.MOID);
alter table RemoteSMSCThrottleLink drop column MOID;

SELECT 'alter table SCSCFClientThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SCSCFClientThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SCSCFClientThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SCSCFClientThrottleLink.MOID group by ManagedObject.MOID);
alter table SCSCFClientThrottleLink drop column MOID;

SELECT 'alter table SCSCFServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SCSCFServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SCSCFServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SCSCFServerThrottleLink.MOID group by ManagedObject.MOID);
alter table SCSCFServerThrottleLink drop column MOID;

SELECT 'alter table SmppClientLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmppClientLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmppClientLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmppClientLink.MOID group by ManagedObject.MOID);
alter table SmppClientLink drop column MOID;

SELECT 'alter table SmppServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmppServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmppServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmppServerLink.MOID group by ManagedObject.MOID);
alter table SmppServerLink drop column MOID;

SELECT 'alter table SmscLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmscLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmscLink.MOID group by ManagedObject.MOID);
alter table SmscLink drop column MOID;

SELECT 'alter table SmscLocalConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscLocalConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmscLocalConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmscLocalConnectionLink.MOID group by ManagedObject.MOID);
alter table SmscLocalConnectionLink drop column MOID;

SELECT 'alter table SmscNetworkConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscNetworkConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmscNetworkConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmscNetworkConnectionLink.MOID group by ManagedObject.MOID);
alter table SmscNetworkConnectionLink drop column MOID;

SELECT 'alter table SmscQueuedMessageLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmscQueuedMessageLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmscQueuedMessageLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmscQueuedMessageLink.MOID group by ManagedObject.MOID);
alter table SmscQueuedMessageLink drop column MOID;

SELECT 'alter table SmtpClientLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpClientLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpClientLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpClientLink.MOID group by ManagedObject.MOID);
alter table SmtpClientLink drop column MOID;

SELECT 'alter table SmtpLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpLink.MOID group by ManagedObject.MOID);
alter table SmtpLink drop column MOID;

SELECT 'alter table SmtpLocalConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpLocalConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpLocalConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpLocalConnectionLink.MOID group by ManagedObject.MOID);
alter table SmtpLocalConnectionLink drop column MOID;

SELECT 'alter table SmtpNetworkConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpNetworkConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpNetworkConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpNetworkConnectionLink.MOID group by ManagedObject.MOID);
alter table SmtpNetworkConnectionLink drop column MOID;

SELECT 'alter table SmtpQueuedMessageLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpQueuedMessageLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpQueuedMessageLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpQueuedMessageLink.MOID group by ManagedObject.MOID);
alter table SmtpQueuedMessageLink drop column MOID;

SELECT 'alter table SmtpServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SmtpServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SmtpServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SmtpServerLink.MOID group by ManagedObject.MOID);
alter table SmtpServerLink drop column MOID;

SELECT 'alter table SpamServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SpamServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SpamServerLink.MOID group by ManagedObject.MOID);
alter table SpamServerLink drop column MOID;

SELECT 'alter table SpamServerThrottleLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerThrottleLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SpamServerThrottleLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SpamServerThrottleLink.MOID group by ManagedObject.MOID);
alter table SpamServerThrottleLink drop column MOID;

SELECT 'alter table SpamServerUnavailableConnection populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SpamServerUnavailableConnection add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SpamServerUnavailableConnection set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SpamServerUnavailableConnection.MOID group by ManagedObject.MOID);
alter table SpamServerUnavailableConnection drop column MOID;

SELECT 'alter table Ss7AssociationLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7AssociationLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Ss7AssociationLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Ss7AssociationLink.MOID group by ManagedObject.MOID);
alter table Ss7AssociationLink drop column MOID;

SELECT 'alter table Ss7PCLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7PCLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Ss7PCLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Ss7PCLink.MOID group by ManagedObject.MOID);
alter table Ss7PCLink drop column MOID;

SELECT 'alter table Ss7Service populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table Ss7Service add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update Ss7Service set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = Ss7Service.MOID group by ManagedObject.MOID);
alter table Ss7Service drop column MOID;

SELECT 'alter table StatSizeAuditLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table StatSizeAuditLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update StatSizeAuditLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = StatSizeAuditLink.MOID group by ManagedObject.MOID);
alter table StatSizeAuditLink drop column MOID;

SELECT 'alter table StatisticsThreshold populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table StatisticsThreshold add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update StatisticsThreshold set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = StatisticsThreshold.MOID group by ManagedObject.MOID);
alter table StatisticsThreshold drop column MOID;

SELECT 'alter table SubLdapServerLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table SubLdapServerLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update SubLdapServerLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = SubLdapServerLink.MOID group by ManagedObject.MOID);
alter table SubLdapServerLink drop column MOID;

SELECT 'alter table TapClientLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapClientLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TapClientLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TapClientLink.MOID group by ManagedObject.MOID);
alter table TapClientLink drop column MOID;

SELECT 'alter table TapLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TapLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TapLink.MOID group by ManagedObject.MOID);
alter table TapLink drop column MOID;

SELECT 'alter table TapLocalConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapLocalConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TapLocalConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TapLocalConnectionLink.MOID group by ManagedObject.MOID);
alter table TapLocalConnectionLink drop column MOID;

SELECT 'alter table TapNetworkConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TapNetworkConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TapNetworkConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TapNetworkConnectionLink.MOID group by ManagedObject.MOID);
alter table TapNetworkConnectionLink drop column MOID;

SELECT 'alter table TranscoderLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table TranscoderLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update TranscoderLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = TranscoderLink.MOID group by ManagedObject.MOID);
alter table TranscoderLink drop column MOID;

SELECT 'alter table UsageControlServiceLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table UsageControlServiceLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update UsageControlServiceLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = UsageControlServiceLink.MOID group by ManagedObject.MOID);
alter table UsageControlServiceLink drop column MOID;

SELECT 'alter table VaspLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table VaspLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update VaspLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = VaspLink.MOID group by ManagedObject.MOID);
alter table VaspLink drop column MOID;

SELECT 'alter table XmlClientLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlClientLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update XmlClientLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = XmlClientLink.MOID group by ManagedObject.MOID);
alter table XmlClientLink drop column MOID;

SELECT 'alter table XmlcLocalConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlcLocalConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update XmlcLocalConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = XmlcLocalConnectionLink.MOID group by ManagedObject.MOID);
alter table XmlcLocalConnectionLink drop column MOID;

SELECT 'alter table XmlcNetworkConnectionLink populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table XmlcNetworkConnectionLink add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update XmlcNetworkConnectionLink set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = XmlcNetworkConnectionLink.MOID group by ManagedObject.MOID);
alter table XmlcNetworkConnectionLink drop column MOID;

####Added on Feb-24-2017
SELECT 'alter table PhysicalEntity populate NAME and drop MOID column' AS 'MIGRATION PROCESS STATUS ... ';
alter table PhysicalEntity add column NAME varchar(100) default NULL, add column OWNERNAME varchar(25) NOT NULL after MOID;
update PhysicalEntity set NAME=(select ManagedObject.NAME from ManagedObject where ManagedObject.MOID = PhysicalEntity.MOID group by ManagedObject.MOID);
alter table PhysicalEntity drop column MOID;

SELECT 'alter ManagedObject and hierarchy tables to drop MOID' AS 'MIGRATION PROCESS STATUS ... ';
alter table IpAddress drop column MOID;
alter table ManagedGroupObject drop column MOID;
alter table Network drop column MOID;
alter table Node drop column MOID;
alter table PortObject drop column MOID;
alter table Printer drop column MOID;
alter table SnmpInterface drop column MOID;
alter table SnmpNode drop column MOID;
alter table SwitchObject drop column MOID;
alter table TL1Interface drop column MOID;
alter table TL1Node drop column MOID;
alter table TOPOUSERPROPS drop column MOID;
alter table TopoObject drop column MOID;
alter table ManagedObject drop column MOID;
alter table CORBANode drop column MOID;

drop table PanelTree;

create table PanelTree (NODEID VARCHAR(100) NOT NULL,NODETYPE VARCHAR(100) NOT NULL,USERNAME VARCHAR(50) NOT NULL,PARENT VARCHAR(100),PREVIOUSNODE VARCHAR(100), MODULENAME varchar(30),PRIMARY KEY (NODEID,USERNAME));
create table PanelProps (NODEID VARCHAR(100) NOT NULL,USERNAME VARCHAR(50) NOT NULL,ATTRIBNAME VARCHAR(30) NOT NULL,ATTRIBVALUE VARCHAR(100),PRIMARY KEY (NODEID,USERNAME,ATTRIBNAME));
