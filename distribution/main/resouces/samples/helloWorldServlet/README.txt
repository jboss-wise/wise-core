This example is little different from others since it demonstrate the use of wise in a web application.

This sample needs a jboss-4.x application server or at least a servlet container. 

To run this sample you have simply to call ant deployTestWS which will deploy a war with the "server side" webservice.
Then call "ant deployServlet" to deploy a war containing the "client" servlet and all needed libraries. To call the servlet point your browser to 

http://localhost:8080/HelloWorld-servlet/HelloWorldServlet?NAME=superman 

Then change the name of you preferred super hero ;)

Please note that Wise client servlet can't stay on the same war of the "server side" webserver due to a conflict in library used by jbossws to provide webservice facilities and ones used to invoke tools.
Have fun.
