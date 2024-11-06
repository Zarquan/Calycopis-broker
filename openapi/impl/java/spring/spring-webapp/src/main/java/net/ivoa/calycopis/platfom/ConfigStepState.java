/**
 * 
 */
package net.ivoa.calycopis.platfom;

/**
 * Enumeration of states for ConfigSteps.
 * 
 */
public enum ConfigStepState
    {
    
    PENDING(),
    ALLOCATING(),
    READY(),
    RELEASING(),
    COMPLETED(),
    CANCELLED(),
    FAILED();
    
    }
