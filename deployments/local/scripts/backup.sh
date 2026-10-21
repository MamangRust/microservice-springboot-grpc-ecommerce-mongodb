#!/usr/bin/env bash
#
# backup.sh — MongoDB backup for the local e-commerce stack.
#
# Usage:
#   ./scripts/backup.sh [retention_days]
#
# Produces: deployments/local/backups/ecommerce_<timestamp>.archive.gz
# Prunes backups older than RETENTION_DAYS (default 7).
#
# Procedure:
#   1. Verify the `mongo` container is healthy.
#   2. mongodump the whole instance (all per-context DBs: ec_identity,
#      ec_merchant, ec_catalog, ec_sales, ec_experience, ec_email) to an
#      archive stream so it is portable to disposable environments.
#   3. Compress with gzip and keep in ./backups/.
#   4. Prune old dumps so retention is bounded.

set -euo pipefail

COMPOSE_FILE="$(cd "$(dirname "$0")/.." && pwd)/docker-compose.yml"
BACKUP_DIR="$(cd "$(dirname "$0")/.." && pwd)/backups"
RETENTION_DAYS="${1:-7}"

mkdir -p "${BACKUP_DIR}"

# `compose ps` exits 0 even when the service is absent/stopped, so check the
# actual container state via inspect instead of relying on its exit code.
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

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
OUT_FILE="${BACKUP_DIR}/ecommerce_${TIMESTAMP}.archive.gz"

echo "Backing up MongoDB (all context DBs) -> ${OUT_FILE}"

docker compose -f "${COMPOSE_FILE}" exec -T mongo \
  mongodump --archive --gzip \
  | gzip -9 > "${OUT_FILE}"

# Exit early if the dump is empty or suspiciously small (< 1 KB).
if [ ! -s "${OUT_FILE}" ] || [ "$(stat -c%s "${OUT_FILE}")" -lt 1024 ]; then
  echo "ERROR: backup file is empty or too small; removing." >&2
  rm -f "${OUT_FILE}"
  exit 1
fi

echo "Backup complete: ${OUT_FILE} ($(du -h "${OUT_FILE}" | cut -f1))"

# Prune old backups beyond the retention window.
PRUNE_TS=$(date -d "-${RETENTION_DAYS} days" +%Y%m%d 2>/dev/null || date -v-${RETENTION_DAYS}d +%Y%m%d)
PRUNED=0
for f in "${BACKUP_DIR}"/ecommerce_*.archive.gz; do
  [ -e "$f" ] || continue
  fdate=$(basename "$f" | sed -E 's/ecommerce_([0-9]{8})_.*/\1/')
  if [ -n "$fdate" ] && [ "$fdate" -lt "$PRUNE_TS" ]; then
    rm -f "$f"
    PRUNED=$((PRUNED + 1))
  fi
done

echo "Pruned ${PRUNED} backup(s) older than ${RETENTION_DAYS} day(s)."
echo "Done."
