/*
Santa's Problem

Solution:
3 semaphores : ss - santa, sr - reindeer, se - elves.
3 mutex - me - elves, mt, met.  :
global ce , cr to count reindeer and elves.
mt is global mutex to access ce and cr counters.
me mutex keeps other elves waiting, when there are 3 elves getting help.

Santa function :
santa sleeps until some1 awakes him. (waits on ss semaphore)
Santa acquire mt mutex, and do the work.
if 9 reindeers are present, he helps them & notify them
, otherwise he helps evles (if they are 3) and notify them.

Reindeer function:
when 9th reindeer arrives, it notifies santa.

Elevs function :
Acquire me lock. : meaning no other elve_group is getting help from santa.
acquire mt lock : meaning reindeer are not getting help
wait until elve count become 3. last elve (3rd one) notifies santa for help.
when last elve gets notified from santa (any of the 3 elves), it
unlocks me lock for other elves.
*/

#include<cstdio>
#include<iostream>
#include<thread>
#include<chrono>
#include<cstdlib>
#include<queue>

#define ELVES_COUNT 10

using namespace std;


// semaphore defined
class semaphore{
private:
    mutex mtx;
    condition_variable cv;
    unsigned int count, total_waits;

public:
    semaphore(unsigned int count_ = 0):count(count_)
		{ total_waits = 0; }
		
		void notify(unsigned int numRes=1)
		{
				unique_lock<mutex> lck(mtx);
				if (total_waits >= numRes) {
					count += numRes;
					total_waits -= numRes;
			  	for(; numRes > 0; numRes--) {
						cv.notify_one();
					}
				} else if (total_waits > 0) {
					printf("total_waits < %u\n",numRes);
					count += total_waits;
					for(; total_waits >0; total_waits--) {
						cv.notify_one();
					}
				} else {
					printf("total_waits is zero.\n");
				}
		}
    void wait(unsigned int numRes=1)  
		// can pass arg (thread_id) for queue notify order
    {
        unique_lock<mutex> lck(mtx);
				total_waits += numRes;
        while(count < numRes){  //==0
            cv.wait(lck);
        }
        count -= numRes;
    }
		void broadcast()
		{
				unique_lock<mutex> lck(mtx);
				count -= total_waits;
				total_waits = 0;
				cv.notify_all();
		}
};

// global variables
thread ts, tr[9], te[ELVES_COUNT];
semaphore ss, sr, se;  //se to prevent other elves when 3 are waiting
mutex mt, me, met;
int cet=ELVES_COUNT, ce, cr;

 
//funcitons
void santa_fn();
void reindeer_fn(int i);
void elves_fn(int i);

void santa_fn() {
	while (1) {
		printf("Santa sleeping\n");
		ss.wait();
		mt.lock();
			if (cr == 9) {
				//Prepare Sleh
				cr = 0;
				printf("Santa Preparing Sleigh\n");
				sr.notify(9);
			} else if ( ce == 3) {
				// Help elves
				printf("Santa helping Elves\n");
				se.notify(3);
			}
		mt.unlock();
	}
}

void reindeer_fn(int i) {
	while(1) {
		printf("Reindeer : %d\n",i);
		mt.lock();
			++cr;
			if (cr == 9) {
				ss.notify();
			}
		mt.unlock();
		
		sr.wait();
		// get hitched.
		//printf("Reindeer hitched\n");
		this_thread::sleep_for(chrono::milliseconds(rand() % 180));
	}
}

void elves_fn(int i) {
	while(1) {
		printf("Elve : %d\n",i);
		me.lock();
		mt.lock();
			++ce;
			if (ce == 3) {
				ss.notify();
			} else {
				me.unlock();
			}
		printf("Elve getting help: %d\n",i);
		mt.unlock();

		// get Help
		se.wait();
	
		mt.lock();
			--ce;
			if (ce == 0) {
				printf("last elve : %d\n",i);
				me.unlock();
			}
		mt.unlock();
	
		// add new no to same thread
		met.lock();
			i = ++cet;
		met.unlock();
		this_thread::sleep_for(chrono::milliseconds(rand() % 180));	
	}
}

int main(int argc, char **argv){
	//Declare variables
  int i;

	// Intialize them
	srand(time(0));

	printf("Program starting..\n");
	// start threads
	ts = thread(santa_fn);
	for(i=0; i< 9; i++) {
		tr[i] = thread(reindeer_fn, i);
	}
	for(i = 0; i < ELVES_COUNT; i++) {
		te[i] = thread(elves_fn, i);
	}

	// sleeps 
	std::this_thread::sleep_for(chrono::seconds(100));
	for(i=0; i<9; i++) {
		tr[i].join();
	}
	for(i=0; i< ELVES_COUNT; i++) {
		te[i].join();
	} 
	ts.join();
	
	printf("Program completed.\n");

}