#! /bin/sh

docker-machine create --driver=virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 1024 Robot1
docker-machine create --driver=virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 1024 Robot2
docker-machine create --driver=virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 1024 Robot3
docker-machine create --driver=virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 1024 Workbench1
docker-machine create --driver=virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 1024 Workbench2