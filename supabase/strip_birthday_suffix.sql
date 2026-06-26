-- D2 (B3): drop the redundant " birthday" suffix from auto-synced birthday rows.
-- New rows are inserted with just the member's name (see FamilyRepository.syncUserBirthday);
-- this back-fills existing rows. Safe to re-run — only touches rows whose name is exactly
-- "<member name> birthday".
update public.birthdays b
set name = u.name
from public.users u
where b.user_id = u.id
  and b.name = u.name || ' birthday';
