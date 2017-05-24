package com.validation.manager.core.active.directory;

/**
 * This class handles authentication against active directory. Based on code
 * from:
 *
 * https://mhimu.wordpress.com/2009/03/18/active-directory-authentication-using-javajndi/
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.openide.util.Exceptions;

public class ADAuthenticator {

    private final String domain;
    private final String ldapHost;
    private final String searchBase;
    private final String filter;

    public ADAuthenticator() {
        this.domain = VMSettingServer.getSetting("ad.domain").getStringVal();
        this.ldapHost = "ldap://" + VMSettingServer.getSetting("ad.controller")
                .getStringVal();
        this.searchBase = VMSettingServer.getSetting("ad.root")
                .getStringVal();//e.g. dc=abbl,dc=org
        this.filter = VMSettingServer.getSetting("ad.filter")
                .getStringVal();//e.g. (&(objectClass=user)(sAMAccountName=%u)
    }

    public ADAuthenticator(String domain, String host, String dn, String filter) {
        this.domain = domain;
        this.ldapHost = host;
        this.searchBase = dn;
        this.filter = filter;
    }

    public Map<String, Object> authenticate(String user, String pass) {
        //Check if Active DIrectory autehntication is active.
        if (VMSettingServer.getSetting("ad.enabled").getBoolVal()) {
            String returnedAtts[] = {"sn", "cn", "mail"};
            String searchFilter = filter.replaceAll("%u", user);

            //Create the search controls
            SearchControls searchCtls = new SearchControls();
            searchCtls.setReturningAttributes(returnedAtts);

            //Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapHost);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);
            env.put(Context.SECURITY_CREDENTIALS, pass);

            LdapContext ctxGC;
            try {
                ctxGC = new InitialLdapContext(env, null);
                //Search objects in GC using filters
                NamingEnumeration answer = ctxGC.search(searchBase, searchFilter,
                        searchCtls);
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult) answer.next();
                    Attributes attrs = sr.getAttributes();
                    Map amap = null;
                    if (attrs != null) {
                        amap = new HashMap<>();
                        NamingEnumeration ne = attrs.getAll();
                        while (ne.hasMore()) {
                            Attribute attr = (Attribute) ne.next();
                            amap.put(attr.getID(), attr.get());
                        }
                        ne.close();
                    }
                    return amap;
                }
            }
            catch (NamingException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
}
