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
public abstract class ValidatorFactoryBaseImpl<ObjectType, EntityType>
    extends FactoryBaseImpl
    implements Validator<ObjectType, EntityType>
    {

    /**
     * Our list of validators.
     * 
     */
    protected List<Validator<ObjectType, EntityType>> validators = new ArrayList<Validator<ObjectType, EntityType>>();

    /**
     * Save a validated result.
     * 
     */
    public abstract void save(
        final OfferSetRequestParserState state,
        final ObjectType object
        );
    
    /**
     * Report an unknown type.
     *
     */
    public abstract Validator.Result<ObjectType, EntityType> unknownResult(
        final OfferSetRequestParserState state,
        final ObjectType object
        );
    
    /**
     * Report an unknown type.
     *
     */
    public Validator.Result<ObjectType, EntityType> unknownResult(
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
        return new ResultBean<ObjectType, EntityType>(
            ResultEnum.FAILED
            );
        }

    @Override
    public Result<ObjectType, EntityType> validate(
        final ObjectType requested,
        final OfferSetRequestParserState state
        ){
        //
        // Try each of the validators in our list.
        for (Validator<ObjectType, EntityType> validator : validators)
            {
            Result<ObjectType, EntityType> result = validator.validate(
                requested,
                state
                );
            switch(result.getEnum())
                {
                case ResultEnum.ACCEPTED:
                    save(
                        state,
                        result.getObject()
                        );
                    return result ;
                case ResultEnum.FAILED:
                    return result ;
                default:
                    continue;
                }
            }
        //
        // Fail the validation if we didn't find a matching validator.
        return unknownResult(
            state,
            requested
            );
        }
    }
