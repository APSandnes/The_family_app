-- D7: lightweight presence. last_active_at is bumped when a user foregrounds the
-- app; the chat header shows "Active now" / "Active {relative}". Safe to re-run.
alter table public.users add column if not exists last_active_at timestamptz;
