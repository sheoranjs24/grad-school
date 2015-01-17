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

# manage tweet_file & save score of new terms.
    term_dict = {}
    term_count = {}
    for entry in tweet_file:     
       tweet = json.loads(entry)
       twscore = 0 
       noise = 0 
       if (u'text') in tweet:
         if tweet[u'lang'] == u'en' :
           twtext = tweet.get(u'text')
           twterm = twtext.split(" ")
           term_list = []
           for text in twterm:
#               print text
               # remove noise from tweet
               index1 = string.find(text,'@',0,1)
               index2 = string.find(text,'#',0,1)
               index3 = string.find(string.lower(text),'http',0)
               if index1==0 or index2==0 or index3==0: 
                   noise += noise
               else:
                   text = string.lower(text)  #unicode string
                   text = text.strip(string.punctuation)
                   text = text.replace("\n", "")
                   text = text.replace(" ", "")
                   # find tweet score
                   if text in scores:
                      twscore = twscore + scores[text]
                   else:    #word is new term
                      term_list.append(text)
                      if text in term_count.keys():
                          term_count[text] += 1
                      else:
                          term_count[text] = 1   #count
                          term_dict[text] = 0    #score
           # end of for-text loop        
    
           # add new scores of term to dict       
           for ttext in term_list:    
#               print ttext.encode('utf-8'), twscore
               term_dict[ttext] = term_dict[ttext] + twscore             
    # end of for-entry loop

# Calculate score of new terms  
    for tentry in term_dict.keys():  
       term_score = 0.0
       term_score = float(term_dict[tentry]) / term_count[tentry]
       #term_score = "%.3f" %term_score
       # Print new terms & scores
       if tentry != "":           
          print tentry.encode('utf-8'), str(term_score)


if __name__ == '__main__':
    main()
