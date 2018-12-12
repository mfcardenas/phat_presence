import socket as sk
import matplotlib.pyplot as plt
from drawnow import *
import sys


def showErrorException(infoError):
    """ Function show Error Exception  """
    print ("---> Error::: ", infoError[0])
    print ("---> Error Type::: ", infoError[1])

x, y, z = [], [], []

# Se lee del Socket los datos del sensor
ADDR = ("localhost", 60001)
sock = sk.socket(sk.AF_INET, sk.SOCK_STREAM)

# OPTIONAL::: se controla la actividad del sensor (connect or unconnet)
sw = True

try:
    sock.connect(ADDR)
except:
    print("---> [initial] No read data from Socket")
    showErrorException(sys.exc_info())
    sw = False

plt.ion()
cnt = 0

def makeFig():
    """ Make Screen  """
    plt.title('PHAT-SIM Accelerometer')
    plt.grid(True)
    plt.ylabel('Axis Acceleration')
    plt.plot(x, 'r-', label='Raw X Acceleration')
    plt.ylim(-10, 10)
    plt.legend(loc='upper left')
    plt.plot(y, 'b-', label='Raw Y Acceleration')
    plt.legend(loc='center right')
    plt.ticklabel_format(useOffset=False)
    plt.plot(z, 'g-', label='Raw Z Acceleration')
    plt.legend(loc='upper right')
    plt.ylim(-10, 10)

while True:
    for data in sock.makefile('r'):
        if len(data.split(";")) == 7:
            datatmp = data.replace("\n", "")
            dataArray = datatmp.split(';')
            xtemp = float(dataArray[4])
            ytemp = float(dataArray[5])
            ztemp = float(dataArray[6])
            x.append(xtemp)
            y.append(ytemp)
            z.append(ztemp)
        if len(x) > 30:
            drawnow(makeFig)
            plt.pause(0.0001)
            cnt = cnt + 1
        if cnt > 5:
            x.pop(0)
            y.pop(0)
            z.pop(0)