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

package net.ivoa.calycopis.datamodel.resource.data.skao;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.resource.data.ivoa.IvoaDataResourceEntity;
import net.ivoa.calycopis.datamodel.resource.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSkaoChecksumItem;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaSkaoDataResourceBlock.ObjecttypeEnum;
import net.ivoa.calycopis.openapi.model.IvoaSkaoReplicaItem;
import net.ivoa.calycopis.util.ListWrapper;

/**
 * An IvoaDataResource entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "skaodataresources"
    )
public class SkaoDataResourceEntity
    extends IvoaDataResourceEntity
    implements SkaoDataResource
    {

    /**
     * Protected constructor
     *
     */
    protected SkaoDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public SkaoDataResourceEntity(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaSkaoDataResource template)
        {
        super(
            session,
            storage,
            template
            );

        IvoaSkaoDataResourceBlock block = template.getSkao();
        if (null != block)
            {
            this.namespace  = block.getNamespace();
            this.objectName   = block.getObjectname();
            this.objectType = block.getObjecttype();
            this.dataSize   = block.getDatasize();
            IvoaSkaoChecksumItem checksum = block.getChecksum();
            if (null != checksum)
                {
                this.checksumType  = checksum.getType();
                this.checksumValue = checksum.getValue();
                }
            for (IvoaSkaoReplicaItem replica : block.getReplicas())
                {
                this.replicas.add(
                    new ReplicaImpl(
                        replica
                        )
                    );
                }
            
            }
        }

    private String namespace;
    @Override
    public String getNamespace()
        {
        return this.namespace;
        }

    private String objectName;
    @Override
    public String getObjectid()
        {
        return this.objectName;
        }

    private ObjecttypeEnum objectType;
    @Override
    public ObjecttypeEnum getObjectType()
        {
        return this.objectType;
        }

    private Long dataSize;
    @Override
    public Long getDataSize()
        {
        return this.dataSize;
        }

    private String checksumType;
    @Override
    public String getChecksumType()
        {
        return this.checksumType;
        }

    private String checksumValue;
    @Override
    public String getChecksumValue()
        {
        return this.checksumValue;
        }

    protected IvoaSkaoChecksumItem getChecksumBean()
        {
        IvoaSkaoChecksumItem bean = new IvoaSkaoChecksumItem();
        bean.setType(
            this.checksumType
            );
        bean.setValue(
            this.checksumValue
            );
        return bean ;
        }
    
    @Embeddable
    public static class ReplicaImpl
    implements Replica
        {
        protected ReplicaImpl()
            {
            super();
            }

        protected ReplicaImpl(IvoaSkaoReplicaItem replica)
            {
            super();
            if (null != replica)
                {
                this.rseName = replica.getRsename();
                this.dataUrl = replica.getDataurl();
                }
            }
        
        private String rseName;
        @Override
        public String getRseName()
            {
            return this.rseName;
            }

        private String dataUrl;
        @Override
        public String getDataUrl()
            {
            return this.dataUrl;
            }
        
        public IvoaSkaoReplicaItem getIvaoBean()
            {
            IvoaSkaoReplicaItem bean = new IvoaSkaoReplicaItem();
            bean.setRsename(this.rseName);
            bean.setDataurl(this.dataUrl);
            return bean;
            }
        }

    @ElementCollection
    @CollectionTable(
        name="skaodataresourcereplicas",
        joinColumns=@JoinColumn(
            name="parent"
            )
        )
    private List<ReplicaImpl> replicas = new ArrayList<ReplicaImpl>();
    public List<Replica> getReplicas()
        {
        return new ListWrapper<Replica, ReplicaImpl>(
            replicas
            ){
            public Replica wrap(final ReplicaImpl inner)
                {
                return (Replica) inner ;
                }
            };
        }

    
    @Override
    public IvoaAbstractDataResource getIvoaBean()
        {
        return fillBean(
            new IvoaSkaoDataResource(
                SkaoDataResource.TYPE_DISCRIMINATOR
                )
            );
        }

    protected IvoaSkaoDataResource fillBean(final IvoaSkaoDataResource bean)
        {
        super.fillBean(
            bean
            );
        IvoaSkaoDataResourceBlock block = new IvoaSkaoDataResourceBlock();
        bean.setSkao(
            block
            );
        block.setNamespace(
            this.namespace
            );
        block.setObjectname(
            this.objectName
            );
        block.setObjecttype(
            this.objectType
            );
        block.setDatasize(
            this.dataSize
            );
        block.setChecksum(
            this.getChecksumBean()
            );
        for (ReplicaImpl replica : this.replicas)
            {
            block.addReplicasItem(
                replica.getIvaoBean()
                );
            }
        return bean ;
        }
    }

