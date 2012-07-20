Wise Invoke Services Easily samples

Here you find a set of samples demonstrating how to use Wise in a standalone application.
Any directory, except for 'lib' and 'ant', contains a single example. The directory's name suggests
which kind of test you will find in. Only in case we think an example needs further explanations, you 
will find a local README.txt inside its directory.

lib directory contains library referred by examples. 'ant' directory contains build.xml imported by
all examples' build.xml.

How to run examples?

1. Edit ant/sample.properties
2. Enter in specific example directory
3. Edit resources/META-INF/wise-log4j.xml and change properties according to your environment if needed.
4. Start your JBoss AS 7 instance
5. Type "ant deployTestWS" to deploy server side content (an archive shipping the WS endpoint invoked by the sample)
6. Type "ant runTest" to run the client side example
7. Type "ant undeployTestWS" to undeploy server side content
8. Have a look to the code.

If something changes for a specific example you will find instructions on local README.txt

have fun.
 
