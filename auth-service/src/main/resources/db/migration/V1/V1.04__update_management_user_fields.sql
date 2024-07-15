-- copy username over from account to token table
update auth.auth_token as t
set cre_user = a.username,
    mut_user = a.username
from auth.account as a
where t.account_id = a.id;