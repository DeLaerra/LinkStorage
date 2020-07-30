--liquibase formatted sql

--changeset gaiduk:20200717-insert-tables runOnChange:true context:test
CREATE TABLE "usercreds" (
                             "uid" bigint NOT NULL,
                             "role_uid" int NOT NULL,
                             "username" varchar(100) NOT NULL UNIQUE,
                             "password" varchar(100) NOT NULL,
                             "active" BOOLEAN NOT NULL,
                             "email" varchar(255) NOT NULL,
                             "date_registration" DATE NOT NULL,
                             CONSTRAINT "user_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );



CREATE TABLE "role" (
                        "uid" serial NOT NULL UNIQUE,
                        "role" varchar(25) NOT NULL,
                        CONSTRAINT "role_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );



CREATE TABLE "friends" (
                           "id" serial NOT NULL,
                           "user_owner" bigint NOT NULL,
                           "user_friend" bigint NOT NULL,
                           CONSTRAINT "friends_pk" PRIMARY KEY ("id")
) WITH (
      OIDS=FALSE
      );

CREATE TABLE "adding_method" (
                                 "uid" serial NOT NULL,
                                 "method" varchar(50) NOT NULL,
                                 CONSTRAINT "adding_method_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );



CREATE TABLE "access_level" (
                                "uid" serial NOT NULL,
                                "level" varchar(20) NOT NULL,
                                CONSTRAINT "access_level_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );

--changeset p.gaiduk:20200726-insert-ref-description runOnChange:true context:test
CREATE TABLE "ref_description" (
                                   "uid" serial NOT NULL,
                                   "uid_user" bigint NOT NULL,
                                   "uid_reference" bigint NOT NULL,
                                   "name" varchar(255) NOT NULL,
                                   "description" varchar(255),
                                   "uid_reference_type" bigint NOT NULL,
                                   "adding_date" DATE NOT NULL,
                                   "source" varchar(255),
                                   "uid_adding_method" bigint NOT NULL,
                                   "uid_access_level" bigint NOT NULL,
                                   "uid_parent_ref" bigint,
                                   CONSTRAINT "ref_description_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );




CREATE TABLE "reference_type" (
                                  "uid" serial NOT NULL,
                                  "type" varchar(100) NOT NULL,
                                  CONSTRAINT "reference_type_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );



CREATE TABLE "user_info" (
                             "uid" serial NOT NULL,
                             "uid_user" bigint NOT NULL,
                             "name" varchar(255) NOT NULL DEFAULT 'name',
                             "surname" varchar(255) NOT NULL DEFAULT 'name',
                             "age" smallint NOT NULL,
                             "sex" smallint NOT NULL,
                             "birth_date" DATE NOT NULL,
                             "avatar" BYTEA,
                             CONSTRAINT "user_info_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );

--changeset r.khokhlov:20200717-insert-tables runOnChange:true context:test
CREATE TABLE confirmation_token
(
    confirmation_token text,
    created_date date,
    token_id bigint NOT NULL,
    uid bigint,
    CONSTRAINT confirmation_token_pkey PRIMARY KEY (token_id)
) WITH (
      OIDS=FALSE
      );

--changeset p.gaiduk:20200726-insert-tags-refs runOnChange:true context:test
CREATE TABLE "refs" (
                        "uid" serial NOT NULL,
                        "url" varchar(255) NOT NULL UNIQUE,
                        "rating" int NOT NULL,
                        CONSTRAINT "refs_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );

CREATE TABLE "tags" (
                        "uid" serial NOT NULL,
                        "name" varchar(255) NOT NULL,
                        CONSTRAINT "tags_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );



CREATE TABLE "tags_refs" (
                             "uid" serial NOT NULL,
                             "uid_ref_description" bigint NOT NULL,
                             "uid_tag" bigint NOT NULL,
                             CONSTRAINT "tags_refs_pk" PRIMARY KEY ("uid")
) WITH (
      OIDS=FALSE
      );


ALTER TABLE "usercreds" ADD CONSTRAINT "user_fk0" FOREIGN KEY ("role_uid") REFERENCES "role"("uid");

ALTER TABLE "friends" ADD CONSTRAINT "friends_fk0" FOREIGN KEY ("user_owner") REFERENCES "usercreds"("uid");
ALTER TABLE "friends" ADD CONSTRAINT "friends_fk1" FOREIGN KEY ("user_friend") REFERENCES "usercreds"("uid");

ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk0" FOREIGN KEY ("uid_user") REFERENCES "usercreds"("uid");
ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk1" FOREIGN KEY ("uid_reference") REFERENCES "refs"("uid");
ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk2" FOREIGN KEY ("uid_reference_type") REFERENCES "reference_type"("uid");
ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk3" FOREIGN KEY ("uid_adding_method") REFERENCES "adding_method"("uid");
ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk4" FOREIGN KEY ("uid_access_level") REFERENCES "access_level"("uid");
ALTER TABLE "ref_description" ADD CONSTRAINT "ref_description_fk5" FOREIGN KEY ("uid_parent_ref") REFERENCES "ref_description"("uid");


ALTER TABLE "user_info" ADD CONSTRAINT "user_info_fk0" FOREIGN KEY ("uid_user") REFERENCES "usercreds"("uid");

ALTER TABLE "tags_refs" ADD CONSTRAINT "tags_refs_fk0" FOREIGN KEY ("uid_ref_description") REFERENCES "ref_description"("uid");
ALTER TABLE "tags_refs" ADD CONSTRAINT "tags_refs_fk1" FOREIGN KEY ("uid_tag") REFERENCES "tags"("uid");

CREATE SEQUENCE hibernate_sequence START 1;

--insert roles
INSERT INTO "role" ("uid", "role")
VALUES (0, 'USER');
INSERT INTO "role" ("uid", "role")
VALUES (1, 'ADMIN');

--insert access levels
INSERT INTO "access_level" ("uid" , "level")
VALUES (0, 'PUBLIC');
INSERT INTO "access_level" ("uid" , "level")
VALUES (1, 'PRIVATE');

--insert adding methods
INSERT INTO "adding_method" ("uid" , "method")
VALUES (0, 'SITE');
INSERT INTO "adding_method" ("uid" , "method")
VALUES (1, 'MAIL');
INSERT INTO "adding_method" ("uid" , "method")
VALUES (2, 'TG');

--insert reference types
INSERT INTO "reference_type" ("uid", "type")
VALUES (0, 'TEXT');
INSERT INTO "reference_type" ("uid", "type")
VALUES (1, 'VIDEO');
INSERT INTO "reference_type" ("uid", "type")
VALUES (2, 'FILE');
INSERT INTO "reference_type" ("uid", "type")
VALUES (3, 'PIC');


