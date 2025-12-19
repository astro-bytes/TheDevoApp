# Database Recommendation: Supabase

## Why Supabase is the Best Choice

### 1. Real-time Subscriptions
The app needs to check for quote updates every 1 second. Supabase provides built-in real-time subscriptions that push updates to clients immediately when new quotes are added, eliminating the need for polling.

### 2. Structured Relational Data
The app has clear data relationships:
- Talks → Quotes → Taps
- Users → Attendance → Streaks

Supabase uses PostgreSQL, which handles these relationships and complex queries efficiently.

### 3. Time-series Analysis
PostgreSQL excels at analyzing tap timestamps in buckets (+/- 2s) and finding peaks/modes in distributions, which is required for the app's tap analysis feature.

### 4. Multi-platform Support
Supabase has official client libraries for:
- JavaScript/TypeScript (Web, Desktop)
- Swift (iOS)
- Kotlin (Android)
- REST API (any platform)

This matches the app's requirement for Mobile, Web, Desktop, and REST API clients.

### 5. Integrated Solution
Supabase provides database, authentication, file storage, and real-time capabilities in one platform, reducing complexity and setup time.

### 6. Search Capabilities
PostgreSQL full-text search supports the app's searchable, filterable, and sortable talks list requirement.

## Free Tier
- 500 MB database storage
- 1 GB file storage
- 2 GB bandwidth/month
- Unlimited API requests
- Real-time subscriptions included
