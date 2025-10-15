#!/bin/bash

echo "=== Modulith Kubernetes Status ==="

echo -e "\n📊 ALL DEPLOYMENTS"
kubectl get deployments

echo -e "\n🛢️ ALL PODS"
kubectl get pods

echo -e "\n🔌 ALL SERVICES"
kubectl get services

echo -e "\n🗄️ DATABASE STATUS"
PG_POD=$(kubectl get pods -l app=postgres -o jsonpath="{.items[0].metadata.name}" 2>/dev/null)
if [ -n "$PG_POD" ]; then
  echo "PostgreSQL pod: $PG_POD"
  kubectl exec $PG_POD -- pg_isready -U myuser || echo "PostgreSQL is not ready"
else
  echo "PostgreSQL not found"
fi

echo -e "\n� RABBITMQ STATUS"
RMQ_POD=$(kubectl get pods -l app=rabbitmq -o jsonpath="{.items[0].metadata.name}" 2>/dev/null)
if [ -n "$RMQ_POD" ]; then
  echo "RabbitMQ pod: $RMQ_POD"
  echo "RabbitMQ Management UI: http://localhost:31672"
else
  echo "RabbitMQ not found"
fi

echo -e "\n📋 APPLICATION LOGS"
APP_POD=$(kubectl get pods -l app=modulith -o jsonpath="{.items[0].metadata.name}" 2>/dev/null)
if [ -n "$APP_POD" ]; then
  echo "Application pod: $APP_POD"
  kubectl logs $APP_POD --tail=20
else
  echo "Application pod not found"
fi

echo -e "\n✅ STATUS"
kubectl get pods -l app=modulith -o jsonpath="{.items[*].status.containerStatuses[0].ready}" | grep -q "true" && echo "Application is running" || echo "Application has issues"

echo -e "\n🔗 ACCESS POINTS"
echo "Application: http://localhost:30080/api/products"
echo "RabbitMQ UI: http://localhost:31672 (login with myuser/secret)"