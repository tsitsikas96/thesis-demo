import os
from Controller.W2Controller import W2Controller
import sys

if __name__ == "__main__":
    # execute only if run as a script
    try:
        print("start program")
        device = "{}".format(sys.argv[1])
        print("Device: " + device)
        w1 = W2Controller()
        w1.start_controller(device)
    except Exception as e:
        print("Error: unable to start thread")
        print(e)
