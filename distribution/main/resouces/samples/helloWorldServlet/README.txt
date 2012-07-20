This example is little different from others since it demonstrates the use of Wise in a web application.

This sample needs JBoss Application Server 7. 

To run this sample you have simply to call "ant deployTestWS" which will deploy a war with the "server side" webservice.
Then call "ant deployServlet" to deploy a war containing the "client" servlet and all needed libraries. To call the servlet point your browser to 

http://localhost:8080/HelloWorld-servlet/HelloWorldServlet?NAME=superman 

Then change the name of you preferred super hero ;)

You can undeploy the "client" servlet using "ant undeployServlet".

Have fun.
