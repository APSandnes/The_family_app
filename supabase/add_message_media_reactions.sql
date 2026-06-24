-- Extend messages with media support
ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS message_type TEXT NOT NULL DEFAULT 'text',
    ADD COLUMN IF NOT EXISTS media_url TEXT;

-- Emoji reactions table
CREATE TABLE IF NOT EXISTS message_reactions (
    id          UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    message_id  UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    emoji       TEXT NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (message_id, user_id)
);

ALTER TABLE message_reactions ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "view_reactions" ON message_reactions;
CREATE POLICY "view_reactions" ON message_reactions FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM conversation_participants cp
            WHERE cp.conversation_id = message_reactions.conversation_id
              AND cp.user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid() LIMIT 1)
        )
    );

DROP POLICY IF EXISTS "manage_own_reactions" ON message_reactions;
CREATE POLICY "manage_own_reactions" ON message_reactions FOR ALL
    USING  (user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid() LIMIT 1))
    WITH CHECK (user_id = (SELECT id FROM public.users WHERE auth_id = auth.uid() LIMIT 1));
