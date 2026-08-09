# Habit Maker — Build Your APK (Step by Step)

This is a real Android app (Kotlin + Jetpack Compose). It runs natively — not a
website — and schedules real Android alarms so reminders fire even if the app
is closed or the phone is idle.

## What it does
- Add habits, each with its own color and **multiple reminder times per day**
- Notifications fire at each scheduled time, daily, automatically rescheduling
- Tap a time chip on the home screen to mark that reminder done for today
- Simple streak counter (🔥) for habits completed fully every day in a row
- Minimal, clean UI (soft off-white background, one accent color per habit)
- All data stored locally on your phone (Room database) — nothing leaves the device

## Step 1 — Install Android Studio (one-time, ~15 min)
1. Go to https://developer.android.com/studio and download Android Studio for your OS.
2. Install it with the default options. When it first opens, let it download the
   "Android SDK" components it prompts for — this needs internet access.

## Step 2 — Open the project
1. Unzip `HabitMaker.zip` anywhere on your computer.
2. Open Android Studio → **Open** → select the unzipped `HabitMaker` folder.
3. Android Studio will "Sync" the project automatically — this downloads the
   libraries the app uses (Compose, Room, etc.). It can take a few minutes the
   first time. If it asks to upgrade the Android Gradle Plugin, click **Accept/OK**.

## Step 3 — Run it (to test on your phone)
1. On your Android phone: Settings → About phone → tap "Build number" 7 times
   to enable Developer Options. Then Settings → Developer Options → enable
   "USB debugging".
2. Plug your phone into the computer with a USB cable. Allow the debugging
   prompt that appears on the phone.
3. In Android Studio, your phone's name should appear in the toolbar device
   dropdown at the top. Select it, then click the green ▶ **Run** button.
4. The app installs and opens on your phone directly.

## Step 4 — Build the installable APK file
1. In Android Studio's menu: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
2. When it finishes, click the "locate" link in the notification that appears
   (bottom right), or find the file manually at:
   `app/build/outputs/apk/debug/app-debug.apk`
3. Copy that `app-debug.apk` file to your phone (via USB, email to yourself,
   Google Drive, etc.), open it on the phone, and tap **Install**.
   - Your phone may warn about "installing from unknown sources" — this is
     normal for any APK not from the Play Store. Tap **Settings** in that
     prompt and allow installs from that source (e.g. your Files app), then
     go back and install.

That's it — the app is now on your phone permanently, with no dependency on
Android Studio or your computer afterward.

## Notes
- The app will ask for two permissions on first launch: notifications, and
  "alarms & reminders" (needed on Android 12+ for exact-time alerts). Please
  allow both, or reminders won't fire on time.
- If you ever see reminders arrive a few minutes late, that's your phone's
  battery optimization throttling the app — you can exempt it in
  Settings → Apps → Habit Maker → Battery → "Unrestricted".
- Everything is stored only on your device. Uninstalling the app deletes the data.

## If you get stuck
Paste the exact error text Android Studio shows (in the "Build" panel at the
bottom) back to Claude, and it can tell you exactly what to fix.
