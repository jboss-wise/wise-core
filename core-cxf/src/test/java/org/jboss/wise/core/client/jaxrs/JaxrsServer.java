package org.jboss.wise.core.client.jaxrs;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.testutil.common.AbstractTestServerBase;

public class JaxrsServer extends AbstractTestServerBase{

    protected void run() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(BookStore.class);

        // default life cycle is per-request, change it to singleton
        sf.setResourceProvider(BookStore.class,
				new SingletonResourceProvider(new BookStore()));
        sf.setAddress("http://localhost:9080/");

        sf.create();
	}
    
	public static void main(String[] args) {
        try {
            JaxrsServer s = new JaxrsServer();
        	
			s.start();
	    } catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
	    } finally {
			System.out.println("done!");
	    }
    }

}
