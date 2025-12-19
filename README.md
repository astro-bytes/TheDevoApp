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
 - Primary: Use deterministic heuristics to score and select best quote candidate
   - Split transcript into sentence candidates
   - Score each sentence (length, boundaries, filler words, meaningful content)
   - Select highest scoring valid sentence as quote
 - Fallback: If no sentence meets quality threshold, use LLM to evaluate transcript
   - LLM determines if quotable statement exists
   - If none, widen transcript time window and retry
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
### LLMs and Quote Extraction
**Purpose:**
- Generate summaries of devotional talks
- Identify meaningful quotes from transcript text near user tap timestamps
- Optionally translate extracted content (stretch)

**Primary Approach: Deterministic Quote Extraction**
- The primary method for identifying quotes does not rely on an LLM
- Transcript text is queried within a time window around a tap peak
- The text is split into sentence candidates
- Each sentence is scored using deterministic heuristics
- The highest scoring valid sentence is selected as the quote
- If no sentence meets the quality threshold, no quote is created

**Why This Approach:**
- Fully deterministic and predictable
- Always free with no usage limits
- Prevents hallucination or rewriting of source text
- Fast and easy to debug
- Well suited for short transcript windows

**Example Heuristics:**
- Sentence length within a target range
- Clean sentence boundaries
- Low filler word density
- Presence of meaningful verbs and nouns
- Not a partial or trailing fragment

**Secondary Approach: LLM Fallback**
- An LLM is used only when deterministic extraction fails
- When used:
  - No sentence in the transcript window passes quality checks
  - The transcript contains only partial or low quality text
  - A wider context window is required
- Role of the LLM:
  - Evaluate short transcript context
  - Identify whether a quotable statement exists
  - Optionally rewrite or lightly clean extracted text
  - Return structured output indicating success or failure
- Constraints:
  - Input text is limited to a small window around the tap timestamp
  - Output must preserve original meaning and wording
  - LLM usage is minimized to control cost and variability

**Summary Generation:**
- For full talk summaries:
  - The entire transcript is passed to an LLM
  - A concise summary is generated once per talk
  - The summary is stored and reused by clients

**Design Principles:**
- Deterministic systems first
- LLMs used only where they add clear value
- Outputs are cached and reused
- Model choice is abstracted and replaceable

**Options, really any work:**
- Gemini
- Llama
- ChatGPT
- HuggingFace
### Audio Transcript Converters
 - Use ffmpeg to normalize audio to wav mono 16 kHz
 - Use Whisper via faster-whisper to generate a timestamped transcript
 - Store transcript in DB as:
   - full transcript text for reading and search
   - timestamped segments for tap to text lookup
 - Support transcript slice queries:
   - given timestamp t, fetch transcript segments within +/- window seconds
### Database
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