/**
 * 
 */
package net.ivoa.calycopis.functional.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * Base class for validator factories.
 * 
 */
public abstract class ValidatorFactoryBaseImpl<ObjectType, EntityType extends ComponentEntity>
    extends FactoryBaseImpl
    implements Validator<ObjectType, EntityType>
    {

    /**
     * Our list of validators.
     * 
     */
    protected List<Validator<ObjectType, EntityType>> validators = new ArrayList<Validator<ObjectType, EntityType>>();

    /**
     * Report an unknown type.
     *
     */
    public abstract void unknown(
        final OfferSetRequestParserContext context,
        final ObjectType object
        );
    
    /**
     * Report an unknown type.
     *
     */
    public void unknown(
        final OfferSetRequestParserContext context,
        final URI    typeName,
        final String className
        ){
        context.getOfferSetEntity().addWarning(
            "uri:unknown-type",
            "Unknown type [${type}][${class}]",
            Map.of(
                "type",
                typeName.toString(),
                "class",
                className
                )
            );
        context.valid(false);
        }

    @Override
    public Validator.Result<ObjectType, EntityType> validate(
        final ObjectType requested,
        final OfferSetRequestParserContext context
        ){
        //
        // Try each of the validators in our list.
        for (Validator<ObjectType, EntityType> validator : validators)
            {
            Result<ObjectType, EntityType> result = validator.validate(
                requested,
                context
                );
            switch(result.getEnum())
                {
                case ResultEnum.ACCEPTED:
                    return result ;
                case ResultEnum.FAILED:
                    return result ;
                default:
                    continue;
                }
            }
        //
        // Fail the validation if we didn't find a matching validator.
        unknown(
            context,
            requested
            );
        return new ResultBean<ObjectType, EntityType>(
            Validator.ResultEnum.FAILED
            );
        }
    }
