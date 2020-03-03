import docker
from docker.types import Resources
from unix_server import UnixServer
import atexit

def removeservice(docker_client):
    workertask_service = docker_client.services.list(filters={"name": "robot3_workertask9"})
    if len(workertask_service) != 0:
        workertask_service[0].remove()
        print("Workertask 9 removed")


address = "/tmp/deployer.sock"
server = UnixServer(address)
client = docker.from_env()
atexit.register(removeservice,docker_client=client)
while(True):
    try:
        message_received = server.read_data()
        image_name = "127.0.0.1:5000/r3_assembly_coordinator:{}".format(message_received)
        workertask_name = "127.0.0.1:5000/workertask9"
        if message_received == "no_cushion":
            workertask_service = client.services.list(filters={"name": "robot3_workertask9"})
            if len(workertask_service) != 0:
                workertask_service[0].remove()
        else:
            workertask_service = client.services.list(filters={"name": "robot3_workertask9"})
            if len(workertask_service) == 0:
                client.services.create(image=workertask_name, name="robot3_workertask9",
                                       constraints=["node.hostname==GOCASr3"],
                                       resources=Resources(mem_limit=52428800), mounts=["/tmp:/tmp:rw"],
                                       env=["PYTHONUNBUFFERED=1"])
        print(image_name)
        service = client.services.list(filters={"name": "robot3_r3_assembly_coordinator"})
        service[0].update(image=image_name)
    except ConnectionResetError as e:
        print(e)

