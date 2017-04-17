#!/usr/bin/env python

#commandline arguments: "recipient address" "email subject" "email body text"

import smtplib
import sys
import cgi
import urllib
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText

form = cgi.FieldStorage()

fromaddr = 'metagenomicpipeline@gmail.com'
#toaddr  = urllib.unquote_plus(str(form.getvalue('recipient')))
#subject = urllib.unquote_plus(str(form.getvalue('subject')))
#msg = urllib.unquote_plus(str(form.getvalue('body')))
toaddr = sys.argv[1]
subject = sys.argv[2]
msg = sys.argv[3]

body = MIMEMultipart()
body['From'] = fromaddr
body['To'] = toaddr
body['Subject'] = subject
body.attach(MIMEText(msg, 'plain'))
text = body.as_string()

username = 'metagenomicpipeline@gmail.com'
password = 'metagenome'
server = smtplib.SMTP('smtp.gmail.com', 587)
server.ehlo()
server.starttls()
server.login(username,password)
server.sendmail(fromaddr, toaddr, text)
server.quit()

print "Content-type: text/html"
print
print "<html><head>"
print ""
print toaddr
print body
#print "</head><body onload=\"document.location=\'Nominator.html\'\"></body>"
print ""
print "</html>"