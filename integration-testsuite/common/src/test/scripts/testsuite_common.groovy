
def root = new XmlParser().parse(project.properties['inputFile'])

/**
 * Add a security-domain block like this:
 *
 * <security-domain name="JBossWS" cache-type="default">
 *   <authentication>
 *     <login-module code="UsersRoles" flag="required">
 *       <module-option name="usersProperties" value="/mnt/ssd/jbossws/stack/cxf/trunk/modules/testsuite/cxf-tests/target/test-classes/jbossws-users.properties"/>
 *       <module-option name="unauthenticatedIdentity" value="anonymous"/>
 *       <module-option name="rolesProperties" value="/mnt/ssd/jbossws/stack/cxf/trunk/modules/testsuite/cxf-tests/target/test-classes/jbossws-roles.properties"/>
 *     </login-module>
 *   </authentication>
 * </security-domain>
 *
 */
 
def subsystems = root.profile.subsystem
def securityDomains = null
for (item in subsystems) {
    if (item.name().getNamespaceURI().contains("urn:jboss:domain:security:")) {
       for (element in item) {
           if (element.name().getLocalPart() == 'security-domains') {
              securityDomains = element
           }
       }
       break
    }
}
def securityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWS','cache-type':'default'])
def authentication = securityDomain.appendNode('authentication')
def loginModule = authentication.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
loginModule.appendNode('module-option', ['name':'unauthenticatedIdentity','value':'anonymous'])
loginModule.appendNode('module-option', ['name':'usersProperties','value':project.properties['usersPropFile']])
loginModule.appendNode('module-option', ['name':'rolesProperties','value':project.properties['rolesPropFile']])

/**
 * Save the configuration to a new file
 */
def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(project.properties['outputFile'])
f.write(writer.toString())
