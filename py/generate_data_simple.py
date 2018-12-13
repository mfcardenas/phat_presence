import time
import random

from socket import *

host = "localhost"
port = 8089
buf = 1024
addr = (host, port)

sock = socket(AF_INET,SOCK_DGRAM)
# def_msg = "===Enter message to send to server==="
# print "\n", def_msg

items = ['S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']

while 1:
    # data = raw_input('>> ')
    time.sleep(0.100)
    data = str(int(time.time()*1000)) + ";" + items[random.randint(0, 3)] + ";" + str(random.randint(1, 5)) + ";" + str(random.randint(6, 9)) + ";" + str(random.randint(0, 1))
    if not data:
        break
    else:
        if sock.sendto(data.encode(), addr):
            print(data)

print("Close Socket")
sock.close()
