#!/bin/bash
set -e

# Configuration for local Docker Desktop Kubernetes
IMAGE_NAME="modulith"
TAG=$(git rev-parse --short HEAD 2>/dev/null || echo "latest")
LOCAL_IMAGE="${IMAGE_NAME}:${TAG}"

echo "=== Deploying to local Docker Desktop Kubernetes ==="

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t ${LOCAL_IMAGE} .
docker tag ${LOCAL_IMAGE} ${IMAGE_NAME}:latest

# Ensure kubectl is pointing to Docker Desktop
echo "Verifying Kubernetes context..."
CONTEXT=$(kubectl config current-context)
if [[ "${CONTEXT}" != "docker-desktop" ]]; then
  echo "Switching to docker-desktop context..."
  kubectl config use-context docker-desktop || {
    echo "Error: docker-desktop context not found. Make sure Docker Desktop Kubernetes is enabled."
    exit 1
  }
fi

# Create secrets from template if needed
if [ ! -f "k8s/secrets.yaml" ]; then
  echo "Creating secrets from template..."
  cp k8s/secrets.yaml.template k8s/secrets.yaml
  echo "Please edit k8s/secrets.yaml with appropriate values before continuing."
  exit 0
fi

# Update deployment.yaml to use local image
echo "Updating Kubernetes manifests..."
sed -i.bak "s|\${DOCKER_REGISTRY}/|localhost/|g" k8s/deployment.yaml

# Apply infrastructure resources first
echo "Deploying PostgreSQL..."
kubectl apply -f k8s/postgres.yaml
echo "Deploying RabbitMQ..."
kubectl apply -f k8s/rabbitmq.yaml

# Wait for infrastructure to be ready
echo "Waiting for deployments to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/postgres deployment/rabbitmq

# Ensure PostgreSQL is fully initialized
echo "Running database initialization check..."
kubectl apply -f k8s/postgres-init.yaml
echo "Waiting for PostgreSQL to be ready for connections..."
kubectl wait --for=condition=complete --timeout=60s job/postgres-init

# Apply application resources
echo "Applying application manifests..."
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/deployment.yaml

echo "Deployment completed successfully!"
echo "Monitor deployment with: kubectl get pods"
echo "View logs with: kubectl logs -l app=modulith"