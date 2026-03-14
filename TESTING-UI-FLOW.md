# Fewster — UI Testing Flow

Concrete steps to verify every user-facing feature works correctly after deployment.
Run these in order — each section builds on the previous one.

---

## Prerequisites

App is running at `http://localhost:8081`
Demo user exists in the database (`demouser` — inserted by V6 + V7 migrations)

---

## 1. Home page (public)

- [ ] Open `http://localhost:8081`
- [ ] Page loads with dark navy theme, no errors
- [ ] Paste a valid URL (e.g. `https://github.com`) into the demo form and click **Shorten URL**
- [ ] Short link appears in the green result box
- [ ] Click **Copy** — URL is copied to clipboard
- [ ] Click **Open** — browser redirects to `https://github.com`
- [ ] Submit the same URL again — same short link returned (no duplicate created)
- [ ] Submit an invalid URL (e.g. `not-a-url`) — validation error shown on the form

---

## 2. Registration

- [ ] Click **Get started** in the navbar
- [ ] Try submitting an empty form — required field errors shown
- [ ] Try a username shorter than 5 characters — error shown
- [ ] Try a password without an uppercase letter — error shown
- [ ] Fill in a valid username (5–20 chars, letters/digits/underscores) and a strong password
- [ ] Submit — redirected to `/login` with success flash message
- [ ] Try registering the same username again — "username already taken" error shown inline

---

## 3. Login

- [ ] Open `http://localhost:8081/login`
- [ ] Submit with wrong password — "Invalid username or password" error shown
- [ ] Login with correct credentials — redirected to `/dashboard`
- [ ] Navbar shows username and **Dashboard / Settings / Sign out** links
- [ ] Try accessing `http://localhost:8081/dashboard` while logged out — redirected to `/login`

---

## 4. Dashboard

- [ ] Dashboard loads with stats bar (Total links, Total clicks)
- [ ] Empty state message shown if no links yet
- [ ] Paste a valid URL into the **New short link** form and click **Shorten**
- [ ] New row appears in the table with: original URL, short code, click count (0), created date
- [ ] Click the **copy** icon next to the short code — URL copied
- [ ] Open the short code link in a new tab — redirects correctly, click count increments to 1 on refresh
- [ ] Create 2–3 more links and confirm all appear in the table

---

## 5. Inline edit

- [ ] Click **Edit** on any row — edit row expands below it with current URL pre-filled
- [ ] Change the URL to another valid URL and click **Save** — row updates, success flash shown
- [ ] Click **Edit** then **Cancel** — edit row collapses, nothing changes
- [ ] Try saving an invalid URL — validation error shown

---

## 6. Delete

- [ ] Click **Delete** on any row — browser confirm dialog appears
- [ ] Click **Cancel** — nothing deleted
- [ ] Click **Delete** again and confirm — row removed, success flash shown

---

## 7. Settings — change username

- [ ] Open `http://localhost:8081/settings`
- [ ] Both form cards load (Change username, Change password)
- [ ] Try submitting username form empty — required error shown
- [ ] Try a username that already exists — "username already taken" error shown
- [ ] Enter a new valid username and submit — success flash shown, navbar immediately shows new username
- [ ] Verify you are still logged in (no redirect to login)

---

## 8. Settings — change password

- [ ] Enter the wrong current password — "Current password is incorrect" error shown
- [ ] Enter correct current password but mismatched new/confirm — "do not match" error shown
- [ ] Enter current password as new password — "must be different" error shown
- [ ] Enter correct current password + valid new password + matching confirm — success
- [ ] Redirected to `/login` with "Password updated" flash message
- [ ] Login with the **new** password — succeeds
- [ ] Login with the **old** password — fails

---

## 9. Logout

- [ ] Click **Sign out** in the navbar
- [ ] Redirected to `http://localhost:8081/?logout=true`
- [ ] Navbar shows **Sign in / Get started** (anonymous state)
- [ ] Try accessing `http://localhost:8081/dashboard` directly — redirected to `/login`

---

## 10. Redirect (public)

- [ ] While logged out, paste a short URL directly into the browser address bar (e.g. `http://localhost:8081/r/abc123`)
- [ ] Redirects to the correct original URL
- [ ] Use a non-existent short code (e.g. `/r/xxxxxx`) — redirected to `/?error=link-not-found`

---

## 11. Data persistence (Docker only)

- [ ] Create at least one short link while the app is running
- [ ] Run `docker compose down`
- [ ] Run `docker compose up -d`
- [ ] Open `http://localhost:8081` and log in — previously created links are still present
- [ ] Click counts are unchanged

---

## Quick smoke-test checklist (minimal, for after redeployment)

| Check | Expected |
|---|---|
| Home page loads | Dark navy UI, no white screen |
| Demo shortener works | Short link generated |
| Register new user | Redirects to login |
| Login | Redirects to dashboard |
| Create short link | Appears in table |
| Redirect works | Opens original URL |
| Logout | Returns to home page |
