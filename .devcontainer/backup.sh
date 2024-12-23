#!/bin/sh

# Define the backup file name and path
BACKUP_FILE="../src/main/resources/data/full_backup.sql"

# Check if docker-compose.yml exists in the current directory
if [ ! -f docker-compose.yml ]; then
  echo "docker-compose.yml not found in the current directory."
  exit 1
fi

# Run the pg_dump command inside the db container with --inserts option
docker-compose exec db pg_dump --username=spacecodee --dbname=springboot_security_open_template --inserts > $BACKUP_FILE

echo "Backup completed: $BACKUP_FILE"