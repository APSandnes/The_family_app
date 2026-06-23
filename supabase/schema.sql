-- The Family App — Supabase PostgreSQL Schema
-- Run this in the Supabase SQL Editor at https://supabase.com/dashboard → your project → SQL Editor
-- NOTE: Supabase Auth handles the auth.users table automatically. Do not create it manually.

-- ────────────────────────────────────────────────────────────
-- Families
-- ────────────────────────────────────────────────────────────
create table if not exists public.families (
    id          uuid primary key default gen_random_uuid(),
    name        text not null unique,
    join_code   text not null,
    admin_id    uuid,                    -- references public.users, set after users table exists
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Users (linked 1-to-1 with auth.users)
-- ────────────────────────────────────────────────────────────
create table if not exists public.users (
    id          uuid primary key default gen_random_uuid(),
    auth_id     uuid unique references auth.users(id) on delete cascade,
    name        text not null,
    email       text not null unique,
    birthday    text not null default '',
    mobile      text not null default '',
    family_id   uuid references public.families(id) on delete set null,
    avatar_color int not null default 0,
    avatar_url  text,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

-- Back-fill the families.admin_id FK now that users exists
alter table public.families
    add constraint families_admin_id_fkey
    foreign key (admin_id) references public.users(id) on delete set null;

-- ────────────────────────────────────────────────────────────
-- Shopping
-- ────────────────────────────────────────────────────────────
create table if not exists public.shopping_lists (
    id            uuid primary key default gen_random_uuid(),
    title         text not null,
    owner_user_id uuid not null references public.users(id) on delete cascade,
    family_id     uuid references public.families(id) on delete cascade,
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now(),
    deleted_at    timestamptz
);

create table if not exists public.shopping_items (
    id           uuid primary key default gen_random_uuid(),
    list_id      uuid not null references public.shopping_lists(id) on delete cascade,
    item         text not null,
    checked      boolean not null default false,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    deleted_at   timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Meal plans
-- ────────────────────────────────────────────────────────────
create table if not exists public.meal_plans (
    id          uuid primary key default gen_random_uuid(),
    family_id   uuid not null references public.families(id) on delete cascade,
    from_date   text not null,
    to_date     text not null,
    week        int not null,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

create table if not exists public.meal_plan_days (
    id           uuid primary key default gen_random_uuid(),
    meal_plan_id uuid not null references public.meal_plans(id) on delete cascade,
    day          text not null,
    date         text not null,
    food         text not null default '',
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    deleted_at   timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Calendar events
-- ────────────────────────────────────────────────────────────
create table if not exists public.calendar_events (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references public.users(id) on delete cascade,
    family_id   uuid references public.families(id) on delete cascade,
    date_from   text not null,
    date_to     text not null,
    time_from   text not null default '',
    time_to     text not null default '',
    activity    text not null,
    all_day     boolean not null default false,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Birthdays
-- ────────────────────────────────────────────────────────────
create table if not exists public.birthdays (
    id               uuid primary key default gen_random_uuid(),
    name             text not null,
    date             text not null,
    family_id        uuid references public.families(id) on delete cascade,
    user_id          uuid references public.users(id) on delete cascade,
    made_by_user_id  uuid not null references public.users(id) on delete cascade,
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),
    deleted_at       timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Wishlists
-- ────────────────────────────────────────────────────────────
create table if not exists public.wishlists (
    id            uuid primary key default gen_random_uuid(),
    owner_user_id uuid not null references public.users(id) on delete cascade,
    name          text not null,
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now(),
    deleted_at    timestamptz
);

create table if not exists public.wishes (
    id          uuid primary key default gen_random_uuid(),
    wishlist_id uuid not null references public.wishlists(id) on delete cascade,
    user_id     uuid not null references public.users(id) on delete cascade,
    text        text not null,
    checked     boolean not null default false,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Chat
-- ────────────────────────────────────────────────────────────
create table if not exists public.conversations (
    id          uuid primary key default gen_random_uuid(),
    user_from   uuid not null references public.users(id) on delete cascade,
    user_to     uuid not null references public.users(id) on delete cascade,
    name        text not null default '',
    family_id   uuid references public.families(id) on delete cascade,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    deleted_at  timestamptz
);

create table if not exists public.messages (
    id               uuid primary key default gen_random_uuid(),
    conversation_id  uuid not null references public.conversations(id) on delete cascade,
    user_from        uuid not null references public.users(id) on delete cascade,
    text             text not null,
    sent_at          timestamptz not null default now(),
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),
    deleted_at       timestamptz
);

-- ────────────────────────────────────────────────────────────
-- Indices (improves query performance for common filters)
-- ────────────────────────────────────────────────────────────
create index if not exists idx_users_family_id           on public.users(family_id);
create index if not exists idx_users_auth_id             on public.users(auth_id);
create index if not exists idx_shopping_lists_owner      on public.shopping_lists(owner_user_id);
create index if not exists idx_shopping_items_list       on public.shopping_items(list_id);
create index if not exists idx_meal_plans_family         on public.meal_plans(family_id);
create index if not exists idx_calendar_events_user      on public.calendar_events(user_id);
create index if not exists idx_birthdays_family          on public.birthdays(family_id);
create index if not exists idx_wishlists_owner           on public.wishlists(owner_user_id);
create index if not exists idx_conversations_users       on public.conversations(user_from, user_to);
create index if not exists idx_messages_conversation     on public.messages(conversation_id);

-- ────────────────────────────────────────────────────────────
-- Row Level Security (RLS)
-- Users can only read/write data belonging to their own family.
-- Enable RLS on every table and add policies as your security model grows.
-- ────────────────────────────────────────────────────────────
alter table public.users            enable row level security;
alter table public.families         enable row level security;
alter table public.shopping_lists   enable row level security;
alter table public.shopping_items   enable row level security;
alter table public.meal_plans       enable row level security;
alter table public.meal_plan_days   enable row level security;
alter table public.calendar_events  enable row level security;
alter table public.birthdays        enable row level security;
alter table public.wishlists        enable row level security;
alter table public.wishes           enable row level security;
alter table public.conversations    enable row level security;
alter table public.messages         enable row level security;

-- Basic policy: users can only see their own row until family scoping is added
create policy "users_self" on public.users
    for all using (auth_id = auth.uid());

-- Extend with family-scoped policies as you add Realtime sync:
-- create policy "family_members_read" on public.calendar_events
--     for select using (
--         family_id in (select family_id from public.users where auth_id = auth.uid())
--     );
