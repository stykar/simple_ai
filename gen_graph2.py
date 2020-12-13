import matplotlib.pyplot as plt
import numpy as np

centroids = []
points = []
lines = open("ask2out.txt").readlines()
flag = False

def drawGraph(c, p):
    c = np.array(c)
    c = c.astype(np.float)
    p = np.array(p)
    p = p.astype(np.float)

    plt.scatter(c[:,0], c[:,1], 30, c='g')
    plt.scatter(p[:,0], p[:,1], 5, c='r')
    plt.show()

for line in lines:
    if("-----" in line):
        flag = True
        continue
    if("/////" in line):
        flag = False
        drawGraph(centroids, points)
        centroids = []
        points = []
        continue
    if(flag):
        points.append(line.rstrip().split(","))
    else:
        print(line.rstrip().split(","))
        centroids.append(line.rstrip().split(","))