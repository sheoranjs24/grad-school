#!/usr/local/bin

import sys
import string
import json


def main():
    sent_file = open(sys.argv[1])
    tweet_file = open(sys.argv[2])

# convert sent_file into list
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
               # remove noise from tweet
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
        	      twscore = twscore + scores[text] 
           # end of for-text loop        
       print twscore      
    # end of for loop

if __name__ == '__main__':
    main()
