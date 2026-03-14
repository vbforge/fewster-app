-- Add user_id column to url table
ALTER TABLE url ADD COLUMN user_id BIGINT;

-- Add foreign key constraint
ALTER TABLE url ADD CONSTRAINT fk_url_user_id
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create index for better performance on user_id queries
CREATE INDEX idx_url_user_id ON url(user_id);

-- Create composite index for original_url and user_id (for duplicate checking per user)
CREATE INDEX idx_url_original_user ON url(original_url(255), user_id);