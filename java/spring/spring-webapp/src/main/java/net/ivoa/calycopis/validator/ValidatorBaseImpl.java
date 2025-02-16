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
public abstract class ValidatorBaseImpl<ObjectType>
    extends FactoryBaseImpl
    implements Validator<ObjectType>
    {

    /**
     * Our list of validators.
     * 
     */
    protected List<Validator<ObjectType>> validators = new ArrayList<Validator<ObjectType>>();

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
    public abstract Validator.ResultSet<ObjectType> unknown(
        final OfferSetRequestParserState state,
        final ObjectType object
        );
    
    /**
     * Report an unknown type.
     *
     */
    public Validator.ResultSet<ObjectType> unknown(
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
        return new ResultSetBean<ObjectType>(
            ResultEnum.FAILED
            );
        }

    @Override
    public ResultSet<ObjectType> validate(
        final ObjectType requested,
        final OfferSetRequestParserState state
        ){
        //
        // Try each of the validators in our list.
        for (Validator<ObjectType> validator : validators)
            {
            ResultSet<ObjectType> result = validator.validate(
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
        return unknown(
            state,
            requested
            );
        }
    }
