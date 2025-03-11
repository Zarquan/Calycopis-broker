/**
 * 
 */
package net.ivoa.calycopis.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.factory.FactoryBaseImpl;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;

/**
 * Base class for validator factories.
 * 
 */
public abstract class ValidatorFactoryBaseImpl<ObjectType, ParentType, EntityType>
    extends FactoryBaseImpl
    implements Validator<ObjectType, ParentType, EntityType>
    {

    /**
     * Our list of validators.
     * 
     */
    protected List<Validator<ObjectType, ParentType, EntityType>> validators = new ArrayList<Validator<ObjectType, ParentType, EntityType>>();

    /**
     * Save a validated object in the parser state.
     * 
    public abstract void save(
        final OfferSetRequestParserState state,
        final ObjectType object
        );
     */
    
    /**
     * Create a result with just the enum.
     * 
    public Validator.Result<ObjectType, ParentType, EntityType> result(
        final ResultEnum value
        ){
        return result(
            value,
            null
            );
        }
     */

    /**
     * Create a result with enum and object.
     * 
    public abstract Validator.Result<ObjectType, ParentType, EntityType> result(
        final ResultEnum value,
        final ObjectType object
        );
     */

    /**
     * Report an unknown type.
     *
     */
    public abstract void unknown(
        final OfferSetRequestParserState state,
        final ObjectType object
        );
    
    /**
     * Report an unknown type.
     *
     */
    public void unknown(
        final OfferSetRequestParserState state,
        final String typeName,
        final String className
        ){
        state.getOfferSetEntity().addWarning(
            "uri:unknown-type",
            "Unknown type [${type}][${class}]",
            Map.of(
                "type",
                typeName,
                "class",
                className
                )
            );
        state.valid(false);
        }

    @Override
    public Validator.Result<ObjectType, ParentType, EntityType> validate(
        final ObjectType requested,
        final OfferSetRequestParserState state
        ){
        //
        // Try each of the validators in our list.
        for (Validator<ObjectType, ParentType, EntityType> validator : validators)
            {
            Result<ObjectType, ParentType, EntityType> result = validator.validate(
                requested,
                state
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
            state,
            requested
            );
        return new ResultBean<ObjectType, ParentType, EntityType>(
            Validator.ResultEnum.FAILED
            );
        }
    }
