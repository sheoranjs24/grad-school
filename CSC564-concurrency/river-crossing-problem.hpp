#include<chrono>
#include<mutex>
#include<condition_variable>

#define THREAD_COUNT 10
#define TOTAL_BOAT_COUNT 1000
#define TOTAL_PASSENGER_COUNT 10000

using namespace std;

enum passenger_type
{
  go,
  pthread
};

struct passenger_data
{
  chrono::high_resolution_clock::time_point startPoint, stopPoint;
  passenger_type type;
  bool boarded;
};

struct collector 
{
  collector():
    passenger_count(0),
    boat_count(0)
  {}

  unsigned int passenger_count;
  unsigned int boat_count;

  mutex mux;
  condition_variable over;

  void notify_end()
  {
    mux.lock();
    over.notify_all();
    mux.unlock();
  }

  void wait_for_end()
  {
    unique_lock<mutex> lock(mux);
    while(passenger_count < TOTAL_PASSENGER_COUNT)
    over.wait(lock);
  }
};
