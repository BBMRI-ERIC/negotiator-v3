create sequence NOTIFICATION_ID_SEQ start with 10000 increment by 1;
create sequence PERSON_ID_SEQ start with 10000 increment by 50;
create sequence RESOURCE_ID_SEQ start with 10000 increment by 1;

create table access_criteria (
  access_criteria_section_id bigint,
  id bigint generated by default as identity,
  description varchar(255) not null,
  label varchar(255) not null,
  name varchar(255) not null,
  type varchar(255) not null,
  primary key (id)
 );

create table access_criteria_section (
  access_criteria_set_id bigint, 
  id bigint generated by default as identity,
  description varchar(255) not null,
  label varchar(255) not null, 
  name varchar(255) not null, 
  primary key (id)
);

create table access_criteria_section_link (
  ordering integer not null, 
  required boolean not null, 
  access_criteria_id bigint not null, 
  access_criteria_section_id bigint not null, 
  primary key (access_criteria_id, access_criteria_section_id)
);

create table access_criteria_set (
  id bigint generated by default as identity,
  name varchar(255),
  primary key (id)
);

create table attachment (
  created_by bigint, 
  creation_date timestamp(6), 
  modified_by bigint, 
  modified_date timestamp(6), 
  organization_id bigint, 
  size bigint, 
  content_type varchar(255), 
  id varchar(255) not null, 
  name varchar(255), 
  negotiation_id varchar(255), 
  payload BYTEA, 
  primary key (id)
);

create table authorities (
  id bigint generated by default as identity, 
  person_id bigint, 
  authority varchar(255), 
  primary key (id)
);

create table data_source (
  sync_active boolean not null, 
  id bigint generated by default as identity,
  api_password varchar(255) not null,
  api_type varchar(255) not null check (api_type in ('MOLGENIS')), 
  api_url varchar(255) not null, 
  api_username varchar(255) not null, 
  description varchar(255), 
  name varchar(255) not null, 
  resource_biobank varchar(255) not null, 
  resource_collection varchar(255) not null, 
  resource_network varchar(255) not null, 
  source_prefix varchar(255),
   url varchar(255) not null unique, 
   primary key (id)
);

create table negotiation (
  posts_enabled boolean, 
  created_by bigint, 
  creation_date timestamp(6), 
  modified_by bigint, 
  modified_date timestamp(6), 
  current_state varchar(255) check (current_state in ('SUBMITTED','APPROVED','DECLINED','IN_PROGRESS','PAUSED','CONCLUDED','ABANDONED')), 
  id varchar(255) not null, 
  payload json, 
  primary key (id)
);

create table negotiation_lifecycle_record (
  id bigint generated by default as identity,
  recorded_at timestamp(6),
  changed_to varchar(255) check (changed_to in ('SUBMITTED','APPROVED','DECLINED','IN_PROGRESS','PAUSED','CONCLUDED','ABANDONED')),
  negotiation_id varchar(255), 
  primary key (id)
);
create table notification (
  id bigint not null,
  recipient_id bigint,
  email_status varchar(255) check (email_status in ('EMAIL_SENT','EMAIL_NOT_SENT')), 
  message TEXT, 
  negotiation_id varchar(255), 
  primary key (id)
);
create table notification_email (
  was_successfully_sent boolean not null, 
  id bigint generated by default as identity,
  person_id bigint,
  message TEXT,
  sent_at timestamp(6),
  primary key (id)
);
create table organization (
  id bigint not null,
  external_id varchar(255) not null unique,
  name varchar(255), 
  primary key (id)
);
create table person (
  admin boolean default false not null, 
  is_service_account boolean default false not null, 
  id bigint not null,
  email varchar(255) not null,
  name varchar(255) not null, 
  organization varchar(255), 
  password varchar(255), 
  subject_id varchar(255) not null unique, 
  primary key (id)
);
create table person_negotiation_role (
  person_id bigint not null,
  role_id bigint not null, 
  negotiation_id varchar(255) not null, 
  primary key (person_id, role_id, negotiation_id)
);
create table person_project_link (
  person_id bigint not null, 
  project_id varchar(255) not null, 
  primary key (person_id, project_id)
);
create table person_project_role (
  person_id bigint not null,
  role_id bigint unique, project_id varchar(255) not null, primary key (person_id, project_id));
