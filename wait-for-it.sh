#!/usr/bin/env bash

shift
cmd="$@"

until mysql -h mysql --user=user --password=password -e "status" &> /dev/null; do
  >&2 echo "MySQL is unavailable - sleeping"
  sleep 1
done

>&2 echo "MySQL is up - executing command"
exec $cmd
