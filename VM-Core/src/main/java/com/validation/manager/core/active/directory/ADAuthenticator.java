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
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.openide.util.Exceptions;

public class ADAuthenticator {

    private final String domain;
    private final String ldapHost;
    private final String searchBase;

    public ADAuthenticator() {
        this.domain = VMSettingServer.getSetting("ad.domain").getStringVal();
        this.ldapHost = "ldap://" + VMSettingServer.getSetting("ad.controller")
                .getStringVal();
        this.searchBase = VMSettingServer.getSetting("ad.root")
                .getStringVal();//e.g. dc=abbl,dc=org
    }

    public ADAuthenticator(String domain, String host, String dn) {
        this.domain = domain;
        this.ldapHost = host;
        this.searchBase = dn;
    }

    public Map authenticate(String user, String pass) {
        //Check if Active DIrectory autehntication is active.
        if (VMSettingServer.getSetting("ad.enabled").getBoolVal()) {
            String returnedAtts[] = {"sn", "givenName", "mail"};
            String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";

            //Create the search controls
            SearchControls searchCtls = new SearchControls();
            searchCtls.setReturningAttributes(returnedAtts);

            //Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
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
                        amap = new HashMap();
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

    /**
     * Find Active Directory Server
     *
     *
     * @param domain Domain to search into
     * @return LDAP Host
     * @throws javax.naming.NamingException
     */
    public static String getADServer(String domain)
            throws NamingException {
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        DirContext ctx = new InitialDirContext(env);
        Attributes attrs = ctx.getAttributes("_ldap._tcp.dc._msdcs."
                + domain, new String[]{"SRV"});
        String record = (String) attrs.get("SRV").get();
        String[] s = record.split(" ");
        return s[s.length - 1].substring(0, s[s.length - 1].length() - 1);
    }
}