create table post (
  created_by bigint, 
  creation_date timestamp(6), 
  modified_by bigint, 
  modified_date timestamp(6), 
  organization_id bigint, 
  id varchar(255) not null, 
  request_id varchar(255), 
  status varchar(255) check (status in ('CREATED','READ')), 
  text TEXT, 
  type varchar(255) check (type in ('PRIVATE','PUBLIC')), 
  primary key (id)
);
create table project (
  created_by bigint, 
  creation_date timestamp(6), 
  modified_by bigint, 
  modified_date timestamp(6), 
  id varchar(255) not null,
  payload json not null, 
  primary key (id)
);
create table request (
  data_source_id bigint not null, 
  human_readable TEXT not null, 
  id varchar(255) not null, 
  negotiation_id varchar(255), 
  url TEXT not null, 
  primary key (id)
);
create table request_resources_link (
  resource_id bigint not null, 
  request_id varchar(255) not null, 
  primary key (resource_id, request_id)
);
create table resource (
  access_criteria_set_id bigint, 
  data_source_id bigint not null, 
  id bigint not null, 
  organization_id bigint not null, 
  description VARCHAR(5000), 
  name varchar(255), 
  source_id varchar(255) not null, 
  primary key (id)
);
create table resource_representative_link (
  person_id bigint not null, 
  resource_id bigint not null, 
  primary key (person_id, resource_id)
);
create table resource_state_per_negotiation (
  current_state varchar(255) check (current_state in ('SUBMITTED','REPRESENTATIVE_CONTACTED','REPRESENTATIVE_UNREACHABLE','RETURNED_FOR_RESUBMISSION','CHECKING_AVAILABILITY','RESOURCE_AVAILABLE','RESOURCE_UNAVAILABLE_WILLING_TO_COLLECT','RESOURCE_UNAVAILABLE','ACCESS_CONDITIONS_INDICATED','ACCESS_CONDITIONS_MET','RESOURCE_NOT_MADE_AVAILABLE','RESOURCE_MADE_AVAILABLE')), 
  negotiation_id varchar(255) not null, 
  resource_id varchar(255) not null, 
  primary key (negotiation_id, resource_id)
);
create table role (
  id bigint generated by default as identity, 
  name varchar(255), 
  primary key (id)
);
alter table if exists access_criteria add constraint FKt4trpxmxhenaxg0i4duggiocr foreign key (access_criteria_section_id) references access_criteria_section;
alter table if exists access_criteria_section add constraint FKijwhm9gjj8qdo5eu09iibxdbo foreign key (access_criteria_set_id) references access_criteria_set;
alter table if exists access_criteria_section_link add constraint FKhwqk0a4nxt3p18e97l4llg4bs foreign key (access_criteria_id) references access_criteria;
alter table if exists access_criteria_section_link add constraint FKcgj2extn02c91q2ld1xvp54di foreign key (access_criteria_section_id) references access_criteria_section;
alter table if exists attachment add constraint FK963a0q97mrss1fxy1f51vfm0b foreign key (created_by) references person;
alter table if exists attachment add constraint FKsbxoy0aohg40gdhkg2ks7ak9y foreign key (modified_by) references person;
alter table if exists attachment add constraint FKhgi1r4d3ylfiqalc4qlru8yhu foreign key (negotiation_id) references negotiation;
alter table if exists attachment add constraint FKtqlmqtq11hl9mhchr4sna6rrh foreign key (organization_id) references organization;
alter table if exists authorities add constraint FKn55wpgxf39f3kda1tw6ia2mgn foreign key (person_id) references person;
alter table if exists negotiation add constraint FK62916aq48ihicemrlbwk8cj10 foreign key (created_by) references person;
alter table if exists negotiation add constraint FKnb8a0248715b96boouf3973r7 foreign key (modified_by) references person;
alter table if exists negotiation_lifecycle_record add constraint FKhmbnhi74t9tve5ghbf7hb7r0p foreign key (negotiation_id) references negotiation;
alter table if exists notification add constraint FKq1tn3w9gjmypghc58magu8i2t foreign key (negotiation_id) references negotiation;
alter table if exists notification add constraint FK7wywiqhssp9wjk9ijg0lms4hv foreign key (recipient_id) references person;
alter table if exists notification_email add constraint FKjsocvwlnpmpp0ftmo5nm8nmm7 foreign key (person_id) references person;
alter table if exists person_negotiation_role add constraint FKp514tee0gwjcd11yycx40j17m foreign key (negotiation_id) references negotiation;
alter table if exists person_negotiation_role add constraint FKp6fcqx8iyy2wcrykniqcic2ge foreign key (person_id) references person;
alter table if exists person_negotiation_role add constraint FKa5uex2honkxkp7a4f7lxrgikn foreign key (role_id) references role;
alter table if exists person_project_link add constraint FK77bria9ttghyi5jhgc9k88pk foreign key (project_id) references project;
alter table if exists person_project_link add constraint FK381qbe3krq75p31d7u25siedv foreign key (person_id) references person;
alter table if exists person_project_role add constraint FKqt0afjgr9fxqyjxeox9xxd4c foreign key (person_id) references person;
alter table if exists person_project_role add constraint FKbdqx0q8bawb93ktue0f772rm1 foreign key (project_id) references project;
alter table if exists person_project_role add constraint FKmnc7g89mav2nlhshfnm4ta4dn foreign key (role_id) references role;
alter table if exists post add constraint FKfs4ns7nd5pb3k72nbt8g2xq3d foreign key (created_by) references person;
alter table if exists post add constraint FK8k515auw0oorgyqymc1tnwat1 foreign key (modified_by) references person;
alter table if exists post add constraint FK3bl88xxkqs4cgun3vk9ur3shu foreign key (request_id) references negotiation;
alter table if exists post add constraint FKowe48u8aic9c83l1edrgmft53 foreign key (organization_id) references organization;
alter table if exists project add constraint FKqwy6ntg495nxuiyc6prb6o8h0 foreign key (created_by) references person;
alter table if exists project add constraint FKjyr9p0nibm4qmaddopb52ksa0 foreign key (modified_by) references person;
alter table if exists request add constraint FK1dblesuyfmaxqj8vvs43ttiut foreign key (data_source_id) references data_source;
alter table if exists request add constraint FKr3i632eg511015o18i0s6x014 foreign key (negotiation_id) references negotiation;
alter table if exists request_resources_link add constraint FKblq0pwafdnlwdk4l3me652hfw foreign key (resource_id) references resource;
alter table if exists request_resources_link add constraint FKl8i1lbs2jfsl7or12i0hi9qkb foreign key (request_id) references request;
alter table if exists resource add constraint FK1xa946oabsglyyf25u09d0nuu foreign key (access_criteria_set_id) references access_criteria_set;
alter table if exists resource add constraint FK2c4lb6ow7camgvn82itk6b68j foreign key (data_source_id) references data_source;
alter table if exists resource add constraint FKnxy1sei1miecaw4aju99ce32u foreign key (organization_id) references organization;
alter table if exists resource_representative_link add constraint FKpda0l0e7a0kk7aj8d7dd8blr2 foreign key (resource_id) references resource;
alter table if exists resource_representative_link add constraint FKey1ddlsd86tu78rxjnqaaapwa foreign key (person_id) references person;
alter table if exists resource_state_per_negotiation add constraint FKc738qoch93u1ybjmiydhf2h58 foreign key (negotiation_id) references negotiation;