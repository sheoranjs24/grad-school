/* 
Smoker Problem

Solution:
define an array of binary semaphores A, one for each smoker;
and a binary semaphore for the table, T.

The agent's code is
while true:
    time.sleep(T)
    # choose smokers i and j nondeterministically,
    # making the third smoker k  [ k=rand(1,3); ]
    signal(A[k])
    // unlock table

and the code for smoker i is
while true:
    time.sleep(A[i])
    # make a cigarette
    signal(T)
    # smoke the cigarette
*/


#include<cstdio>
#include<iostream>
#include<thread>
#include<chrono>
#include<cstdlib>
using namespace std;


// semaphore defined
class semaphore{
private:
    mutex mtx;
    condition_variable cv;
    int count;

public:
    semaphore(int count_ = 0):count(count_){;}
    void notify()
    {
        unique_lock<mutex> lck(mtx);
        ++count;
        cv.notify_one();
    }
    void wait()
    {
        unique_lock<mutex> lck(mtx);

        while(count == 0){
            cv.wait(lck);
        }
        count--;
    }
};

// global variables
thread smokers[3];
thread agent;
semaphore sm[3], am;

void agent_fn() {
	int selection;
	while (1) {
		//std::this_thread::sleep_for(std::chrono::seconds(1));
		selection = rand() % 3;
		printf("selected smoker %d\n",selection);
		sm[selection].notify();
		am.wait();
		printf("Agent selecting..\n");
	}
}

void smoker_fn (int i){
	while(1){
		sm[i].wait();
		printf("Smoker %d smoking.\n",i);
		//std::this_thread::sleep_for(std::chrono::seconds(std::rand() % 2));
		am.notify();
	}
}

int main(int argc, char **argv){
	//Declare variables
  int i;

	// Intialize them
	srand(time(0));

	printf("Program starting..\n");
	// start threads
	agent = thread(agent_fn);
	for(i=0; i<3; i++) {
		smokers[i] = thread(smoker_fn, i);
	}

	// sleeps 
	std::this_thread::sleep_for(chrono::seconds(10));
	
	printf("Program completed.\n");

}