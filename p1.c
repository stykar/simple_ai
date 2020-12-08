#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define d 1
#define K 4
#define H1 10
#define H2 6
#define H2_in "logistic"

//σ(u)=1/(1+exp(-u))).
char train_set[3000][3];

void main(){
    FILE *fp1;
    char *line = NULL;
    char *token;
    size_t len = 100;
    size_t read;
    fp1 = fopen("s1_train.txt", "r");    
    int i = 0;
    int j = 0;
    char * checkString;
    float temp;
    int ch = 'C';
   
    while ((read = getline(&line, &len, fp1)) != -1) {

        token = strtok(line," ");
        while(token != NULL){
            checkString = strchr(token, ch);
            if(checkString == NULL){
                //TO-DO string se float
                sprintf(&train_set[i][j], "", strtof(token));
            }
            else{
                strcpy(&train_set[i][j], token);
            }
            j++;
            printf("%s ", &train_set[i][j]);
            //printf(" %s ",token);
            token = strtok(NULL, " ");
        }
        i++;
        j = 0;
        printf("\n");
    }
    fclose(fp1);


}

