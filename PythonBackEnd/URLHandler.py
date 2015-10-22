from datetime import datetime
import cgi
import urllib

from google.appengine.api import users
from google.appengine.ext import ndb

import webapp2
from models import *

class URLHandler(webapp2.RequestHandler):
    def get(self):
        print self.request



app = webapp2.WSGIApplication([
	("/", URLHandler),], debug = True)
