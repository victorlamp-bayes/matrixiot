docker compose -f matrixiot-base/matrixiot-mysql/mysql.yaml -p matrixiot down
docker compose -f matrixiot-base/matrixiot-mongo/mongo.yaml -p matrixiot down
docker compose -f matrixiot-base/matrixiot-rocketmq/rocketmq.yaml -p matrixiot down
docker compose -f matrixiot-base/matrixiot-redis/redis.yaml -p matrixiot down
docker compose -f matrixiot-base/matrixiot-nacos/nacos-standalone-mysql.yaml -p matrixiot down

#docker compose -f matrixiot-base/matrixiot-prometheus/prometheus-standalone.yaml -p matrixiot down
#docker compose -f matrixiot-base/matrixiot-grafana/grafana.yaml -p matrixiot down

