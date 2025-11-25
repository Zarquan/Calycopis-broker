/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker;

import java.net.URI;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutable;

/**
 *
 */
public interface DockerContainer
    extends AbstractExecutable
    {
    /**
     * The OpenAPI type identifier.
     *
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0") ;

    public Image getImage();
    
    public boolean getPrivileged();

    public String getEntrypoint();
    
    public Map<String, String> getEnvironment();
    
    public Network getNetwork();    
    
    public static interface Image
        {
        public String getDigest();

        public List<String> getLocations();
        
        public Platform getPlatform();
        }

    public static interface Platform
        {
        public String getArchitecture();
    
        public String getOs();
        }

    public interface Network    
        {
        public List<NetworkPort> getPorts();
        }

    public interface NetworkPort
        {
        public boolean getAccess();

        public InternalPort getInternal();
        
        public ExternalPort getExternal();
        
        public String getProtocol();
        
        public String getPath();
        }
    
    public interface InternalPort
        {
        public Integer getPort();
        }
    
    public interface ExternalPort
        {
        public Integer getPort();
        
        public List<String> getAddresses();
        }
    }
