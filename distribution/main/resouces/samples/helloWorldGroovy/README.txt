Groovy include in its botloader directory ($GROOVY_HOME/lib) xpp.jar XML Pull Parser. 
It conflicts with xercesImpl.jar needed by jbossws and more general by jaxws. If it is happenning you would get this exception:

Caught: java.lang.LinkageError: loader constraint violation: loader (instance of <bootloader>) previously initiated loading for a different type with name "javax/xml/namespace/QName"
java.lang.LinkageError: loader constraint violation: loader (instance of <bootloader>) previously initiated loading for a different type with name "javax/xml/namespace/QName"

To solve the problem you have to remove or rename this file before use wise directly within an interpreted script.
Of course it isn't a problem if you compile script with groovyc since it will compile it in java bytecode
and class loading will depend only by jvm used to launch the compiled application.

Then run runGroovyJDK6.sh and have fun. 
If you want to see groovy's closures in action with Wise run runGroovyClosure.sh

If you would use this example compiling groovy script in bytecode and then run please type "ant compileGroovy"
before type "ant runTest". And please note this example have 2 different script (i.e. compiled 
classes that can act as Main). Select which one you would run editing locale build.xml file.

Have fun.
