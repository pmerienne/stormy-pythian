#!/bin/sh -ex
wget http://download.redis.io/releases/redis-2.6.17.tar.gz
tar xzf redis-2.6.17.tar.gz
cd redis-2.6.17
make
src/redis-server &
