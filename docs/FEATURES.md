# Features
## Client Apps (Mobile, Web, Desktop, REST API)
### Initial App Opened - Things that happen only once after installing the app

### Initial App Load - Things that happen every time the app is opened with a new RAM Allocation
1. Launches a Loading Screen whil refresh and launch of other systems occurs
2. Launches Initial App Open if first time launch
3. Refershes all Repositories
4. Check Auth state
5. Sends User to previous used tab (Maybe)

### On App Background - Things that can happen just before app is backgrounded
1. TBD

### On App Foreground - Things that happen when the app is brought back to the foreground
1. Refreshes Repositories

### Splash Screen
1. Appears at launch
2. Moves into Loading view
3. Loading view kicks off initial load

- [Android documentation](https://developer.android.com/develop/ui/views/launch/splash-screen)

### App Icon
1. Use Figma to design the icon
2. Export the as cvgs or pngs
3. Use Icon Composer to create an App Icon from cvgs or pngs

### Dependency Injection Container
1. Integrate with Astroject (iOS) or Dagger (Android)
2. Singleton instance so that it is accessable everywhere

### Test Settings Page
1. Integrate with AstroFramework
2. Make visible only on Debug builds

### Connect to Supabase
1. Integrate Supabase Swift Package
2. Use the right Anon Key to access
3. Point to production when in release build vs point to local instance when in debug build with the option to point to production if desired

### Authentication
1. Connect to Supabase DB Instance
2. Sign In Anonymously
3. If already signed in do not sign in again anonymously
4. Store User Data via Repository

### Tap View
1. Tap Anywher on the screen
2. Low lighting - dark dark mode
3. Keep Screen on
4. Connect to Supabase DB Local Instance
5. Store Taps Locally
6. Get User Anon ID
7. Offload Taps to Supabase DB after devo is over

### Devotional List
1. List Devotionals past and present
2. Filter By Selection - Possibly the same as search
   1. Topics
   2. Speakers
   3. Institution
3. Sort by
   1. Topics
   2. Speakers
   3. Institution
   4. Date
4. Search Bar
   1. Speaker
   2. Institution
   3. Topic
5. Refreshable List
6. Launch Details of any 1 devotional
7. Batch Loading

### Devotional Details
1. Speaker
   1. Image
   2. Name
   3. Occupation
2. Summary
3. Transcript
4. Video
5. Topic(s)
6. Date & Time
7. Quotes List

### Devotional Quotes List w/ Most Recent at top of the list
1. Quote Transcript
2. Speaker
3. Date
4. Link to devotional Details
5. Likes
6. Count of likes in live - The number of taps received live durint the original devotional
7. Refreshable - Active pulling or listening?
8. Batch Loading

### Theme
1. Design Application Theme colors 
2. Design Button styles

### STRETCH
#### Transcript of the Talk with User Notes (Stretch)
#### Make Service Requests & Cache Data (Stretch)
 - Personalized analytics on YTD Devotionals
#### Interact with Quotes (Stretch)
#### Note taking (Stretch)
#### Multi-Lingual Translations (Stretch)
#### Attendence Streak (Stretch)
 - You were there in person
 - Geo Location
#### Participation Streak (Stretch)
 - You Tapped or at least had the app open during devo
 - Applicaiton State

## Web Service
### Analyize Talk
1. Convert Talk to Usable Data
 - Find the Audio/Video with Audio of the talk
 - Convert Audio to Transcript
 - Tie a timestamp to the words in the Transcript
2. Analize Taps
 - "Graph" the taps in buckets of +/- 2s (subject to change)
 - Find the peaks in a distributions or the multiple modes of timestamps 
3. Draw Connections between User Taps and Talk
 - Get the transcript +/- 5s (subject to change)
 - Pass transcript into LLM and let it determine if there is a reasonable Quote to be made out of the text
   - If none then widen the transcript time until there is
4. Inserting Quotes into the DB
5. Sending Notifications to Client Applications
### Perform Analytics function at Scheduled Times
 - This should be based on Service hosting
### Notifications to Client Apps
 - Apple notifications
 - Android/Google notifications
### Connect to DB(s)
### Personalized User Specific Quotes/Talks/Attendence (Stretch)

## Infrastructure
### LLMs
 - Gemini
 - Llama
 - Chat GTP
 - HuggingFace
### Audio Transcript Converters
 - ???
### Database (SQL or JSON) - Nathan: 
 - Supabase
 - Neon
 - CloudFlare
 - GoogleCloud/Firebase
 - Schemas
### Web Service (Prolong TS)
 - AWS?
 - Firebase?
 - Supabase?
 - Self Hosted and Open to the Internet (Josh?)?
### Telemetry (Stretch)
 - Open Telemetry?
### User Analytics (Stretch)
 - Adobe Analytics?
### Logging (Stretch)
 - Elastic
 - Firebase
 - Database Schema
### Client Configuration
 - Database Schema

## Marketing
### How to get the word out?