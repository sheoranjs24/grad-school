#!/usr/bin/python
#coding='UTF-8'

import oauth2 as oauth
import urllib2 
import json

# See Assignment 1 instructions or README for how to get these credentials
access_token_key = "95222288-FiBCb0sHtKt3CzkeP9xSpdU9rDHdWvYsWaLO1XsGa"
access_token_secret = "hC1TyhAOVFZhasHgErlZTOb4Z1XqdN6HaHvnKRmmU"

consumer_key = "thulHcStgzQ1TdnmczkE1Q"
consumer_secret = "mjl6guLYQ96F7kgHRUoJzUtOUZ0NE3bq1ZGYsFal9Y"

_debug = 0

oauth_token    = oauth.Token(key=access_token_key, secret=access_token_secret)
oauth_consumer = oauth.Consumer(key=consumer_key, secret=consumer_secret)

signature_method_hmac_sha1 = oauth.SignatureMethod_HMAC_SHA1()

http_method = "GET"

http_handler  = urllib2.HTTPHandler(debuglevel=_debug)
https_handler = urllib2.HTTPSHandler(debuglevel=_debug)

'''
Construct, sign, and open a twitter request
using the hard-coded credentials above.
'''
def twitterreq(url, method, parameters):
  req = oauth.Request.from_consumer_and_token(oauth_consumer,
                                             token=oauth_token,
                                             http_method=http_method,
                                             http_url=url, 
                                             parameters=parameters)

  req.sign_request(signature_method_hmac_sha1, oauth_consumer, oauth_token)

  headers = req.to_header()

  if http_method == "POST":
    encoded_post_data = req.to_postdata()
  else:
    encoded_post_data = None
    url = req.to_url()

  opener = urllib2.OpenerDirector()
  opener.add_handler(http_handler)
  opener.add_handler(https_handler)

  response = opener.open(url, encoded_post_data)

  return response


'''
Fetch samples
'''
def fetchsamples():
# next_maxid, loop_counter
  max_id = 0
  since_id = 0
  lcounter = 1

#loop over for multiple pages
  while lcounter:   # Infinite loop
   if max_id == 0:
     url = "https://api.twitter.com/1.1/search/tweets.json?q=microsoft&lang=en&result_type=recent&count=100"  # Change count=100
   else:
     url = "https://api.twitter.com/1.1/search/tweets.json?q=microsoft&lang=en&result_type=recent&count=100&max_id="+str(max_id)+"&since_id="+str(since_id) 

   parameters = []
   search_results = twitterreq(url, "GET", parameters)

   result_dict = json.load(search_results)

# get since_id & max_id for next stream
   metadata_list = result_dict.get(u'search_metadata')
   statuses_list = result_dict.get(u'statuses')
   ids = [temp[u'id'] for temp in statuses_list]
   temp_id = metadata_list.get(u'max_id_str')
   for temp1 in ids:
        max_id = temp1
   max_id = max_id - 1    
   if (temp_id > since_id) and (max_id > temp_id):
        since_id = temp_id

# extract tweets from stream
   tweets = [x[u'text'] for x in statuses_list]
   for tweet in tweets:
      print tweet
# while loop ends


'''
main function call
'''
if __name__ == '__main__':
  fetchsamples()

