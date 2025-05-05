#!/bin/bash
set -x

source pre-start-monitoring.sh

docker compose --profile monitoring --profile mongo --profile booking-service up -d 
