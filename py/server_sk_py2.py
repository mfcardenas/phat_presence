from socket import *
from mqueue import MQueue
import pandas as pd

host = "localhost"
port = 8089
buf = 1024
addr = (host,port)

sock = socket(AF_INET,SOCK_DGRAM)
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
NP1 = 4 # umbran numero de activaciones
TP1 = 20

rows_list = []
init_data = {'TimeSimulation': 0, 'S1-LivingRoom': 0, 'S2-BedRoom': 0, 'S3-Kitchen': 0, 'S4-BathRoom': 0}

sensorQueue = {'TimeSimulation': 0, 'S1-LivingRoom': MQueue(), 'S2-BedRoom': MQueue(), 'S3-Kitchen': MQueue(), 'S4-BathRoom': MQueue()}


_columns=['TimeSimulation', 'S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']
df = pd.DataFrame([init_data], columns=_columns)

def sensorInformationGeneral( TS, Si, ATi, ATs, Status):
    queue = sensorQueue[Si]
    queue.enqueue(ATi)
    global sum
    sum = sum +  ATi
    ret = False

    if queue.size() > NP1:
        oldT = queue.dequeue()
        sum = sum + oldT
        print Si,queue.showItems()

    if sum > TP1:
        data_temp = dict(df.iloc[-1])
        data_temp['TimeSimulation'] = TS
        data_temp[Si] = Status
        rows_list.append(data_temp)
        ret = True;
    return ret


def sensorInformationOptimo( TS, Si, ATi, ATs, Status):
    queue = sensorQueue[Si]
    queue.enqueue(ATs)
    ret = False

    if ((queue.size > NP1) & (queue.mean() < TP1)):
        data_temp = dict(df.iloc[-1])
        data_temp['TimeSimulation'] = TS
        data_temp[Si] = Status
        rows_list.append(data_temp)
        print Si,queue.showItems()
        ret = True
    return ret


print "Websocket Activo"
print "address:\t" + host + ":" + str(port)

while 1:
    data,addr = sock.recvfrom(buf)
    if not data:
        print "Client has exited!"
        break
    else:
        sample = data.split(";")
        if len(sample) == 5:
            if (sensorInformationOptimo(int(sample[0]), str(sample[1]), float(sample[2]), float(sample[3]), int(sample[4]))):
                df = pd.DataFrame(rows_list, columns=_columns)
                print df.tail(5)

# Close socket
print("Close Socket")
sock.close()