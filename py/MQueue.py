import numpy as np


class MQueue:

    def __init__(self):
        self.items = []

    def is_empty(self):
        return self.items == []

    def enqueue(self, item):
        self.items.insert(0, item)

    def dequeue(self):
        return self.items.pop()

    def size(self):
        return len(self.items)

    def show_items(self):
        print(self.items)

    def mean(self):
        return np.mean(self.items)
