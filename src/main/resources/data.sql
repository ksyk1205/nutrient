

insert into member(id,member_id,name) values (1,'testMemberId1', 'martin');
call next value for hibernate_sequence;

insert into member(id,member_id,name) values (2,'testMemberId2', 'dennis');
call next value for hibernate_sequence;

insert into SUPPLEMENT(id,name,ranking) values(1,'testName',0.0);
call next value for hibernate_sequence;