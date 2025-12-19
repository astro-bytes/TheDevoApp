# Features
## Client Apps (Mobile, Web, Desktop, REST API)
### Tap
 - Cache on device and pushed to DB after Devo
 - Should be dark themed
 - Take up the whole screen
 - Keep the phone on
### Talk Details (Stretch)
#### Summary of Talk
 - LLM Summary of talk from DB
#### Transcript of the Talk with User Notes (Stretch)
#### List Quotes from Talk
 - Active Pulling (check every 1 for updates from the DB on Quotes)
### List Previous Talks
 - Searchable
 - Filterable
 - Sortable
### Connect to DB
 - Read
 - Write
 - Update?
 - Delete?
### Make Service Requests & Cache Data (Stretch)
 - Personalized analytics on YTD Devotionals
### Interact with Quotes (Stretch)
### Note taking (Stretch)
### Multi-Lingual Translations (Stretch)
### Attendence Streak (Stretch)
 - You were there in person
 - Geo Location
### Participation Streak (Stretch)
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
 - Use ffmpeg to normalize audio to wav mono 16 kHz
 - Use Whisper via faster-whisper to generate a timestamped transcript
 - Store transcript in DB as:
   - full transcript text for reading and search
   - timestamped segments for tap to text lookup
 - Support transcript slice queries:
   - given timestamp t, fetch transcript segments within +/- window seconds
### Database (SQL or JSON) - Nathan: 
 - Supabase
   - Why: PostgreSQL with real-time subscriptions, multi-platform SDKs, and integrated auth/storage. Best fit for relational data structure and tap analysis needs.
 - Schemas (to be designed)
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

# Current Work
|Teammate|Item|Deadline|
|-|-|-|
|Porter|Database Schemas|EOD - 12/18/25|
|Nathan|Research which providers give us the largest free data storage possible|EOD - 12/18/25|
|Josh|Start with iOS App|EOD - 12/27/25|