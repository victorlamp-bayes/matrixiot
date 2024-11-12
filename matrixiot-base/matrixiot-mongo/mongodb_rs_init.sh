#!/bin/bash

m1=mongo1:27017
m2=mongo2:27018
m3=mongo3:27019

echo "###### Waiting for ${m1} instance startup.."
until mongosh 'mongodb://mongo1:27017/admin' --eval 'quit(db.runCommand({ ping: 1 }).ok ? 0 : 2)' &>/dev/null; do
  printf '.'
  sleep 1
done
echo "###### Working ${m1} instance found, initiating user setup & initializing rs setup.."

# setup user + pass and initialize replica sets
mongosh --host ${m1} <<EOF
var rootUser = '$MONGO_INITDB_ROOT_USERNAME';
var rootPassword = '$MONGO_INITDB_ROOT_PASSWORD';
var admin = db.getSiblingDB('admin');
admin.auth(rootUser, rootPassword);
var config = {
    "_id": "rs0",
    "version": 1,
    "members": [
        {
            "_id": 1,
            "host": "${m1}",
            "priority": 2
        },
        {
            "_id": 2,
            "host": "${m2}",
            "priority": 1
        },
        {
            "_id": 3,
            "host": "${m3}",
            "priority": 1,
            "arbiterOnly": true 
        }
    ]
};
rs.initiate(config, { force: true });
rs.status();
EOF