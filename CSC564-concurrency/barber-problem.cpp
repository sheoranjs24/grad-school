/* 
Barber Problem

Solution:
barber function :
sleeps. i.e. wait on sb semaphore.
when some1 awakes it, it does following until customer queue is not
empty (mcq) :
    pop the first customer from queue,
    notify the customer for haircut;  sc[cutomer] semaphore
    do haircut
    notifty customer of haircut being finished.  scust sempahore

customer function:
acquire mq lock
check waiting room, if full (i.e. mcq is full), then go and come back
later at some time.
if waiting room queue has space or barber chair is free, then push to queue
notify the barber
wait for turn to haircut
wait until haircut is finished
go back and come back later time for haircut. (i.e. loop back, instead
of creating new threads)

*/

#include<cstdio>
#include<iostream>
#include<thread>
#include<chrono>
#include<cstdlib>
#include<queue>

#define CUST_COUNT 10
#define CHAIR_COUNT 4

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
thread barber, customer[CUST_COUNT];
semaphore sb, sc[CUST_COUNT], scust;
mutex mc, mq;
int coc = -1;
queue<int> cwq;  // protected by sq;
bool bs = true;

void barber_fn() {
	while(1) {
		printf("Barber sleeping...\n");
		sb.wait();
		
		while (!cwq.empty()) { 	// remove 1st customer from queue
				mq.lock(); 
				coc = cwq.front();
				cwq.pop();
				mq.unlock();
				sc[coc].notify();
				printf("Haircut of customer %d has started\n",coc);
				this_thread::sleep_for(chrono::milliseconds(rand() % 120));
				//coc = -1;
				scust.notify();
				// Haircut done	
		}
	}
}

void customer_fn (int i){
	while(1){
		this_thread::sleep_for(chrono::milliseconds(rand() % 120));
		mq.lock();
		printf("Customer %d arrived; queue:%lu\n", i, cwq.size());
		if ( cwq.size() < CHAIR_COUNT ) {
				cwq.push(i);
				printf("Customer %d waiting\n",i);
				mq.unlock();
		} else {
				mq.unlock();
				std::this_thread::sleep_for(chrono::milliseconds(rand() % 2));
				break;
		}

		sb.notify();
		sc[i].wait();
		scust.wait();

		// either die or wait for some random time and come back for haircut
		this_thread::sleep_for(chrono::seconds(rand() % 2));
	}
}

int main(int argc, char **argv){
	//Declare variables
  int i;

	// Intialize them
	srand(time(0));

	printf("Program starting..\n");
	// start threads
	barber = thread(barber_fn);
	for(i = 0; i < CUST_COUNT; i++) {
		customer[i] = thread(customer_fn, i);
	}

	// sleeps 
	std::this_thread::sleep_for(chrono::seconds(10));
	for(i=0; i< CUST_COUNT; i++) {
		customer[i].join();
	}
	barber.join();
	
	printf("Program completed.\n");

}