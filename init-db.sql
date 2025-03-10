DO $$ BEGIN
IF NOT EXISTS (
    SELECT FROM pg_catalog.pg_database
    WHERE datname = 'tasks') THEN
    CREATE DATABASE tasks;
END IF;
END $$;