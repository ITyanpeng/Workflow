CREATE OR REPLACE FORCE VIEW act_id_user (id_,
                                                  rev_,
                                                  first_,
                                                  last_,
                                                  email_,
                                                  pwd_,
                                                  picture_id_
                                                 )
AS
   SELECT reg_no AS id_, 1 AS rev_, reger AS first_, '' AS last_,
          email AS email_, pwd AS pwd_, '' AS picture_id_
     FROM accounts;
     
-- own 为WORKFLOW的角色流程专用     
create or replace view ACT_ID_GROUP as
select to_char(code) as ID_ ,1 as REV_,  name as NAME_ , 'assignment' AS TYPE_  from roles a where own='WORKFLOW'

--为了在流程定义中使用roles.code
create or replace view  act_id_membership  AS  
select A.REG_NO as USER_ID_, to_char(b.code) as GROUP_ID_ from r_accounts_roles r, accounts a, roles b
where A.ACCOUNT_ID=R.ACCOUNT_ID and r.role_id=b.role_id
