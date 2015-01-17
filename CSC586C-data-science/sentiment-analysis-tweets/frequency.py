import sys 
import string
import json

def main():
    tweet_file = open(sys.argv[1])
    term_count = 0
    term_dict = {}
    noise = 0

    for entry in tweet_file:
       tweet = json.loads(entry)
       if (u'text') in tweet:
         if tweet[u'lang'] == u'en' :
            twtext = tweet.get(u'text')
            twterm = twtext.split(" ")
            for term in twterm:
#               print text
               # remove noise from tweet
               index1 = string.find(term,'@',0,1)
               index2 = string.find(term,'#',0,1)
               index3 = string.find(string.lower(term),'http',0)
               if index1==0 or index2==0 or index3==0: 
                   noise += noise
               else:
                   term = string.lower(term)  #unicode string
                   term = term.strip(string.punctuation)
                   term = term.replace("\n", "") 
                   term = term.replace(" ", "")
                   term_count += 1 
                   # count terms
                   if term in term_dict.keys():
                       term_dict[term] += 1
                   else:
                       term_dict[term] = 1
            # for-term loop ends
    # for-entry loop ends

# calculate frequency of terms
    print "term_count=", term_count
    for tentry in term_dict.keys():
#        print "term=", tentry.encode('utf-8'), "count=", term_dict[tentry]
        frequency = float(term_dict[tentry]) / term_count
#        frequency = "%.5f" %frequency
        print tentry.encode('utf-8'), str(frequency)

if __name__ == '__main__':
    main()
