import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.exception.WiseRuntimeException;


WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
            .wsdlURL("http://127.0.0.1:8080/HelloWorldGroovy/HelloWorldWS?wsdl").build();
WSMethod method = client.getWSMethod("HelloWorldWSService", "HelloWorldPort", "sayHello");
HashMap<String, Object> requestMap = new HashMap<String, Object>();
requestMap.put("toWhom", "SpiderMan");
InvocationResult result = method.invoke(requestMap, null);
println(result.getMappedResult(null));
client.close();
