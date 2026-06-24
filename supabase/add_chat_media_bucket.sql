INSERT INTO storage.buckets (id, name, public)
VALUES ('chat-media', 'chat-media', true)
ON CONFLICT (id) DO NOTHING;

DROP POLICY IF EXISTS "chat_media_read"   ON storage.objects;
DROP POLICY IF EXISTS "chat_media_insert" ON storage.objects;
DROP POLICY IF EXISTS "chat_media_delete" ON storage.objects;

CREATE POLICY "chat_media_read" ON storage.objects FOR SELECT
    USING (bucket_id = 'chat-media');

CREATE POLICY "chat_media_insert" ON storage.objects FOR INSERT
    WITH CHECK (
        bucket_id = 'chat-media' AND
        auth.uid() IS NOT NULL
    );

CREATE POLICY "chat_media_delete" ON storage.objects FOR DELETE
    USING (
        bucket_id = 'chat-media' AND
        (storage.foldername(name))[2] = auth.uid()::text
    );
