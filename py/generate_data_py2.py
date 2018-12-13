import time
import random
from constants import Const

from socket import *
cons = Const()
addr = (cons.host, cons.port)

sock = socket(AF_INET,SOCK_DGRAM)
def_msg = "=== Sending to send to Server ===";
print("\n", def_msg)

items = cons.sensors

while (1):
    #data = raw_input('>> ')
    time.sleep(1)
    data = str(int(time.time()*1000)) + ";" + items[random.randint(0,3)] + ";" + str(random.randint(1,5)) + ";" + str(random.randint(6,9)) + ";" + str(random.randint(0,1))
    if not data:
        break
    else:
        if(sock.sendto(data,addr)):
            print data

print("Close Socket")
sock.close()