-- D6b follow-up: richer wishes — optional link, price, and image.
-- Visible to all family members (not secret); reservation stays hidden via wish_reservations.
-- Safe to re-run.
alter table public.wishes add column if not exists link text;
alter table public.wishes add column if not exists price text;
alter table public.wishes add column if not exists image_url text;
