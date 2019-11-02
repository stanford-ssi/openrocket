import json
import numpy as np
from os import listdir
from os.path import isfile, join
import matplotlib.pyplot as plt

onlyfiles = [f for f in listdir('DataOut') if f.endswith('.txt')]
speeds = []
mses = []

for f in onlyfiles:
    file = open("DataOut/"+f)
    js = json.load(file)
    orientations = js['values'][1]
    degree_orientations = np.degrees(np.array(list(filter(None,orientations))))
    error = degree_orientations - 87.0
    meansqerr = np.mean(np.power(error,2))
    speed = float(f.replace('.txt','')[4:])
    speeds.append(speed)
    mses.append(meansqerr)

plt.scatter(speeds,mses)
plt.xlabel("Spin Speed (RPM)")
plt.ylabel("Mean Squared Deviation")
plt.title("Error vs. Spin Speed")
plt.show()
