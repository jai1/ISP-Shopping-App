
import cgi
import urllib

from google.appengine.api import users
from google.appengine.ext import ndb
import webapp2

#Username is the parent
class Ping(ndb.model):
        commonInfo = CommonInfo()
	result = ndb.FloatProperty()

class DNSLookUp(ndb.model):-
        commonInfo = CommonInfo()
	result = ndb.FloatProperty()

# Need to refactor it to store only Result
# and not TotalResult and Duration
class TCPSpeedTest(ndb.model):	
	#Can be an array of values
	totalResult = ndb.FloatProperty(repeated = True) 
	duration = ndb.FloatProperty()
	direction = ndb.StringProperty()
        commonInfo = CommonInfo()

class CommonInfo(ndb.model):
        testNumber = ndb.IntegerProperty()
	latitude = ndb.FloatProperty()
	longitude = ndb.FloatProperty()
	carrier = ndb.StringProperty()
	timeStamp = ndb.DateTimeProperty(auto_now_add = True)
	networkType = ndb.StringProperty()
