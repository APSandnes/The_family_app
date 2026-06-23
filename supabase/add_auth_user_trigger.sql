-- Auto-create public.users profile when a new auth user is created.
-- This avoids the FK constraint issue caused by PKCE email-confirmation flow,
-- where there is no active session to authenticate a manual INSERT after signUpWith.
--
-- Name/mobile/birthday/avatar_color are passed via raw_user_meta_data in signUpWith.

CREATE OR REPLACE FUNCTION public.handle_new_auth_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO public.users (auth_id, name, email, mobile, birthday, avatar_color)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'full_name', split_part(NEW.email, '@', 1)),
    NEW.email,
    COALESCE(NEW.raw_user_meta_data->>'phone', ''),
    COALESCE(NEW.raw_user_meta_data->>'birthday', ''),
    COALESCE((NEW.raw_user_meta_data->>'avatar_color')::int, 0)
  )
  ON CONFLICT (auth_id) DO NOTHING;
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_auth_user();
