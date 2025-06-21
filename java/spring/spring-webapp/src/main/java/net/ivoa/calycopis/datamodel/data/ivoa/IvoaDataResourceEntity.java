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

package net.ivoa.calycopis.datamodel.data.ivoa;

import java.net.URI;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.openapi.model.IvoaIvoaDataResourceBlock;
import net.ivoa.calycopis.openapi.model.IvoaIvoaObsCoreItem;

/**
 * An IvoaDataResource entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "ivoadataresources"
    )
public class IvoaDataResourceEntity
    extends AbstractDataResourceEntity
    implements IvoaDataResource
    {

    /**
     * Protected constructor
     *
     */
    protected IvoaDataResourceEntity()
        {
        super();
        }
    
    /**
     * Protected constructor with parent.
     *
     */
    public IvoaDataResourceEntity(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaIvoaDataResource template)
        {
        super(
            session,
            storage,
            template.getSchedule(),
            template.getName()
            );

        IvoaIvoaDataResourceBlock ivoa = template.getIvoa();
        if (null != ivoa)
            {
            this.ivoid = ivoa.getIvoid();
            this.obsCoreImpl = new ObsCoreImpl(
                ivoa.getObscore()
                ); 
            this.dataLinkImpl = new DataLinkImpl(
                ivoa.getDatalink()
                );
            }
        }

    private URI ivoid;
    @Override
    public URI getIvoid()
        {
        return this.ivoid;
        }

    @Embeddable
    public static class ObsCoreImpl
    implements ObsCore
        {
        public ObsCoreImpl()
            {
            super();
            }

        public ObsCoreImpl(final IvoaIvoaObsCoreItem template)
            {
            super();
            if (null != template)
                {
                this.obsId              = template.getObsId();
                this.obsCollection      = template.getObsCollection();
                this.obsPublisherDid    = template.getObsPublisherDid();
                this.obsCreatorDid      = template.getObsCreatorDid();
                this.obsDataproductType = template.getDataproductType();
                this.obsCalibLevel      = template.getCalibLevel();
                this.obsAccessUrl       = template.getAccessUrl();
                this.obsAccessFormat    = template.getAccessFormat();
                }
            }

        public IvoaIvoaObsCoreItem getIvoaBean()
            {
            IvoaIvoaObsCoreItem bean = new IvoaIvoaObsCoreItem();
            bean.setObsId(this.obsId);
            bean.setObsCollection(this.obsCollection);
            bean.setObsPublisherDid(this.obsPublisherDid);
            bean.setObsCreatorDid(this.obsCreatorDid);
            bean.setDataproductType(this.obsDataproductType);
            bean.setCalibLevel(this.obsCalibLevel);
            bean.setAccessUrl(this.obsAccessUrl);
            bean.setAccessFormat(this.obsAccessFormat);
            return bean ;
            }

        private String obsId;
        @Override
        public String getObsId()
            {
            return this.obsId;
            }

        private String obsCollection;
        @Override
        public String getObsCollection()
            {
            return this.obsCollection;
            }

        private String obsPublisherDid;
        @Override
        public String getObsPublisherDid()
            {
            return this.obsPublisherDid;
            }

        private String obsCreatorDid;
        @Override
        public String getObsCreatorDid()
            {
            return this.obsCreatorDid;
            }

        private String obsDataproductType;
        @Override
        public String getDataproductType()
            {
            return this.obsDataproductType;
            }

        private Integer obsCalibLevel;
        @Override
        public Integer getCalibLevel()
            {
            return this.obsCalibLevel;
            }

        private String obsAccessUrl;
        @Override
        public String getAccessUrl()
            {
            return this.obsAccessUrl;
            }

        private String obsAccessFormat;
        @Override
        public String getAaccessFormat()
            {
            return this.obsAccessFormat;
            }
        }
    
    @Embedded
    private ObsCoreImpl obsCoreImpl ;
    public ObsCoreImpl getObsCore()
        {
        return this.obsCoreImpl;
        }

    @Embeddable
    public static class DataLinkImpl
    implements DataLink
        {

        public DataLinkImpl()
            {
            super();
            }

        public DataLinkImpl(final IvoaIvoaDataLinkItem template)
            {
            super();
            if (null != template)
                {
                this.dataLinkId = template.getID();
                this.dataLinkAccessUrl = template.getAccessUrl();
                this.dataLinkServiceDef = template.getServiceDef();
                this.dataLinkErrorMessage = template.getErrorMessage();
                this.dataLinkDescription = template.getDescription();
                this.dataLinkSemantics = template.getSemantics();
                this.dataLinkContentType = template.getContentType();
                this.dataLinkContentLength = template.getContentLength();
                this.dataLinkContentQualifier = template.getContentQualifier();
                this.dataLinkLocalSemantics = template.getLocalSemantics();
                this.dataLinkLinkAuth = template.getLinkAuth();
                this.dataLinkLinkAuthorized = template.getLinkAuthorized();
                }
            }
        @Override
        public IvoaIvoaDataLinkItem getIvoaBean()
            {
            IvoaIvoaDataLinkItem bean = new IvoaIvoaDataLinkItem();
            bean.setID(this.dataLinkId);
            bean.setAccessUrl(this.dataLinkAccessUrl);
            bean.setServiceDef(this.dataLinkServiceDef);
            bean.setErrorMessage(this.dataLinkErrorMessage);
            bean.setDescription(this.dataLinkDescription);
            bean.setSemantics(this.dataLinkSemantics);
            bean.setContentType(this.dataLinkContentType);
            bean.setContentLength(this.dataLinkContentLength);
            bean.setContentQualifier(this.dataLinkContentQualifier);
            bean.setLocalSemantics(this.dataLinkLocalSemantics);
            bean.setLinkAuth(this.dataLinkLinkAuth);
            bean.setLinkAuthorized(this.dataLinkLinkAuthorized);
            return bean ;
            }

        private String dataLinkId ;
        @Override
        public String getID()
            {
            return this.dataLinkId;
            }

        private String dataLinkAccessUrl;
        @Override
        public String getAccessUrl()
            {
            return this.dataLinkAccessUrl;
            }

        private String dataLinkServiceDef;
        @Override
        public String getServiceDef()
            {
            return this.dataLinkServiceDef;
            }

        private String dataLinkErrorMessage;
        @Override
        public String getErrorMessage()
            {
            return this.dataLinkErrorMessage;
            }

        private String dataLinkDescription;
        @Override
        public String getDescription()
            {
            return this.dataLinkDescription;
            }

        private String dataLinkSemantics;
        @Override
        public String getSemantics()
            {
            return this.dataLinkSemantics;
            }

        private String dataLinkContentType;
        @Override
        public String getContentType()
            {
            return this.dataLinkContentType;
            }

        private Integer dataLinkContentLength;
        @Override
        public Integer getContentLength()
            {
            return this.dataLinkContentLength;
            }

        private String dataLinkContentQualifier;
        @Override
        public String getContentQualifier()
            {
            return this.dataLinkContentQualifier;
            }

        private String dataLinkLocalSemantics;
        @Override
        public String getLocalSemantics()
            {
            return this.dataLinkLocalSemantics;
            }

        private String dataLinkLinkAuth;
        @Override
        public String getLinkAuth()
            {
            return this.dataLinkLinkAuth;
            }

        private String dataLinkLinkAuthorized;
        @Override
        public String getLinkAuthorized()
            {
            return this.dataLinkLinkAuthorized;
            }
        }
    
    @Embedded
    private DataLinkImpl dataLinkImpl ;
    public DataLinkImpl getDataLink()
        {
        return this.dataLinkImpl ;
        }
    
    @Override
    public IvoaAbstractDataResource getIvoaBean()
        {
        return fillBean(
            new IvoaIvoaDataResource(
                IvoaDataResource.TYPE_DISCRIMINATOR
                )
            );
        }

    protected IvoaIvoaDataResource fillBean(final IvoaIvoaDataResource bean)
        {
        super.fillBean(bean);

        IvoaIvoaDataResourceBlock block = new IvoaIvoaDataResourceBlock();
        bean.setIvoa(
            block
            );
        block.setIvoid(
            this.getIvoid()
            );
        if (null != this.getObsCore())
            {
            block.setObscore(
                this.getObsCore().getIvoaBean()
                );
            }
        if (null != this.getDataLink())
            {
            block.setDatalink(
                this.getDataLink().getIvoaBean()
                );
            }
        return bean;
        }
    }

