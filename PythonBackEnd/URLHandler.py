from datetime import datetime
import cgi
import urllib

from google.appengine.api import users
from google.appengine.ext import ndb

import webapp2
from models import *

class URLHandler(webapp2.RequestHandler):
    def get(self):
        queryType=self.request.getattr("QueryType")
		
		if queryType == "Ping":
			query = Ping.query()
		elif queryType == "DNSLookup":
			query = DNSLookUp.query()
		elif queryType == "TCPSpeedTest":
			query = TCPSpeedTest.query()
		
		dataSet = query.fetch(10)
		
		for record in dataSet:
			"""
			Do Something
			"""
			
app = webapp2.WSGIApplication([
	("/", URLHandler),], debug = True)
