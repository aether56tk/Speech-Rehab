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


-- RLS hardening helpers. Keep this schema out of the exposed Data API schemas.
create schema if not exists private;
revoke all on schema private from public, anon;
grant usage on schema private to authenticated;

create or replace function private.is_clinic_member(p_clinic_id uuid)
returns boolean language sql stable security definer set search_path=''
as $$ select exists(select 1 from public.clinic_memberships m where m.clinic_id=p_clinic_id and m.user_id=(select auth.uid()) and m.status='active') $$;
create or replace function private.is_clinic_owner(p_clinic_id uuid)
returns boolean language sql stable security definer set search_path=''
as $$ select exists(select 1 from public.clinics c where c.id=p_clinic_id and c.created_by=(select auth.uid())) $$;
create or replace function private.has_client_access(p_client_id uuid)
returns boolean language sql stable security definer set search_path=''
as $$ select exists(select 1 from public.client_access a where a.client_id=p_client_id and a.user_id=(select auth.uid())) $$;
create or replace function private.can_manage_client_access(p_client_id uuid)
returns boolean language sql stable security definer set search_path=''
as $$ select exists(select 1 from public.clinic_clients c join public.clinic_memberships m on m.clinic_id=c.clinic_id where c.id=p_client_id and m.user_id=(select auth.uid()) and m.status='active' and m.role='therapist') $$;
revoke all on function private.is_clinic_member(uuid), private.is_clinic_owner(uuid), private.has_client_access(uuid), private.can_manage_client_access(uuid) from public,anon;
grant execute on function private.is_clinic_member(uuid), private.is_clinic_owner(uuid), private.has_client_access(uuid), private.can_manage_client_access(uuid) to authenticated;

drop policy if exists clinics_select_member on public.clinics;
create policy clinics_select_member on public.clinics for select to authenticated using (created_by=(select auth.uid()) or private.is_clinic_member(id));
drop policy if exists memberships_select_peers on public.clinic_memberships;
create policy memberships_select_peers on public.clinic_memberships for select to authenticated using (user_id=(select auth.uid()) or private.is_clinic_owner(clinic_id));
drop policy if exists memberships_owner_manage on public.clinic_memberships;
create policy memberships_owner_manage on public.clinic_memberships for all to authenticated using (private.is_clinic_owner(clinic_id)) with check (private.is_clinic_owner(clinic_id));
drop policy if exists invitations_owner_select on public.clinic_invitations;
create policy invitations_owner_select on public.clinic_invitations for select to authenticated using (invited_by=(select auth.uid()) or private.is_clinic_owner(clinic_id));
drop policy if exists invitations_owner_insert on public.clinic_invitations;
create policy invitations_owner_insert on public.clinic_invitations for insert to authenticated with check (invited_by=(select auth.uid()) and private.is_clinic_owner(clinic_id));

drop policy if exists clients_clinician_select on public.clinic_clients;
create policy clients_clinician_select on public.clinic_clients for select to authenticated using (
 private.has_client_access(id) or exists(select 1 from public.clinic_memberships m where m.clinic_id=clinic_clients.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clients_clinician_insert on public.clinic_clients;
create policy clients_clinician_insert on public.clinic_clients for insert to authenticated with check (
 created_by=(select auth.uid()) and exists(select 1 from public.clinic_memberships m where m.clinic_id=clinic_clients.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clients_clinician_update on public.clinic_clients;
create policy clients_clinician_update on public.clinic_clients for update to authenticated using (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinic_clients.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
) with check (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinic_clients.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clients_clinician_delete on public.clinic_clients;
create policy clients_clinician_delete on public.clinic_clients for delete to authenticated using (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinic_clients.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role='therapist')
);
drop policy if exists client_access_clinician_manage on public.client_access;
drop policy if exists client_access_select_scoped on public.client_access;
create policy client_access_select_scoped on public.client_access for select to authenticated using (user_id=(select auth.uid()) or private.can_manage_client_access(client_id));
create policy client_access_clinician_manage on public.client_access for all to authenticated using (private.can_manage_client_access(client_id)) with check (private.can_manage_client_access(client_id));

drop policy if exists clinical_records_select_scoped on public.clinical_records;
create policy clinical_records_select_scoped on public.clinical_records for select to authenticated using (
 private.has_client_access(client_id) or exists(select 1 from public.clinic_memberships m where m.clinic_id=clinical_records.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clinical_records_insert_clinician on public.clinical_records;
create policy clinical_records_insert_clinician on public.clinical_records for insert to authenticated with check (
 created_by=(select auth.uid()) and exists(select 1 from public.clinic_memberships m where m.clinic_id=clinical_records.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clinical_records_update_clinician on public.clinical_records;
create policy clinical_records_update_clinician on public.clinical_records for update to authenticated using (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinical_records.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
) with check (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinical_records.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role in ('therapist','student_clinician'))
);
drop policy if exists clinical_records_delete_clinician on public.clinical_records;
create policy clinical_records_delete_clinician on public.clinical_records for delete to authenticated using (
 exists(select 1 from public.clinic_memberships m where m.clinic_id=clinical_records.clinic_id and m.user_id=(select auth.uid()) and m.status='active' and m.role='therapist')
);
