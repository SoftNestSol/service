#!/bin/bash
set -x

docker compose --profile monitoring --profile mongo --profile booking-service down 
