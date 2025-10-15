#!/bin/bash

echo "=== Cleaning up Modulith Kubernetes resources ==="

echo "Deleting application resources..."
kubectl delete deployment modulith
kubectl delete service modulith
kubectl delete secret modulith-secrets

read -p "Do you want to delete PostgreSQL and RabbitMQ as well? (y/n) " -n 1 -r
echo 
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "Deleting PostgreSQL..."
    kubectl delete -f k8s/postgres.yaml
    
    echo "Deleting RabbitMQ..."
    kubectl delete -f k8s/rabbitmq.yaml
    
    echo "All resources deleted."
else
    echo "Kept PostgreSQL and RabbitMQ running."
fi

echo "Cleanup completed!"