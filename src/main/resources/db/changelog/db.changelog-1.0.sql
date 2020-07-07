--liquibase formatted sql

--changeset balandin:20200705-create-table-products runOnChange:true context:prod and test
create  table  products  (
                           uuid  varchar(50)  primary  key,
                           name  varchar(255)
);
--rollback drop table products;

--changeset balandin:20200705-insert-table-products runOnChange:true context:test
insert  into  products  (uuid,  name)  values  (1,  'name  1');
insert  into  products  (uuid,  name)  values  (2,  'name  2');
