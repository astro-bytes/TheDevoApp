# Android Client
Native mobile application for the Devo App. This application focuses on learning and developing natively with Kotlin for Android applications.

## Getting Started

### Install Android Studio
Validate the right version of Android Studio is installed. List of current versions found [here](../../docs/VERSIONS.md)
#### MacOS
1. Run in the terminal
   ```bash
   brew install android-studio --cask
   ```

#### Linux
TODO

#### Windows
TODO

### Run the App
1. Open Android Studio and go through the setup up process
2. Open TheDevoApp in Android Studio
3. Copy the contents of the `example.properties` into a new file called `debug.properties` at `/clients/android/`
4. If not started Start Supabase Local Instance. [See](../../README.md#supabase-setup) for how to setup Supabase.
5. In your `debug.properties` file 
   1. set `SUPABASE_URL` = `Project URL` API 
   2. set `SUPABASE_KEY` = `Publishable` Authentication Key
   > **NOTE:** You can find these values in the terminal after running `supabase start` or using `supabase status` when already running.
6. Sync the gradle
7. Run a clean build of the project
8. Select your destination (e.i simulator you have installed or a physical device)
9. Press the play button & watch the magic work

## Tech Stack
|Library|Version|Notes|
|-|-|-|
|[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)|2.57.2|Dependency Injection Framework|
|[Supabase](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://supabase.com/docs/guides/getting-started/quickstarts/kotlin&ved=2ahUKEwiW9rGwgtKRAxXVFTQIHfJ6NdcQmuEJegQIGBAB&usg=AOvVaw1ds-esArYbXK1u8T9D65XJ)||Database & Service|
|[Kotlin](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://kotlinlang.org/docs/home.html&ved=2ahUKEwjNhuupgtKRAxUHDjQIHVwvCj4QFnoECBAQAQ&usg=AOvVaw0CN1eu52oohX1ehvFclHLc)|2.2.0|Language|
|[Serialization](https://kotlinlang.org/docs/serialization.html)||Encoding & Decoding of Objects|