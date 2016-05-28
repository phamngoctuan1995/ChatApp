from urllib2 import *
import urllib
import json
import sys

MY_API_KEY="AIzaSyCaat0tQ2bUwU5PtqkIHAr1tc8XY_dJlaw"

messageTitle = "First push notification"
messageBody = "Enjoy"

data={
    "to" : "/topics/my_little_topic",
    "notification" : {
        "body" : messageBody,
        "title" : messageTitle,
        "icon" : "ic_notification"
    }
}

dataAsJSON = json.dumps(data)

request = Request(
    "https://gcm-http.googleapis.com/gcm/send",
    dataAsJSON,
    { "Authorization" : "key="+MY_API_KEY,
      "Content-type" : "application/json"
    }
)

print urlopen(request).read()