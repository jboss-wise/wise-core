Wise Invoke Services Easily samples

Here you find a set of samples demonstrating how to use Wise in a standalone application.
Any directory, except for 'lib' and 'ant', contains a single example. The directory's name suggests
which kind of test you will find in. Only in case we think an example needs further explanations, you 
will find a local README.txt inside its directory.

lib directory contains library referred by examples. ant directory contains build.xml imported from
all examples' build.xml.

How to run examples?

1. Enter in specific example directory ;)
2. Edit resources/META-INF/jboss-beans.xml and change properties according to your environment (i.e defaultTmpDeployDir) if needed.
3. Edit resources/META-INF/wise-log4j.xml and change properties according to your environment if needed.
4. Edit build.properties changing "JBossHome" and "ServerConfig" property to point to your JBossAS instance
5. Start your JBossAS instance (of course it has to provide JBossWS)
6. type "ant deployTestWS" to deploy server side content (aka the ws against example will run)
7. type "ant runTest" to run the client side example
8. type "ant undeployTestWS" to undeploy server side content
9. Have a look to the code.

If something changes for a specific example you will find instructions on local README.txt

have fun.
 
