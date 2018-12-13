from socket import *
from mqueue import MQueue
from constants import Const
from connectionws import ConnectionWS

import time
import pandas as pd

cons = Const()
conn = ConnectionWS()
conn.ports[60000, 60001, 60002, 60003, 60004, 60005]

conn.set_connection()

print conn.get_data()


addr = (cons.host, cons.port)

sock = socket(AF_INET, SOCK_DGRAM)
sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
sock.bind(addr)

sensors = []
queue = MQueue()
sum = 0
ts_old = int(time.time()*1000)
NP1 = 6
TP1 = 5000

rows_list = []
# init_data = {'TimeSimulation': 0, 'S1-LivingRoom': 0, 'S2-BedRoom': 0, 'S3-Kitchen': 0, 'S4-BathRoom': 0}

sensorsQueue = {'S1-LivingRoom': MQueue(), 'S2-BedRoom': MQueue(),
               'S3-Kitchen': MQueue(), 'S4-BathRoom': MQueue()}

# columns = ['TimeSimulation', 'S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']
df = pd.DataFrame([cons.new_data()], columns=cons.columns)


def sensor_information_gen(ts, si, status):
    global ts_old, sum
    queue.enqueue(ts - ts_old)
    ts_old = ts
    ret = False

    if queue.size() > NP1:
        ts_ant = queue.dequeue()
        sum = sum + ts_ant
        print si, sum, ts_ant, queue.get_items()

    if sum > TP1:
        data_temp = cons.new_data()
        data_temp['TimeSimulation'] = ts
        data_temp[si] = status
        rows_list.append(data_temp)
        ret = True

    return ret


def sensor_information_two(ts, si, status):
    global ts_old, sum
    _queue = sensorsQueue[si]
    _queue.enqueue(ts - ts_old)
    ts_old = ts
    ret = False

    if (_queue.size() > NP1) & (_queue.mean() < TP1):
        data_temp = cons.new_data()
        data_temp['TimeSimulation'] = ts
        data_temp[si] = status
        rows_list.append(data_temp)
        print(si, _queue.get_items())
        ## Es necesario desencolar?
        _queue.dequeue()
        ret = True

    return ret

print("WebSocket Activo")
print("URL:\t http://" + cons.host + ":" + str(cons.port))

while 1:
    data = sock.recv(cons.buff).decode()
    if not data:
        print("Cliente Cerrado")
        break
    else:
        sample = data.split(";")
        if len(sample) == 3:
            if sensor_information_two(int(sample[0]), str(sample[1]), int(sample[2])):
                df = pd.DataFrame(rows_list, columns=cons.columns)
                print(df.tail(6))


print("Close Socket")
sock.close()
