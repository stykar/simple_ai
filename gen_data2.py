import random

s2_train = open("s2_train.txt", "w")

for i in range(0,150):
    x1 = random.uniform(0.75,1.25)
    x2 = random.uniform(0.75,1.25)
    s2_train.write(str(x1)+" " +str(x2)+"\n")

for i in range(0,150):
    x1 = random.uniform(0,0.5)
    x2 = random.uniform(0,0.5)
    s2_train.write(str(x1)+" " +str(x2)+"\n")

for i in range(0,150):
    x1 = random.uniform(0,0.5)
    x2 = random.uniform(1.5,2)
    s2_train.write(str(x1)+" " +str(x2)+"\n")

for i in range(0,150):
    x1 = random.uniform(1.5,2)
    x2 = random.uniform(0,0.5)
    s2_train.write(str(x1)+" " +str(x2)+"\n")

for i in range(0,150):
    x1 = random.uniform(1.5,2)
    x2 = random.uniform(1.5,2)
    s2_train.write(str(x1)+" " +str(x2)+"\n")

for i in range(0,150):
    x1 = random.uniform(0,2)
    x2 = random.uniform(0,2)
    s2_train.write(str(x1)+" " +str(x2)+"\n")
