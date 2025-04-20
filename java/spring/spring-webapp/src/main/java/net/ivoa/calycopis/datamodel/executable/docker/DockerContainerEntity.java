/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 *
 */

package net.ivoa.calycopis.datamodel.executable.docker;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.execution.TestExecutionStepEntity;
import net.ivoa.calycopis.functional.execution.TestExecutionStepEntityFactory;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaDockerContainer;
import net.ivoa.calycopis.openapi.model.IvoaDockerExternalPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.openapi.model.IvoaDockerInternalPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerNetworkPort;
import net.ivoa.calycopis.openapi.model.IvoaDockerNetworkSpec;
import net.ivoa.calycopis.openapi.model.IvoaDockerPlatformSpec;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * JPA Entity for DockerContainer executables.
 *
 */
@Slf4j
@Entity
@Table(
    name = "dockerexecutables"
    )
@DiscriminatorValue(
    value = "uri:docker-executable"
    )
public class DockerContainerEntity
    extends AbstractExecutableEntity
    implements DockerContainer
    {

    protected DockerContainerEntity()
        {
        super();
        }

    protected DockerContainerEntity(
        final ExecutionSessionEntity parent,
        final IvoaDockerContainer template
        ){
        super(
            parent,
            template.getName()
            );

        this.privileged = template.getPrivileged();
        this.entrypoint = template.getEntrypoint();        

        this.image = new ImageImpl(
            template.getImage()
            );
        this.environment = new HashMap<String, String>();
        this.environment.putAll(
            template.getEnvironment()
            );
        
        IvoaDockerNetworkSpec ivoaNetwork = template.getNetwork() ; 
        if (ivoaNetwork != null)
            {
            for (IvoaDockerNetworkPort port : ivoaNetwork.getPorts())
                {
                this.networkPorts.add(
                    new DockerNetworkPortEntity(
                        this,
                        port
                        )
                    );                
                }
            }
        }

    /**
     * Build the prepare and release steps.
     *
     */
    protected void configure(final TestExecutionStepEntityFactory factory)
        {
        getPrepareList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 001"
                )
            );

        getPrepareList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 002"
                )
            );

        getPrepareList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 003"
                )
            );

        getPrepareList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 004"
                )
            );

        getReleaseList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 005"
                )
            );

        getReleaseList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 006"
                )
            );

        getReleaseList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 007"
                )
            );

        getReleaseList().addStep(
            factory.create(
                this.getParent(),
                this,
                Duration.ofSeconds(10),
                Duration.ofSeconds(10),
                "Step 008"
                )
            );
        }
    
    @Override
    public IvoaAbstractExecutable getIvoaBean(final String baseurl)
        {
        IvoaDockerContainer bean = new IvoaDockerContainer(
            DockerContainer.TYPE_DISCRIMINATOR
            );
        bean.setUuid(
                this.getUuid()
                );
        bean.setName(
            this.getName()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        bean.setPrivileged(
            this.privileged
            );
        bean.setEntrypoint(
            this.entrypoint
            );

        if ((this.environment != null) && (this.environment.isEmpty() == false))
            {
            bean.setEnvironment(
                this.environment
                );
            }

        if (this.image != null)
            {
            IvoaDockerImageSpec ivoaImage = new IvoaDockerImageSpec();
            
            if ((this.image.getLocations() != null) && (this.image.getLocations().isEmpty() == false))
                {
                ivoaImage.setLocations(
                    this.image.getLocations()
                    );
                }
            ivoaImage.setDigest(
                this.image.getDigest()
                );

            if (this.image.getPlatform() != null)
                {
                IvoaDockerPlatformSpec ivoaPlatform = new IvoaDockerPlatformSpec();
                ivoaPlatform.setArchitecture(
                    this.image.getPlatform().getArchitecture()
                    );
                ivoaPlatform.setOs(
                    this.image.getPlatform().getOs()
                    );
                ivoaImage.setPlatform(
                    ivoaPlatform
                    );
                }
            bean.setImage(
                ivoaImage
                );
            }

        if ((this.networkPorts != null) && (this.networkPorts.isEmpty() == false))
            {
            log.debug("Network ports [{}]", this.networkPorts);
            
            IvoaDockerNetworkSpec ivoaNetworkSpec = new IvoaDockerNetworkSpec();

            for (DockerNetworkPortEntity networkPort : this.networkPorts)
                {
                IvoaDockerNetworkPort ivoaNetworkPort = new IvoaDockerNetworkPort();
                ivoaNetworkPort.setAccess(
                    networkPort.getAccess()
                    );
                ivoaNetworkPort.setPath(
                    networkPort.getPath()
                    );
                ivoaNetworkPort.setProtocol(
                    networkPort.getProtocol()
                    );

                if (networkPort.getInternal() != null)
                    {
                    IvoaDockerInternalPort ivoaInternalPort = new IvoaDockerInternalPort();
                    ivoaInternalPort.setPort(
                        networkPort.getInternal().getPort()
                        );
                    ivoaNetworkPort.setInternal(
                        ivoaInternalPort
                        );
                    }

                if (networkPort.getExternal() != null)
                    {
                    IvoaDockerExternalPort ivoaExternalPort = new IvoaDockerExternalPort();
                    ivoaExternalPort.setPort(
                        networkPort.getExternal().getPort()
                        );
                    for (String address : networkPort.getExternal().getAddresses())
                        {
                        ivoaExternalPort.addAddressesItem(
                            address
                            );
                        }
                    ivoaNetworkPort.setExternal(
                        ivoaExternalPort
                        );
                    }
                ivoaNetworkSpec.addPortsItem(
                    ivoaNetworkPort
                    );
                }
            bean.setNetwork(
                ivoaNetworkSpec
                );
            }
        
        // TODO generate the access URLs
        
        return bean;
        }

    @Embeddable
    public static class ImageImpl
    implements Image
        {

        public ImageImpl()
            {
            super();
            }

        public ImageImpl(final IvoaDockerImageSpec template)
            {
            super();
            if (template != null)
                {
                this.digest = template.getDigest();
                if (template.getLocations() != null)
                    {
                    this.locations = new ArrayList<String>();
                    this.locations.addAll(
                        template.getLocations()
                        );
                    }
                if (template.getPlatform() != null)
                    {
                    this.platformArch = template.getPlatform().getArchitecture();
                    this.platformOs   = template.getPlatform().getOs();
                    }
                }
            }

        private String digest;
        @Override
        public String getDigest()
            {
            return digest;
            }

        @ElementCollection
        @CollectionTable(
            name="dockerimagelocations",
            joinColumns=@JoinColumn(
                name="parent",
                referencedColumnName = "uuid"
                )
            )
        private List<String> locations;
        @Override
        public List<String> getLocations()
            {
            return locations;
            }

        private String platformArch;
        private String platformOs;
        @Override
        public Platform getPlatform()
            {
            return new Platform()
                {
                @Override
                public String getArchitecture()
                    {
                    return platformArch;
                    }

                @Override
                public String getOs()
                    {
                    return platformOs;
                    }
                };
            }
        }
    
    @Embedded
    private ImageImpl image ;
    @Override
    public ImageImpl getImage()
        {
        return image;
        }

    private boolean privileged;
    @Override
    public boolean getPrivileged()
        {
        return privileged;
        }

    private String entrypoint;
    @Override
    public String getEntrypoint()
        {
        return entrypoint;
        }

    @ElementCollection
    @Column(name="envvalue")
    @MapKeyColumn(name="envkey")
    @CollectionTable(
        name="dockerenvironmentvariables",
        joinColumns=@JoinColumn(
            name="parent",
            referencedColumnName = "uuid"
            )
        )
    private Map<String, String> environment;
    @Override
    public Map<String, String> getEnvironment()
        {
        return this.environment;
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<DockerNetworkPortEntity> networkPorts = new ArrayList<DockerNetworkPortEntity>();

    @Override
    public Network getNetwork()
        {
        return new Network()
            {
            public List<NetworkPort> getPorts()
                {
                return new ListWrapper<NetworkPort, DockerNetworkPortEntity>(
                    networkPorts
                    ){
                    public NetworkPort wrap(final DockerNetworkPortEntity inner)
                        {
                        return (NetworkPort) inner ;
                        }
                    };
                }
            };
        }
    }
