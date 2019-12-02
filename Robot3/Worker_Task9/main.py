import os
from at9_MS.AT9Controller import AT9Controller



if __name__ == "__main__":
    # execute only if run as a script
    try:
        print("start program")

        wt9 = AT9Controller()
        wt9.start_working()
    except Exception as e:
        print("Error: unable to start thread")
        print(e)
