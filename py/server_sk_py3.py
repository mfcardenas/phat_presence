from socket import *
from MQueue import MQueue
import pandas as pd

host = "localhost"
port = 8089
buff = 1024
addr = (host, port)

sock = socket(AF_INET, SOCK_DGRAM)
sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
sock.bind(addr)

# procedure HANDLESENSORINFORMATION(si, Ati, Ats,i)
#   queue.add (Ati)
#   sum = sum + Ati
#   if queue.length > np,1 then
#       oldT = queue.begin
#       queue.removeBegin()
#       sum = sum - oldT
#   if sum > tp,1 then
#       notifyMemoryPattern (p1)

sensors = []
queue = MQueue()
sum = 0
NP1 = 5
TP1 = 50

rows_list = []
init_data = {'TimeSimulation': 0, 'S1-LivingRoom': 0, 'S2-BedRoom': 0, 'S3-Kitchen': 0, 'S4-BathRoom': 0}

sensorQueue = {'S1-LivingRoom': MQueue(), 'S2-BedRoom': MQueue(),
               'S3-Kitchen': MQueue(), 'S4-BathRoom': MQueue()}


_columns=['TimeSimulation', 'S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']
df = pd.DataFrame([init_data], columns=_columns)


def sensor_information_one(ts, si, ati, ats, status):
    __queue = sensorQueue[si]
    __queue.enqueue(ati)
    global sum
    sum = sum + ati
    ret = False

    if __queue.size() > NP1:
        old_t = __queue.dequeue()
        sum = sum + old_t
        print(si, __queue.show_items())

    if sum > TP1:
        data_temp = dict(df.iloc[-1])
        data_temp['TimeSimulation'] = ts
        data_temp[si] = status
        rows_list.append(data_temp)
        ret = True

    return ret


def sensor_information_two(ts, si, ati, ats, status):
    __queue = sensorQueue[si]
    __queue.enqueue(ats)
    ret = False

    if (__queue.size() > NP1) & (__queue.mean() < TP1):
        __queue.dequeue()
        data_temp = dict(df.iloc[-1])
        data_temp['TimeSimulation'] = ts
        data_temp[si] = status
        rows_list.append(data_temp)
        print(si, __queue.show_items())
        ret = True

    return ret



print("WebSocket Activo")
print("URL:\t http://" + host + ":" + str(port))

while 1:
    data = sock.recv(512).decode()
    if not data:
        print("Cliente Cerrado")
        break
    else:
        sample = data.split(";")
        if len(sample) == 5:
            if sensor_information_two(int(sample[0]), str(sample[1]), float(sample[2]), float(sample[3]), int(sample[4])):
                df = pd.DataFrame(rows_list, columns=_columns)
                print(df.tail(10))

# Close socket
print("Close Socket")
sock.close()
