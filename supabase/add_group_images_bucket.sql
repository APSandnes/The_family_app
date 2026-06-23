-- Create the group-images storage bucket for chat conversation pictures.
-- Safe to run even if the avatars bucket already exists.
-- Run this in the Supabase SQL Editor if chat conversation images are not saving.

INSERT INTO storage.buckets (id, name, public)
VALUES ('group-images', 'group-images', true)
ON CONFLICT (id) DO NOTHING;

-- Policies (drop first so the script is idempotent)
DROP POLICY IF EXISTS "Group images: authenticated read"   ON storage.objects;
DROP POLICY IF EXISTS "Group images: authenticated write"  ON storage.objects;
DROP POLICY IF EXISTS "Group images: authenticated update" ON storage.objects;
DROP POLICY IF EXISTS "Group images: authenticated delete" ON storage.objects;

CREATE POLICY "Group images: authenticated read" ON storage.objects
  FOR SELECT USING (bucket_id = 'group-images' AND auth.uid() IS NOT NULL);

CREATE POLICY "Group images: authenticated write" ON storage.objects
  FOR INSERT WITH CHECK (bucket_id = 'group-images' AND auth.uid() IS NOT NULL);

CREATE POLICY "Group images: authenticated update" ON storage.objects
  FOR UPDATE USING (bucket_id = 'group-images' AND auth.uid() IS NOT NULL);

CREATE POLICY "Group images: authenticated delete" ON storage.objects
  FOR DELETE USING (bucket_id = 'group-images' AND auth.uid() IS NOT NULL);
