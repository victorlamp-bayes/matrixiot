./shutdown.sh

docker network create --driver bridge --subnet 172.20.0.0/16 --gateway 172.20.0.1 matrixiot_net

docker compose -f matrixiot-base/matrixiot-mysql/mysql.yaml -p matrixiot up -d
docker compose -f matrixiot-base/matrixiot-mongo/mongo.yaml -p matrixiot up -d
docker compose -f matrixiot-base/matrixiot-rocketmq/rocketmq.yaml -p matrixiot up -d
docker compose -f matrixiot-base/matrixiot-redis/redis.yaml -p matrixiot up -d
docker compose -f matrixiot-base/matrixiot-nacos/nacos-standalone-mysql.yaml -p matrixiot up -d
#docker compose -f matrixiot-base/matrixiot-prometheus/prometheus-standalone.yaml -p matrixiot up -d
#docker compose -f matrixiot-base/matrixiot-grafana/grafana.yaml -p matrixiot up -d

