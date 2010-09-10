JAX-RS Client API Demo 
======================

The demo shows how to use WISE JAX-RS client API to invoke JAX-RS services. 


Prerequisites
-------------

Apache CXF 2.2 snapshot. WISE JAX-RS client API can work with any JAX-RS (JSR-311)
compliant implementations. This sample chooses Apache CXF for the purpose of
demostration.


Building and running the demo using Ant
---------------------------------------

1. Download and install Apache CXF 2.2 snapshot. 
2. Start CXF JAX-RS server from <CXF-installation-dir>\samples\jax_rs\basic directory.
3. From the base directory of this sample (i.e., where this README file is
located), the Ant build.xml file can be used to build and run the demo. 
The client target automatically build the demo.

Using either UNIX or Windows:

  ant runTest
    

To remove the .class files generated, run "ant clean".


What Happened
-------------
Examine JaxrsClient.java, compare it with the client codes used by CXF jax_rs\basic to 
understand how WISE JAX-RS client API can simplify the invocation of JAX-RS services. 