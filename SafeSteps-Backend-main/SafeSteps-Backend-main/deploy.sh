#!/bin/bash

echo "🚀 Starting SafeSteps Deployment..."

# Check if docker is installed
if ! command -v docker &> /dev/null
then
    echo "Docker is not installed. Please install Docker and Docker Compose first."
    exit 1
fi

# Ensure .env exists
if [ ! -f .env ]; then
    echo "⚠️ .env file not found! Copying .env.example..."
    cp .env.example .env
    echo "🚨 Please edit the .env file with your API keys before running this script again."
    exit 1
fi

echo "Stopping any existing containers..."
docker compose down

echo "Building and starting containers in detached mode..."
docker compose up --build -d

echo "✅ Deployment successful! SafeSteps Backend is running on port 8000."
echo "Use 'docker compose logs -f backend' to view live logs."
