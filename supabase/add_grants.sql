-- Grant table-level permissions to the authenticated role.
-- RLS policies alone are not enough — without explicit GRANTs the
-- authenticated role gets "permission denied" even when a policy passes.
-- Run this after schema.sql and fix_users_rls_recursion.sql.

GRANT SELECT, INSERT, UPDATE, DELETE ON public.families          TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.shopping_lists    TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.shopping_items    TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.meal_plans        TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.meal_plan_days    TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.calendar_events   TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.birthdays         TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.wishlists         TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.wishes            TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.conversations     TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.messages          TO authenticated;

-- Fix families SELECT policy: the original policy only allows reading a
-- family if the user is already a member or the admin. This blocks joinFamily,
-- which must look up a family by name+code before the user has joined it.
-- Allow any authenticated user to read families (the join code is the gate).
DROP POLICY IF EXISTS "families_select" ON public.families;
CREATE POLICY "families_select" ON public.families
  FOR SELECT USING (auth.uid() IS NOT NULL);
