/*
H2O Problem

Solution:
2 semaphore seh , seo for Hydrodgen and Oxygen elements.
2 mutex mh, mo to access hydrogen related data (hcount) and oxygen
related data (ocount)
Global hydrogen count : hcount variable

Hydrogen thread function:
if 1st then wait on seh semaphore.
if 2nd H element, then notify O element and wait on seh semaphore.

Oxygen thread funciton :
acquire mo mutex (i.e only one O element can run through code.)
wait for signal from Hydrogen element. when received, it means 2 H are present,
notify 2 H elements and create the bond.

*/

#include<cstdio>
#include<iostream>
#include<thread>
#include<chrono>
#include<cstdlib>
#include<queue>

#define THREAD_COUNT 10

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
thread eh[THREAD_COUNT], eo[THREAD_COUNT];
semaphore seh, seo;
mutex mh, mo;
int hcount=0, total=0, ocount=0;
int qh[300]={0}, qo[300]={0}, ih=0, io=0; 
 

void hydrogen_fn(int i) {
	while(1) {
		printf("    H Arrived\n");
		mh.lock();
		//qh[ih++] = i;		
		++hcount;
		if (hcount % 2 == 1) {
			mh.unlock();
			seh.wait();
			printf("H");
		} else {
			seo.notify();
			seh.wait();
			printf("H");
			mh.unlock();
		}
		this_thread::sleep_for(chrono::milliseconds(rand() % 120));	
	}
}

void oxygen_fn (int i){
	while(1){
		printf("    O Arrived\n");
		mo.lock();
		++ocount;
		seo.wait();
		
		//qo[io++] = i;
		++total;
		printf("H2O formed, total:%d, hcount=%d, ocount=%d\n",total, hcount, ocount);
		printf("O");

		seh.notify();
		seh.notify();
		mo.unlock();

		this_thread::sleep_for(chrono::milliseconds(rand() % 120));
	}
}

int main(int argc, char **argv){
	//Declare variables
  int i;

	// Intialize them
	srand(time(0));

	printf("Program starting..\n");
	// start threads
	for(i = 0; i < THREAD_COUNT; i++) {
		eh[i] = thread(hydrogen_fn, i);
		eo[i] = thread(oxygen_fn, i);
	}

	// sleeps 
	std::this_thread::sleep_for(chrono::seconds(10));
	for(i=0; i< THREAD_COUNT; i++) {
		eh[i].join();
		eo[i].join();
	}
	
	printf("Program completed.\n");

}