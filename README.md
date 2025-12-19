# Getting Started
## MacOS
You are expected to already have installed the following:
  - [homebrew](https://brew.sh)

### Supabase Setup
Supabase is our applications backend. It will host our Backend API functions and Database.

1. Open a shell in the root directory
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
8. Start supabase local instance
   ```bash
   supabase start
   ```
   Running `supabase start` will also start supabase and the db at the same time.  
   Use `supabase db reset` after making changes to `seed.sql` or adding a migration.  
> **WARNING: Shut down supabase and docker before putting your computer to sleep**  
> Sometimes if left open the application might not restart correctly because a port that should be used to execute the container is being used and prevents the container from working correctly.
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

### Supabase Setup

### Docker (CLI)? (Desktop App)? Setup