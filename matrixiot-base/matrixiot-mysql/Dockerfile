FROM mysql:8.0.31
ADD conf/nacos-mysql-schema.sql /docker-entrypoint-initdb.d/nacos-mysql-schema.sql
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/nacos-mysql-schema.sql
EXPOSE 3306
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]