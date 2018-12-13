from socket import *

class ConnectionWS:

    def __init__(self):
        self.numSocket = 1
        self.ports = [60001]
        self.host = 'localhost'
        self.addrs = []
        self.socks = []
        self.buff = 512

    def set_connection(self):
        for i in self.ports:
            addr = (self.host, self.ports[i])
            sock = socket(AF_INET, SOCK_DGRAM)
            sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
            sock.bind(addr)
            self.socks.append(sock)


    def get_data(self):
        datas = []
        for i in self.socks:
            data = i.recv(self.buff).decode()
            datas.append(data)

        return datas

