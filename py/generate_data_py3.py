import time
import random
from constants import Const
from socket import *

cons = Const()
addr = (cons.host, cons.port)

sock = socket(AF_INET,SOCK_DGRAM)
items = cons.sensors
data = None

while 1:
    # data = raw_input('>> ')
    time.sleep(0.500)
    activation = random.randint(0, 1)
    data = str(int(time.time()*1000)) + ";" + items[random.randint(0, 3)] + ";" + str(activation)
        # data = str(int(time.time()*1000)) + ";" + items[random.randint(0, 3)] + ";" + str(random.randint(1, 5)) + ";" + str(random.randint(6, 9)) + ";" + str(activation)
    if not data:
        break
    else:
        if activation:
            if sock.sendto(data.encode(), addr):
                print(data)

print("Close Socket")
sock.close()
