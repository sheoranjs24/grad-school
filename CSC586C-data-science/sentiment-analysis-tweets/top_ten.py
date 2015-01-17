
import sys 
import string
import json

def main():
    tweet_file = open(sys.argv[1])
    tag_dict = {}

# find hashtags from tweets
    for entry in tweet_file:
        tweet = json.loads(entry)
        if (u'entities') in tweet:
            entities = tweet.get(u'entities')
            if (u'hashtags') in entities:
                hashtags = entities.get(u'hashtags')
                for item in hashtags:                                  
#                  print item
                   text = item.get(u'text')
                   if text in tag_dict.keys():  
                        tag_dict[text] += 1
                   else:
                        tag_dict[text] = 1

# find the top-ten hashtags
    count = 1
    for tag in sorted(tag_dict, key=tag_dict.get, reverse=True):
       if count==11:
          break
       print tag, tag_dict[tag]
       count += 1     

               

if __name__ == '__main__':
    main()
