
class Const:

    def __init__(self):
        self.sensors = ['S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']
        self.host = "localhost"
        self.port = 8089
        self.buff = 512
        self.init_data = {'TimeSimulation': 0, 'S1-LivingRoom': 0, 'S2-BedRoom': 0, 'S3-Kitchen': 0, 'S4-BathRoom': 0}
        self.NP1 = 5
        self.TP1 = 50
        self.columns = ['TimeSimulation', 'S1-LivingRoom', 'S2-BedRoom', 'S3-Kitchen', 'S4-BathRoom']

    def new_data(self):
        return {'TimeSimulation': 0, 'S1-LivingRoom': 0, 'S2-BedRoom': 0, 'S3-Kitchen': 0, 'S4-BathRoom': 0}

    def get_data_phat(self, num = 1):
        return 1
