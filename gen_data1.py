import random

s1_train = open("s1_train.txt", "w")
s1_test = open("s1_test.txt", "w")

for i in range(0,3000):
    x1 = random.uniform(-1,1)
    x2 = random.uniform(-1,1)
    temp = random.randint(1,10)
    if(temp == 1):
        s1_train.write(str(x1)+" " +str(x2)+ " C4\n")
    elif((x1**2 + x2**2) < 0.25):
        s1_train.write(str(x1)+" " +str(x2)+ " C1\n")
    elif((-1<x1<-0.4 and -1<x2<-0.4) or (0.4<x1<1 and 0.4<x2<1)):
        s1_train.write(str(x1)+" " +str(x2)+ " C2\n")
    elif((-1<x1<-0.4 and 0.4<x2<1) or (0.4<x1<1 and -1<x2<-0.4)):
        s1_train.write(str(x1)+" " +str(x2)+ " C3\n")
    else:
        s1_train.write(str(x1)+" " +str(x2)+ " C4\n")

for i in range(0,3000):
    x1 = random.uniform(-1,1)
    x2 = random.uniform(-1,1)
    if((x1**2 + x2**2) < 0.25):
        s1_test.write(str(x1)+" " +str(x2)+ " C1\n")
    elif((-1<x1<-0.4 and -1<x2<-0.4) or (0.4<x1<1 and 0.4<x2<1)):
        s1_test.write(str(x1)+" " +str(x2)+ " C2\n")
    elif((-1<x1<-0.4 and 0.4<x2<1) or (0.4<x1<1 and -1<x2<-0.4)):
        s1_test.write(str(x1)+" " +str(x2)+ " C3\n")
    else:
        s1_test.write(str(x1)+" " +str(x2)+ " C4\n")