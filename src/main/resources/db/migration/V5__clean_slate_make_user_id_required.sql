-- Delete all existing URLs (since they don't have user association)
DELETE FROM url WHERE user_id IS NULL;

-- Make user_id NOT NULL
ALTER TABLE url MODIFY COLUMN user_id BIGINT NOT NULL;

-- Add a comment to document this change
ALTER TABLE url COMMENT = 'URL table with required user association - existing URLs were removed in V5 migration';