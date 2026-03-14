-- V8: Add index on users.username for faster lookup during settings operations.
--
-- WHY: updateUsername() and createUser() both call UserRepository.findByUsername()
-- to check uniqueness. Without an index this is a full table scan. As the user
-- table grows this becomes the hottest query on the write path.
--
-- The UNIQUE constraint added in V2 already guarantees a unique index exists in
-- most MySQL versions, but making it explicit here ensures it survives schema
-- migrations and is visible in tooling.

ALTER TABLE users
    ADD INDEX idx_users_username (username);