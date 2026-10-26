-- Speech Rehab: Supabase Auth + private per-user cloud workspace
-- Apply in Supabase SQL Editor if the migration has not already been applied.
-- Safe to re-run: tables and policies are created/replaced idempotently.

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default '',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
alter table public.profiles enable row level security;
drop policy if exists "profiles_select_own" on public.profiles;
create policy "profiles_select_own" on public.profiles for select to authenticated using ((select auth.uid()) = id);
drop policy if exists "profiles_insert_own" on public.profiles;
create policy "profiles_insert_own" on public.profiles for insert to authenticated with check ((select auth.uid()) = id);
drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own" on public.profiles for update to authenticated using ((select auth.uid()) = id) with check ((select auth.uid()) = id);

create table if not exists public.user_workspaces (
  user_id uuid primary key references auth.users(id) on delete cascade,
  data jsonb not null default '{"clients":[],"tasks":[],"responses":[],"diary":[]}'::jsonb,
  updated_at timestamptz not null default now()
);
alter table public.user_workspaces enable row level security;
drop policy if exists "workspace_select_own" on public.user_workspaces;
create policy "workspace_select_own" on public.user_workspaces for select to authenticated using ((select auth.uid()) = user_id);
drop policy if exists "workspace_insert_own" on public.user_workspaces;
create policy "workspace_insert_own" on public.user_workspaces for insert to authenticated with check ((select auth.uid()) = user_id);
drop policy if exists "workspace_update_own" on public.user_workspaces;
create policy "workspace_update_own" on public.user_workspaces for update to authenticated using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
grant select, insert, update on public.profiles to authenticated;
grant select, insert, update on public.user_workspaces to authenticated;

create or replace function public.handle_speech_rehab_new_user()
returns trigger language plpgsql security definer set search_path = ''
as $$
begin
  insert into public.profiles (id, display_name)
  values (new.id, coalesce(new.raw_user_meta_data ->> 'display_name', ''))
  on conflict (id) do nothing;
  insert into public.user_workspaces (user_id, data)
  values (new.id, '{"clients":[],"tasks":[],"responses":[],"diary":[]}'::jsonb)
  on conflict (user_id) do nothing;
  return new;
end;
$$;
revoke all on function public.handle_speech_rehab_new_user() from public, anon, authenticated;
drop trigger if exists on_speech_rehab_user_created on auth.users;
create trigger on_speech_rehab_user_created after insert on auth.users
for each row execute procedure public.handle_speech_rehab_new_user();
