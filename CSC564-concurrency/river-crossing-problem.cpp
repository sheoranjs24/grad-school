/*
River Crossing Problem

Solution:
3 semaphores are used for Go_thread queue, Pthread queue and boarding.
1 global mutex mt.
Two variables cGo, cPt to count no of threads waiting for boarding.
One local isCaptian.

Go Thread function :
When go thread arrives, it checks if there are enough threads present
to board the boat, if yes then it declares itself as captain, wakes up
exactly 3 threads and then waits on those 3 threads to board the boat,
then the thread will call row_boat.

PThread function: works the same way.

Mutex mt is used to access shared info - cGo and cPt. Threads must
release mt before going on wait/sleep. Only captain thread keeps the
mutex mt untill it calls row_boat.
*/

#include<cstdio>
#include<iostream>
#include<thread>
#include<chrono>
#include<cstdlib>
#include<queue>
#include<string>
#include"river-crossing-problem.hpp"

using namespace std;
using namespace std::chrono;


// semaphore defined
class semaphore{
private:
    mutex mtx;
    condition_variable cv;
    unsigned int count;

public:
    semaphore(unsigned int count_ = 0):count(count_){;}
		
		void notify(unsigned numRes=1)
		{
				unique_lock<mutex> lck(mtx);
				count += numRes;
				for (; numRes>0; --numRes) {
					cv.notify_one();
				}
		}
    void wait(unsigned int numRes = 1)  
		// can pass arg (thread_id) for queue notify order
    {
        unique_lock<mutex> lck(mtx);
        while(count < numRes){  //==0
            cv.wait(lck);
        }
        count -= numRes;
    }
};

// global variables
collector mdata;
thread tgo[THREAD_COUNT];
semaphore sGo, sPt, sboard, smain;
mutex mt, mlk;
int cGo=0, cPt=0,j,k;
string board;

atomic_int boat_count, passenger_count;
atomic_bool first_boat;
chrono::high_resolution_clock::time_point start_time, stop_time;

 
//funcitons
void go_fn(int i);
void pthread_fn(int i);
void generate_threads();

void generate_threads() 
{
		boat_count=0; first_boat=true;
		while (1){
	
			if ((rand()%2) == 0) {
				go_fn(++passenger_count);
				
			} else {	
				pthread_fn(++passenger_count);
			}
			//std::this_thread::sleep_for(chrono::milliseconds(5));
		}
}	

void go_fn(int i) {
		bool isCaptain;
		isCaptain = false;
		//printf("Arrived : %u from GO\n",i);
		
		mt.lock();
		cGo++; 
		
		if (cGo >= 4 ) {
			sGo.notify(3);
			cGo -= 4;
			isCaptain = true;
		} else if ( (cGo >= 2) && (cPt >= 2))  {
			sGo.notify(1);
			sPt.notify(2);
			cPt -= 2;
			cGo -= 2;
			isCaptain = true;
		} else {
			mt.unlock();
			sGo.wait();
			//printf("G");
			sboard.notify();
		}

		//printf("Boarded :%d of GO\n",i);
		if (isCaptain) {
			// wait for others to board
			sboard.wait(3);
			if (first_boat) {
				start_time  = chrono::high_resolution_clock::now();
				first_boat = false;
			}
			//printf("Rowing Boat : %u of GO\n",i);

			if (++boat_count >= TOTAL_BOAT_COUNT) {
					stop_time  = chrono::high_resolution_clock::now();
					smain.notify();
					return;
			}
			mt.unlock();		 
		}
}

void pthread_fn (int i){
		bool isCaptain;
		isCaptain = false;
		//printf("Arrived : %u from PT\n",i);

		mt.lock();
		cPt++; 

		if (cPt >= 4 ) {
			sPt.notify(3);
			cPt -= 4;
			isCaptain = true;
		} else if ( (cPt >= 2) && (cGo >= 2))  {
			sPt.notify(1);
			sGo.notify(2);
			cGo -= 2;
			cPt -= 2;
			isCaptain = true;
		} else {
			mt.unlock();
			sPt.wait();
			sboard.notify();
		}
		
		//printf("Boarded :%d of PT\n",i);
		if (isCaptain) {			  
				//printf("Rowing Boat : %u of PT\n",i);
				sboard.wait(3);
				//printf("Rowing Boat : %u of P\n",i);
				
				if (++boat_count >= TOTAL_BOAT_COUNT) {
						stop_time  = chrono::high_resolution_clock::now();
						smain.notify();
						return;
				}				
				mt.unlock();				
		} 
		//generate_threads();		
}

int main(int argc, char **argv){
	//Declare variables
  int i;
	high_resolution_clock::duration avg_time(0);
	size_t boarded_count=0;

	// Intialize them
	srand(time(0));
	passenger_count =0;

	printf("Program starting..\n");
	// start threads : random order
	
	for(i = 0; i < THREAD_COUNT; i++) {
		tgo[i] = thread(&generate_threads);
	}
	
	smain.wait();
	printf("Program exiting.\n");
	
	auto time = stop_time - start_time;
	cout<<"total time for 1000 boats :"<<duration<double, micro>(time).count()<< "micro seconds"<<endl;
	
	exit(0);
	/* sleeps 
	/std::this_thread::sleep_for(chrono::seconds(10));
	for(i=0; i< THREAD_COUNT; i++) {
		tgo[i].join();
		tpt[i].join();
	} */
	
	printf("Program completed.\n");

}