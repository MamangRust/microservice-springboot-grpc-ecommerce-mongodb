#!/usr/bin/env bash
#
# restore.sh — MongoDB restore for the local e-commerce stack.
#
# Usage:
#   ./scripts/restore.sh <backup_file>
#
# The backup file may be a raw .archive dump or a .archive.gz dump produced by
# backup.sh. Restoring uses `mongorestore --drop`, which drops each collection
# present in the archive before recreating it — so this is destructive and a
# confirmation prompt is shown unless --yes is passed.
#
#   ./scripts/restore.sh --yes backups/ecommerce_20260101_000000.archive.gz
#
# Procedure:
#   1. Validate the backup file exists.
#   2. Confirm intent (destructive).
#   3. mongorestore --drop from the archive (all per-context DBs).
#   4. Report success/failure.

set -euo pipefail

COMPOSE_FILE="$(cd "$(dirname "$0")/.." && pwd)/docker-compose.yml"

AUTO_YES=0
BACKUP_FILE=""

for arg in "$@"; do
  case "$arg" in
    --yes) AUTO_YES=1 ;;
    *)     BACKUP_FILE="$arg" ;;
  esac
done

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: $0 [--yes] <backup_file>" >&2
  echo "  backup_file: path to a .archive or .archive.gz dump" >&2
  exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
  echo "ERROR: backup file not found: ${BACKUP_FILE}" >&2
  exit 1
fi

# Validate the dump BEFORE the destructive restore: a truncated/empty/corrupt
# file would wipe collections and restore nothing.
if [ ! -s "$BACKUP_FILE" ] || [ "$(stat -c%s "$BACKUP_FILE")" -lt 1024 ]; then
  echo "ERROR: backup file is empty or too small (< 1 KB); refusing to restore." >&2
  exit 1
fi

if [[ "$BACKUP_FILE" == *.gz ]]; then
  if ! gzip -t "$BACKUP_FILE" 2>/dev/null; then
    echo "ERROR: backup file is not a valid gzip archive; refusing to restore." >&2
    exit 1
  fi
fi

MONGO_ID=$(docker compose -f "${COMPOSE_FILE}" ps -q mongo 2>/dev/null || true)
if [ -z "$MONGO_ID" ]; then
  echo "ERROR: mongo container is not running. Start the stack first (just up)." >&2
  exit 1
fi
MONGO_STATE=$(docker inspect -f '{{.State.Running}}' "$MONGO_ID" 2>/dev/null || echo false)
if [ "$MONGO_STATE" != "true" ]; then
  echo "ERROR: mongo container is not running. Start the stack first (just up)." >&2
  exit 1
fi

if [ "$AUTO_YES" -ne 1 ]; then
  read -r -p "Restore will DROP and recreate collections present in the archive. Continue? [y/N] " answer
  case "$answer" in
    y|Y) ;;
    *) echo "Aborted."; exit 1 ;;
  esac
fi

echo "Restoring ${BACKUP_FILE} (all context DBs)"

# mongorestore --drop removes each collection before restoring it, giving a
# clean overlay without touching collections absent from the archive.
case "$BACKUP_FILE" in
  *.gz)
    gunzip -c "$BACKUP_FILE" | docker compose -f "${COMPOSE_FILE}" exec -T mongo \
      mongorestore --archive --gzip --drop
    ;;
  *)
    cat "$BACKUP_FILE" | docker compose -f "${COMPOSE_FILE}" exec -T mongo \
      mongorestore --archive --drop
    ;;
esac

echo "Restore complete: MongoDB is back in service."
