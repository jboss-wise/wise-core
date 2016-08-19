package org.jboss.wise.core.i18n;

import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;

/**
 * User: rsearls
 * Date: 8/17/16
 */
@SuppressWarnings("deprecation")
@MessageBundle(projectCode = "WCORE")
public interface Messages {
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);

    @Message(id = 84000, value = "unable to remove tmpDir %s")
    String unableToRemoveTmpdir(String s);

    @Message(id = 84001, value = "Error Description")
    String errorDescription();

    @Message(id = 84002, value = "Cannot resolve [publicID= %s ,systemID= %s ]")
    String cannotResolveIds(String pId, String sId);

    @Message(id = 84003, value = "Cannot load publicId from resource: %s")
    String cannotLoadPublicid(String filename);

    @Message(id = 84004, value = "Cannot load systemId from resource: %s")
    String cannotLoadSystemid(String filename);

    @Message(id = 84005, value = "Trying to resolve systemId as a non-file URL: %s")
    String tryingToResolveSystemId(String systemId);

    @Message(id = 84006, value = "Failed to open url stream")
    String failedToOpenUrlStream();

    @Message(id = 84007, value = "Cannot load systemId as URL: %s")
    String cannotLoadSystemIdAsURL(String systemId);

    @Message(id = 84008, value = "Cannot resolve entity: [pub= %s ,sysid= %s]")
    String cannotResolveEntityIds(String publicId, String systemId);

    @Message(id = 84009, value = "Trying to resolve id as a non-file URL: %s")
    String tryingToResolveIdAsNonFileURL(String id);

    @Message(id = 84010, value = "Cannot load id as URL: %s")
    String cannotLoadIdAsURL(String id);

    @Message(id = 84011, value = "WSDL saved to: %s")
    String wsdlSavedTo(String name);

    @Message(id = 84012, value = "Cannot save wsdl to: %s")
    String cannotSaveWsdlTo(String name);

    @Message(id = 84013, value = "Getting wsdl definition from: %s")
    String gettingWsdlDefinitionFrom(String name);

    @Message(id = 84014, value = "Processing wsdl import: %s")
    String processingWsdlImport(String name);

    @Message(id = 84015, value = "targetFile: %s")
    String targetFile(String name);

    @Message(id = 84016, value = "WSDL import saved to: %s")
    String wsdlImportSaveTo(String name);

    @Message(id = 84017, value = "Processing schema import: %s")
    String processngSchemaImport(String name);

    @Message(id = 84018, value = "XMLSchema import saved to: %s")
    String xmlSchemaImportSavedTo(String name);

    @Message(id = 84019, value = "Not a valid URL: %s")
    String notValidURL(String name);

    @Message(id = 84020, value = "Null category!")
    String nullCategory();

    @Message(id = 84021, value = "Null priority!")
    String nullPriority();

    @Message(id = 84022, value = "The stream has been closed.")
    String streamHasBeenClosed();

    @Message(id = 84023, value = "Outbound message:")
    String outboundMessage();

    @Message(id = 84024, value = "Inbound message:")
    String inboundMessage();

    @Message(id = 84025, value = "Exception in handler: ")
    String exceptionInHandler();

    @Message(id = 84026, value = "Wise will continue without it")
    String wiseWillContinueWithoutIt();

    @Message(id = 84027, value = "Error during loading/instanciating Html report generator (%s) with exception message: %s")
    String errorDuringLoading(String smooksReport, String msg);

    @Message(id = 84028, value = "Failed to load metro wsimport to generate jaxws classes for wsdl %s")
    String failedToLoadMetroWsimport(String wsdlURL);

    @Message(id = 84029, value = "Not found the metro jar files, plese check the metroLibPath setting")
    String notFoundTheMetroJarFiles();

    @Message(id = 84030, value = "Failed to getURL from the metro jar file ")
    String failedToGetURLFromTheMetroJarFiles();

    @Message(id = 84031, value = "Metro home is not set.")
    String metroHomeIsNotSet();

    //############################# core-cxf ######################

    @Message(id = 84032, value = "done!")
    String msgDone();

    @Message(id = 84033, value = "Failed to invoke WSDLToJava")
    String failedToInvokeWSDLToJava();

    @Message(id = 84034, value = "Could not make directory: %s")
    String couldNotMakeDirectory(String name);

    @Message(id = 84035, value = "TODO! Check SOAP 1.2 extension")
    String checkSoap12Extension();

    @Message(id = 84036, value = "Unsupported target, using default value '2.2'")
    String unsupportedTarget();
}