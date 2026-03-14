-- V7: Replace plain-text demo user password with a BCrypt-encoded hash.
--
-- WHY: V6 inserted 'demopassword' as plain text. Spring Security uses BCryptPasswordEncoder,
-- which means the login attempt will always fail because BCrypt.matches("demopassword", "demopassword")
-- returns false — it expects the stored value to be a BCrypt hash, not raw text.
--
-- The hash below encodes the password: demopassword
-- Generated with BCryptPasswordEncoder.encode("demopassword"), strength 10.
-- Replace with your own hash if you want a different demo password.

UPDATE users
SET password = '$2a$10$Ow4uPfGaFNDHbGXZHFX.8.aZl7Uz4I1gxkl6CgB2TfRxfGoflwXmW'
WHERE username = 'demouser';
