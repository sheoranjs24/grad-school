#!/usr/local/bin

import sys
import string
import json

def main():
    sent_file = open(sys.argv[1])
    tweet_file = open(sys.argv[2])

    states_dict = {'AL':0, 'AK':0, 'AZ':0, 'AR':0, 'CA':0, 'CO':0, 'CT':0, 'DE':0, 'FL':0, 'GA':0, 'HI':0, 'ID':0, 'IL':0, 'IN':0, 'IA':0, 'KS':0, 'KY':0, 'LA':0, 'ME':0, 'MD':0, 'MA':0, 'MI':0, 'MN':0, 'MS':0, 'MO':0, 'MT':0, 'NE':0, 'NV':0, 'NH':0, 'NJ':0, 'NM':0, 'NY':0, 'NC':0, 'ND':0, 'OH':0, 'OK':0, 'OR':0, 'PA':0, 'RI':0, 'SC':0, 'SD':0, 'TN':0, 'TX':0, 'UT':0, 'VT':0, 'VA':0, 'WA':0, 'WV':0, 'WI':0, 'WY':0 } 
    scount_dict = {'AL':0, 'AK':0, 'AZ':0, 'AR':0, 'CA':0, 'CO':0, 'CT':0, 'DE':0, 'FL':0, 'GA':0, 'HI':0, 'ID':0, 'IL':0, 'IN':0, 'IA':0, 'KS':0, 'KY':0, 'LA':0, 'ME':0, 'MD':0, 'MA':0, 'MI':0, 'MN':0, 'MS':0, 'MO':0, 'MT':0, 'NE':0, 'NV':0, 'NH':0, 'NJ':0, 'NM':0, 'NY':0, 'NC':0, 'ND':0, 'OH':0, 'OK':0, 'OR':0, 'PA':0, 'RI':0, 'SC':0, 'SD':0, 'TN':0, 'TX':0, 'UT':0, 'VT':0, 'VA':0, 'WA':0, 'WV':0, 'WI':0, 'WY':0 }

# convert sent_file into dict
    scores = {}
    for line in sent_file:
       term, score  = line.split("\t")  
       scores[term] = int(score)  
 
# manage tweet_file
    for entry in tweet_file:     
       tweet = json.loads(entry)
       twscore = 0.0
       noise = 0
       if (u'text') in tweet:
         if tweet[u'lang'] == u'en' :
           twtext = tweet.get(u'text')
#           print twtext
           twterm = twtext.split(" ")
           for text in twterm:
#               print text
               index1 = string.find(text,'@',0,1)
               index2 = string.find(text,'#',0,1)
               index3 = string.find(string.lower(text),'http',0)
               if index1==0 or index2==0 or index3==0: #(text[0] == '@') or (text[0] == '#') :
                   noise += noise
               else:
                   text = string.lower(text)  
                   text = text.strip(string.punctuation)
                   text = text.replace("\n", "")
                   if text.encode('utf-8') in scores:
#                     print text.encode('utf-8'), scores[text]
        	      twscore = twscore + scores[text] 
           # end of for-text loop

           # find state of tweet
           #1 place
           if not tweet[u'place'] == None:
               place = tweet.get(u'place') #dict  
               if place[u'country_code'] == u'US' or place[u'country'] == u'United States':
#                  print place
                  l = len(place[u'full_name'])
                  twstate = place[u'full_name'][l-2:l]
                  if twstate in states_dict.keys():
                      states_dict[twstate] += twscore
                      scount_dict[twstate] += 1

           #2 coordinates
           #if not tweet[u'coordinates'] == None: #dict
           #    print tweet[u'coordinates']

           #3 user
           elif (u'user') in tweet:
               user = tweet.get(u'user') #dict
               if not user[u'location'] == "":
                    # search for US states abbrivations in location_string
#                    print "|"+user[u'location']+"|"
                    l = len(user[u'location'])
                    if user[u'location'][l-3:l] == "USA":  # "USA" or "NY, USA" or "NY,USA" or "NY ,USA" : "NY , USA"
                       if (l>7 and (user[u'location'][l-8]==' ' or user[u'location'][l-8] ==',')) or l==7 or l==6 :
                          s1 = user[u'location'][l-7:l-5]
                          if s1 in states_dict.keys():
                             states_dict[s1] += twscore
                             scount_dict[s1] += 1
#                             print "s1 = ", s1 
                    elif (l>2 and (user[u'location'][l-3] == ',' or user[u'location'][l-3] == ' ')) or l==2:  # "New York, NY" or "New York,NY" or "NY"
                       s1 = user[u'location'][l-2:l]
                       if s1 in states_dict.keys():
                           states_dict[s1] += twscore
                           scount_dict[s1] += 1
#                           print "s1 = ", s1
                    elif (l>3 and (user[u'location'][l-4] == ',' or user[u'location'][l-4] == ' ')) or l==3: # "New York, NY " or "New York,NY " or "NY "
                           s2 = user[u'location'][l-3:l]
                           s2 = s2.rstrip(" ")
                           if s2 in states_dict.keys():
                               states_dict[s2] += twscore
                               scount_dict[s2] += 1
#                               print "s2 =", s2 
                                
 
    # end of for loop

    # find the happiest state
    for state in states_dict.keys():
         if scount_dict[state] > 0 :
             states_dict[state] = float(states_dict[state]) / scount_dict[state]
    # display happiest state
    for state in sorted(states_dict, key=states_dict.get, reverse=True):
       print state #, states_dict[state] 
       break   

if __name__ == '__main__':
    main()
