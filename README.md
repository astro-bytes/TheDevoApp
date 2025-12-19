# Getting Started
## MacOS
You are expected to already have installed the following:
  - [homebrew](https://brew.sh)

### Supabase Setup
Supabase is our applications backend. It will host our Backend API functions and Database.

1. Open a shell in the supabase directory
2. Install the Supabase CLI
   ```bash
   brew install supabase
   ```
3. Login to supabase in a [browser](supabase.com)
4. Login to supabase cli
   ```bash
   supabase login
   ```
5. Link the remote instance to the local project instance. The reference can be found in the project [settings](https://supabase.com/dashboard/project/fpxxsiuhbscytuazcubm/settings/general) page.
   ```bash
   supabase link --project-ref <reference>
   ```
6. Start [docker](#docker) daemon
7. Pull the Supabase DB. This will pull the remote schema migrations to your local instance and overwrite any that have not been committed. (use this carefully after the first time setting up supabase locally)
   ```bash
   supabase db pull
   ```
8. Start supabase db local instance
   ```bash
   supabase db start
   ```
> **WARNING: Shut down supabase and docker before putting your computer to sleep**  
> Sometimes if left open the application might not restart correctly because a port that shoudl be used to execute the container is being used and prevents the container from working correctly.
    ```bash
    supabse db stop
    ```
    Close docker app

### Docker Desktop
Docker is used to run local instances of supabase instead of pointing our production Backend.

1. Install Docker Desktop
   ```bash
   brew install -c docker-desktop
   ```
2. Start Docker Daemon. This is as easy as opening the Docker Desktop app from your applications folder.

## Linux
TODO

## Windows
TODO

---
---

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

# Current Work
|Teammate|Item|Deadline|
|-|-|-|
|Porter|Database Schemas|EOD - 12/18/25|
|Nathan|Research which providers give us the largest free data storage possible|EOD - 12/18/25|
|Josh|Start with iOS App|EOD - 12/27/25|