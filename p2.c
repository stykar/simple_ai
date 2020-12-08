#include <stdio.h>
#include <string.h>

#define d 1
#define K 4
#define H1 10
#define H2 6
#define H2_in "logistic"

//σ(u)=1/(1+exp(-u))).
char array[3000];

int main() {
    FILE *fp1;
    
    const char token;
    size_t len = 100;
    size_t read;
    fp1 = fopen("s1_train.txt", "r");    
    int i = 0;
    int j = 0;
    int c=0;
    char *line[256];
   
    while (fgets(line, sizeof(line), fp1) != NULL) {
        while(i<3000){
            
            fputs(line,array[i]);
            //fputs(line, &array[i]);
            i++;
        }
        
         
    }

    printf("%c",array[1999]);
    fclose(fp1);
}